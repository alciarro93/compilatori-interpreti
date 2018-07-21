package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import parser.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.TerminalNode;

import parser.FOOLParser.BaseExpContext;
import parser.FOOLParser.BoolValContext;
import parser.FOOLParser.DecContext;
import parser.FOOLParser.ExpContext;
import parser.FOOLParser.FactorContext;
import parser.FOOLParser.FunContext;
import parser.FOOLParser.FunExpContext;
import parser.FOOLParser.IfExpContext;
import parser.FOOLParser.IntValContext;
import parser.FOOLParser.LetInExpContext;
import parser.FOOLParser.ClassExpContext;
import parser.FOOLParser.ClassdecContext;
import parser.FOOLParser.SingleExpContext;
import parser.FOOLParser.TermContext;
import parser.FOOLParser.TypeContext;
import parser.FOOLParser.VarExpContext;
import parser.FOOLParser.VarasmContext;
import parser.FOOLParser.VardecContext;
import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

public class ClassExpNode implements Node {

	private ArrayList<ClassdecContext> contexts;
	private Node exp;
	private Node declist;
	private ArrayList<Node> let;
	// variabile utilizzata per richiamare le visite sui vari nodi
	private FoolVisitorImpl m = new FoolVisitorImpl();
	private ArrayList<ArrayList<Node>> funListNode = new ArrayList<ArrayList<Node>>();
	private ArrayList<Node> decListNode = new ArrayList<Node>();
	private HashMap<String, STentry> symTableL0;
	private HashMap<String, String> subclass;
	private HashMap<Pair<String, String>, Pair<String, String>> subFuncTable = new HashMap<Pair<String, String>, Pair<String, String>>();
	private Environment envSaved;
	private Node print;
	private ArrayList<String> checked = new ArrayList<String>();
	private HashMap<String, HashMap<String, STentry>> virtualsTables = new HashMap<String, HashMap<String, STentry>>();

	public ClassExpNode(ArrayList<ClassdecContext> d, Node e, ArrayList<Node> p, Node p1) {
		contexts = d;
		exp = e;
		let = p;
		print = p1;
	}

