package ast;

import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import parser.FOOLParser.ExpContext;
import parser.FOOLParser.FunContext;
import parser.FOOLParser.VardecContext;
import util.Environment;
import util.SemanticError;

public class DotCallNode implements Node {

	private String idName = "";
	private String id = "";
	private STentry m;
	private FunContext funFound = null;
	private FoolVisitorImpl app = new FoolVisitorImpl();
	private Node fun;
	private ArrayList<Node> args = new ArrayList<Node>();
	private Environment envSaved;
	private HashMap<String, String> subclass;
	private String classname;
	private int offsetID = -10000;
	private int nestingLevel;
	private ClassTypeNode classNode = null;
	private Node exp = null;
	private String returnType = null;
	private boolean thisExp;
	private STentry entry;
	STentry tmp1 = null;

	public DotCallNode(String c, String i, ArrayList<Node> a) {
		idName = c;
		id = i;
		args = a;

	}

	@Override
	public String toPrint(String indent) {
		return "Inside dotnode: " + idName + "." + id + "\n";
	}

	FunContext getFunFound() {
		return funFound;
	}

	String getReturnType() {
		return returnType;
	}

	@Override
	public Node typeCheck() {
		int counter = 0; // contatore per scandire i parametri attuali della funzione
		// Controllo dei parametri della funzione
		if (idName.equals("this")) {
			// Chiamata di un metodo con "this"
			ArrowTypeNode t = null;
			if (entry.getType() instanceof ArrowTypeNode)
				t = (ArrowTypeNode) entry.getType();
			else {
				System.out.println("Invocation of a non-function " + id);
				System.exit(0);
			}
			ArrayList<Node> p = t.getParList();
			// Controllo nr parametri
			if (!(p.size() == args.size())) {
				System.out.println("Wrong number of parameters in the invocation of " + id);
				System.exit(0);
			}
			for (int i = 0; i < args.size(); i++) {
				// Se i parametri sono classi, controllo che il tipo dei parametri attuali sia
				// un sottotipo dei parametri formali
				if (((args.get(i)).typeCheck() instanceof ClassTypeNode) && (p.get(i) instanceof ClassTypeNode)) {
					if (!(FOOLlib.isSubtype(((ClassTypeNode) p.get(i)).getTypeID(),
							((ClassTypeNode) (args.get(i)).typeCheck()).getTypeID(), envSaved.subClassTable))) {
						System.out.println("Wrong type for " + (i + 1) + "-th parameter in the invocation of " + id);
						System.exit(0);
					} else {
						// Setto il tipo a quello del parametro attuale
						((ClassTypeNode) p.get(i)).setTypeId(((ClassTypeNode) (args.get(i)).typeCheck()).getTypeID());
					}
				} else {
					// I parametri non sono classi
					if (!(FOOLlib.isSubtype((args.get(i)).typeCheck(), p.get(i)))) {
						System.out.println("Wrong type for " + (i + 1) + "-th parameter in the invocation of " + id);
						System.exit(0);
					}
				}
			}
			return t.getRet();
		} else {

			// Non siamo dentro un "this"

			ArrowTypeNode t = null;
			ArrayList<Node> p = null;
			// System.out.println(envSaved.virtualsTables.get(classname).toString());
			// Vado a cercare nella virtual table il metodo chiamato (tramite l'id della
			// classe a cui appartiene)
			if (envSaved.virtualsTables.get(classname).get(funFound.ID().getText())
					.getType() instanceof ArrowTypeNode) {
				t = (ArrowTypeNode) envSaved.virtualsTables.get(classname).get(id).getType();
				p = t.getParList();
			}
			for (VardecContext c : funFound.vardec()) {
				// Se il parametro attuale Ã¨ una classe, controllo se il parametro formale Ã¨
				// sottotipo del parametro attuale
				if (args.get(counter).typeCheck() instanceof ClassTypeNode) {
					ClassTypeNode c1 = (ClassTypeNode) args.get(counter).typeCheck();
					//
					System.out.println(c.type().getText() + " " + c1.getTypeID());
					if (!(FOOLlib.isSubtype(c.type().getText(), c1.getTypeID(), subclass))) {
						System.out.println("Wrong type for " + counter + "th parameter in the invocation of "
								+ funFound.ID().getText());
						System.exit(0);
					} else {
						((ClassTypeNode) p.get(counter)).setTypeId(c1.getTypeID());
					}
				}
				// Se il parametro formale è un int, deve esserlo anche il parametro attuale
				else if (args.get(counter).typeCheck() instanceof IntTypeNode) {
					if (!(c.type().getText().equals("int"))) {
						System.out.println("Wrong type for " + counter + "th parameter in the invocation of "
								+ funFound.ID().getText());
						System.exit(0);
					}
				}
				// Se il parametro formale è un bool, deve esserlo anche il parametro attuale
				else if (args.get(counter).typeCheck() instanceof BoolTypeNode) {
					if (!(c.type().getText().equals("bool"))) {
						System.out.println("Wrong type for " + counter + "th parameter in the invocation of "
								+ funFound.ID().getText());
						System.exit(0);
					}
				}
				counter++;
			}
			// restituisco il tipo della funzione
			if (funFound.type().getText().equals("int"))
				return new IntTypeNode();
			else if (funFound.type().getText().equals("bool"))
				return new BoolTypeNode();
			else {

				returnType = ((ClassTypeNode) exp.typeCheck()).getTypeID();
				return exp.typeCheck();
			}
		}
	}