	public String toPrint(String s) {
		String declstr = "";
		int counterclass = 1;
		for (ClassdecContext entr : contexts) {
			declstr += "\nClass number: " + counterclass;
			counterclass++;

			declstr += "\nDecList2.0:\n " + m.visitClassdec(entr).toPrint(s + "  ");

			Node fun3 = null;
			// function and let
			for (FunContext fun1 : entr.fun()) {
				fun3 = m.visitFun(fun1);
				declstr += "\nFuncList2.0:\n " + fun3.toPrint(s + "  ");
			}
		}
		return s + "PROG WITH CLASS:\n" + declstr + exp.toPrint(s + "  ");
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {

		// declare resulting list

		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		HashMap<String, STentry> hm = new HashMap<String, STentry>();

		env.nestingLevel++;
		env.symTable.add(hm);
		env.offset = -2;
		int classoffset = env.offset;
		ClassTypeNode classType = null;
		List<FunContext> funSuper = null;
		List<FunContext> funSub = null;
		HashMap<String, List<FunContext>> funSubMap = new HashMap<String, List<FunContext>>();
		HashMap<String, List<VardecContext>> varSubMap = new HashMap<String, List<VardecContext>>();
		List<VardecContext> varSuper = null;
		List<VardecContext> varSub = null;
		for (ClassdecContext entr : contexts) {

			if ((entr.IMPLEMENTS() != null) && (hm.get(entr.ID(1).getText()) == null)) {
				// La classe da estendere non esiste
				res.add(new SemanticError(
						"Class " + entr.ID(0).getText() + " implements non-existing class: " + entr.ID(1).getText()));
				break;
			} else if ((entr.IMPLEMENTS() != null) && (hm.get(entr.ID(1).getText()) != null)) {
				// La classe da estendere esiste
				funSub = entr.fun(); // Salvo i metodi della sottoclasse
				// prendo i metodi della superclasse
				funSuper = hm.get(entr.ID(1).getText()).getFunContext();
				Collections.reverse(funSuper);
				for (FunContext f : funSuper) {
					if (!containsID(f.ID().getText(), funSub))
						// Se l'id del metodo non è già  contenuto in funSub, lo aggiungo
						funSub.add(0, f);
				}
				funSubMap.put(entr.ID(0).getText(), funSub);
				// Ripeto il procedimento per i campi

				varSuper = hm.get(entr.ID(1).getText()).getListContext();
				varSub = entr.vardec();
				int numberOfpar = varSub.size();
				List<VardecContext> varDeclaration = new ArrayList<VardecContext>();
				varDeclaration.addAll(varSub);
				Collections.reverse(varSuper);
				for (VardecContext v : varSuper) {
					if (!containsIDvar(v.ID().getText(), varSub)) {
						varSub.add(0, v);
					}
				}
				varSubMap.put(entr.ID(0).getText(), varSub);

				// Creo il ClassTypeNode con relative info
				classType = new ClassTypeNode(entr.ID(0).getText(), varSub, funSub, entr.ID(1).getText(), numberOfpar,
						varDeclaration);

				// Salvo la coppia (sottoclasse,superclasse) nella subClassTable
				env.subClassTable.put(entr.ID(0).getText(), entr.ID(1).getText());
			} else {
				// La classe non estende nessun'altra classe
				funSuper = null;
				classType = new ClassTypeNode(entr.ID(0).getText(), entr.vardec(), entr.fun(), null,
						entr.vardec().size(), entr.vardec());
			}
			// Creo l'entry per la symbol table
			STentry entry;
			if (funSuper != null)
				// La classe Ã¨ una sottoclasse
				entry = new STentry(env.nestingLevel, classType, classoffset, varSub, funSub);
			else if (entr.fun() != null) {
				// La classe non Ã¨ sottoclasse di nessuno
				entry = new STentry(env.nestingLevel, classType, classoffset, entr.vardec(), entr.fun());
			} else
				// La classe non ha metodi
				entry = new STentry(env.nestingLevel, classType, classoffset, entr.vardec(), null);

			if (hm.put(entr.ID(0).getText(), entry) != null) {
				res.add(new SemanticError("Class " + entr.ID(0).getText() + " already declared"));
			}
			symTableL0 = env.symTable.get(env.nestingLevel);
			env.symTableL0 = symTableL0;

		}
		for (ClassdecContext entr : contexts) {
			// Setto le variabili dell'environment necessarie
			env.insideClass = entr.ID(0).getText();
			// Entro nello scope della classe, incremento il livello di
			// annidamento e aggiungo un hashmap vuota all'inizio della lista
			env.nestingLevel++;
			HashMap<String, STentry> hm1 = new HashMap<String, STentry>();
			env.symTable.add(hm1);
			env.offset = 1;

			env.varSub = varSubMap.get(entr.ID(0).getText());
			// Faccio la visita e il controllo della semantica delle
			// dichiarazioni della classe
			Node app = m.visitClassdec(entr);
			res.addAll(app.checkSemantics(env));

			decListNode.add(app);
			env.offset = -1;
			// FUNCTION CHECK SEMANTIC
			Node fun3 = null;
			ArrayList<Node> funListNodeApp = new ArrayList<Node>();

			if (funSubMap.get(entr.ID(0).getText()) == null) {

				for (int i = 0; i < entr.fun().size(); i++) {
					fun3 = m.visitFun(entr.fun().get(i));
					if (!contains(funListNodeApp, fun3)) {
						env.listFunc = entr.fun();
						env.toADD = new ArrayList<Node>();
						res.addAll(fun3.checkSemantics(env));
						// funSub=env.listFunc;
						env.listFunc = null;
						if (env.toADD != null) {
							funListNodeApp.addAll(env.toADD);
							env.toADD = null;
							virtualsTables.put(entr.ID(0).getText(), hm1);
							env.virtualsTables = virtualsTables;
						}
						virtualsTables.put(entr.ID(0).getText(), hm1);
						env.virtualsTables = virtualsTables;
						funListNodeApp.add(fun3);
					}

					if (res.size() > 0) {
						break;
					}
				}

				if (res.size() > 0) {
					break;
				}
			} else {

				for (int i = 0; i < funSubMap.get(entr.ID(0).getText()).size(); i++) {
					fun3 = m.visitFun(funSubMap.get(entr.ID(0).getText()).get(i));
					if (!contains(funListNodeApp, fun3)) {
						env.listFunc = funSubMap.get(entr.ID(0).getText());
						env.toADD = new ArrayList<Node>();
						res.addAll(fun3.checkSemantics(env));
						env.listFunc = null;
						if (env.toADD != null) {
							funListNodeApp.addAll(env.toADD);
							env.toADD = null;
							virtualsTables.put(entr.ID(0).getText(), hm1);
							env.virtualsTables = virtualsTables;
						}
						funListNodeApp.add(fun3);
						virtualsTables.put(entr.ID(0).getText(), hm1);
						env.virtualsTables = virtualsTables;
					}

					if (res.size() > 0) {
						break;
					}
				}

				if (res.size() > 0) {
					break;
				}
			}
			funListNode.add(funListNodeApp);
			// CREAZIONE VIRTUAL TABLE (NOME CLASSE(TIPO) <---> (NOME
			// FUNZIONE-OFFSET))

			virtualsTables.put(entr.ID(0).getText(), hm1);
			env.virtualsTables = virtualsTables;
			// Esco dallo scope della classe, rimuovo prima hashmap della lista
			// e decremento livello di annidamento
			env.symTable.remove(env.nestingLevel);
			env.nestingLevel--;
			env.insideClass = null;
		}
		// return the result
		if (res.size() == 0) {
			// Se non c'Ã¨ nessun errore di semantica, procedo con i controlli
			// sull'eventuale let e l'exp finale
			env.nestingLevel++;
			HashMap<String, STentry> hm5 = new HashMap<String, STentry>();
			env.symTable.add(hm5);
			env.offset = classoffset;
			if (let != null)
				for (Node n : let)
					res.addAll(n.checkSemantics(env));

			env.insidefunction = false;
			if (print != null) {
				res.addAll(print.checkSemantics(env));
			} else {
				res.addAll(exp.checkSemantics(env));
			}
			env.insidefunction = true;
			// Esco dallo scope
			env.symTable.remove(env.nestingLevel);
			env.nestingLevel--;
			// implements/extend control
			// symTableL0 = env.symTable.get(env.nestingLevel);
			subclass = env.subClassTable;
			env.symTable.remove(env.nestingLevel);
			// env.symTableL0 = symTableL0;
			envSaved = env;
		}
		return res;
	}

	private boolean contains(ArrayList<Node> funListNode2, Node fun3) {
		for (Node t : funListNode2) {
			if (((FunNode) t).getID().equals(((FunNode) fun3).getID()))
				return true;
		}

		return false;
	}

	// Prende in input un id (stringa) e una lista di FunContext, restituisce TRUE
	// se l'id è contenuto nella lista di FunContext, FALSE altrimenti
	private boolean containsID(String text, List<FunContext> funSub) {
		for (FunContext f : funSub) {
			if (text.equals(f.ID().getText()))
				return true;
		}
		return false;
	}

	// Prende in input un id (stringa) e una lista di VardecContext, restituisce
	// TRUE se l'id è contenuto nella lista di VardecContext, FALSE altrimenti
	private boolean containsIDvar(String text, List<VardecContext> varSub) {
		for (VardecContext f : varSub) {
			if (text.equals(f.ID().getText()))
				return true;
		}
		return false;
	}

	public Node typeCheck() {

		int counter = 0;
		int counter2 = 0;
		for (ClassdecContext entr : contexts) {
			decListNode.get(counter2).typeCheck();
			counter2++;
		}

		for (ArrayList<Node> t1 : funListNode) {
			for (Node t : t1)
				t.typeCheck();
		}

		if (let != null) {
			for (Node n : let)
				n.typeCheck();
		}
		System.out.println(exp.toPrint(""));
		if (print != null) {
			return print.typeCheck();
		} else {
			return exp.typeCheck();
		}

	}

	public String codeGeneration() {
		String dec = "";
		String afterDec = "";
		String classCode = "";
		String actual = "";
		int counter = 0;
		String classl = "";
		HashMap<String, HashMap<String, String>> dispatchTable = new HashMap<String, HashMap<String, String>>();
		for (ArrayList<Node> t1 : funListNode) {

			for (Node t : t1) {
				classCode = (t.codeGeneration());
				counter++;
				afterDec += classCode;
			}
		}

		if (let != null) {
			for (Node n : let)
				dec = dec + n.codeGeneration();
		}

		if (print == null)
			return "push 0\n" + dec + exp.codeGeneration() + "halt\n" + FOOLlib.getCode();
		else {
			return "push 0\n" + dec + print.codeGeneration() + "halt\n" + FOOLlib.getCode();
		}
	}

}