	@Override
	public String codeGeneration() {

		int funcNumber = 0;
		String getAR = "";
		for (int i = 0; i < nestingLevel - 1; i++)
			getAR += "lw\n";

		String parlist = "";
		for (int i = args.size() - 1; i >= 0; i--)
			parlist += args.get(i).codeGeneration();
		String parlistNew;
		parlistNew = "";

		for (int i = (envSaved.symTableL0.get(classNode.getTypeID()).getListContext().size()) - 1; i >= 0; i--) {

			if (!idName.equals("this")) {
				// inserisco i campi della classe sullo stack
				parlistNew += "lfp\n" + "push " + offsetID + "\n" + "add\n" + "lw\n" + "push " + i + "\n" + "add\n"
						+ "lw\n";
			} else {

				parlistNew += "lfp\n" + "push 1\n" + "add\n" + // to jump CL
						"push " + i + "\n" + "add\n" + "lw\n";

			}
		}

		return "lfp\n" + // CL
				parlist + parlistNew + // "halt\n"+
				"lfp\n" + getAR +
				// prendo la funzione giusta dalla dispatch table in caso di overriding
				"push " + envSaved.dispatchTable.get(classNode.getTypeID()).get(id) + "\n" + "js\n";

	}

	private int searchInSuper(String prec, String superc, String id2) {
		if (envSaved.virtualsTables.get(superc).get(id2) != null)
			return envSaved.virtualsTables.get(superc).get(id).getOffset();

		else
			return searchInSuper(superc, envSaved.symTableL0.get(superc).getType1().getSuper(), id2);
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		FoolVisitorImpl app = new FoolVisitorImpl();
		boolean ok = false;
		boolean applicable = true;
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		HashMap<String, STentry> hm = env.symTable.get(env.nestingLevel);
		HashMap<String, STentry> hm1 = env.symTable.get(0);
		int j = env.nestingLevel;
		STentry tmp = null;
		while (j >= 1 && tmp == null)
			tmp = (env.symTable.get(j--)).get(id);

		if ((idName.equals("this")) && (env.insideClass == null)) {
			// Uso this non essendo in una classe
			res.add(new SemanticError("Cannot use THIS in such context"));
		} else if (idName.equals("this")) {

			// ricorsivamente risalendo la catena
			classname = env.insideClass;
			ok = exists(hm1, hm, env.insideClass);
			if (ok) {
				if (tmp == null) {
					// se tmp è null, la funzione esiste ma non l'ho ancora visitata
					// serve per poter chiamare delle funzioni che vengono dichiarate dopo la
					// chiamata nel codice
					exp = app.visitFun(funFound);
					HashMap<String, STentry> sytSaved = env.symTable.get(env.nestingLevel);
					env.symTable.remove(env.nestingLevel);
					int before = env.nestingLevel;
					env.nestingLevel = 1;// nestinglevel dei metodi appartenti al this
					res.addAll(exp.checkSemantics(env));
					env.symTable.add(sytSaved);
					env.nestingLevel = before;
					env.toADD.add(exp);
					this.entry = ((FunNode) exp).getEntry();

				}

				else {
					// trovo la funzione
					ArrayList<Node> l = null;
					int before = env.nestingLevel;
					exp = app.visitFun(funFound);
					env.skip = true;
					exp.checkSemantics(env);
					env.skip = false;
					env.nestingLevel = before;
					this.entry = ((FunNode) exp).getEntry();
				}
			}
		} else {
			// non siamo in un this
			int k = env.nestingLevel;
			if (env.nestingLevel > 1) {

				while (k >= 1 && tmp1 == null)
					tmp1 = (env.symTable.get(k--)).get(idName);
				if (tmp1 == null) {
					res.add(new SemanticError("Id " + idName + " not declared"));
				} else {
					hm = env.symTable.get(k + 1);
				}
			}
			if ((hm.get(idName) == null) && (tmp1 == null)) {
				res.add(new SemanticError("Id " + idName + " not declared"));
			} else {
				if (tmp1 != null) {
					m = tmp1;
				} else
					m = hm.get(idName);
				offsetID = m.getOffset();
				Node app3 = m.getType();
				if (app3 instanceof ClassTypeNode) {
					// Prendo nome della classe a cui appartiene il metodo
					classNode = (ClassTypeNode) app3;
					classname = classNode.getTypeID();
					// ricorsivamente risalendo la catena
					ok = exists(hm1, hm, classname);

					if (ok) {

						if (env.insideClass == null) {
							int before = env.offset;
							exp = app.visitFun(funFound);
							exp.checkSemantics(env);
							env.offset = before;
							// env.toADD=exp;
						} else if (tmp != null) {
							int before = env.offset;
							exp = app.visitFun(funFound);
							exp.checkSemantics(env);
							env.offset = before;
						} else {

							exp = app.visitFun(funFound);
							HashMap<String, STentry> sytSaved = env.symTable.get(env.nestingLevel);
							env.symTable.remove(env.nestingLevel);
							int before = env.nestingLevel;
							env.nestingLevel = 1;// nestinglevel dei metodi appartenti al this
							res.addAll(exp.checkSemantics(env));
							env.symTable.add(sytSaved);
							env.nestingLevel = before;
							env.toADD.add(exp);
							this.entry = ((FunNode) exp).getEntry();
						}
					}
				} else {
					applicable = false;
					;
				}
			}
		}
		if (!applicable) {
			res.add(new SemanticError("Dot is not applicable for primitive type"));
		} else if ((!ok) && (res.size() == 0))
			res.add(new SemanticError("Method " + id + " does not exist for class " + classname));
		else if ((res.size() == 0) && (args.size() != funFound.vardec().size())) {
			res.add(new SemanticError("Wrong number of parameters in the invocation of " + id));
		}

		for (int i = 0; i < args.size(); i++)
			args.get(i).checkSemantics(env);
		envSaved = env;
		nestingLevel = env.nestingLevel;
		subclass = env.subClassTable;

		return res;

	}

	// Controlla se esiste l'id del metodo dentro la classe data (risalendo la
	// catena di superclassi)
	private boolean exists(HashMap<String, STentry> hm1, HashMap<String, STentry> hm, String classname) {

		if (classname == null)
			return false;
		else {
			m = hm1.get(classname);
			for (FunContext m1 : m.getFunContext()) {

				if (m1.ID().getText().equals(id)) {
					funFound = m1;
					classNode = m.getType1(); // getType1 restituisce un ClassTypeNode

					hm.remove(id);
					return true;
				}
			}

			return exists(hm1, hm, m.getType1().getSuper());
		}
	}
}
