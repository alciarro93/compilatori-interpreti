package ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lib.FOOLlib;
import parser.FOOLParser.ClassdecContext;
import parser.FOOLParser.FunContext;
import parser.FOOLParser.VardecContext;
import util.Environment;
import util.SemanticError;

public class IdNode implements Node {

	private String id;
	private STentry entry;
	private int nestinglevel;
	private String idSuper = null;
	private int nasting;
	private VardecContext contextIdFound = null;
	private Environment envSaved;
	private String insideClass = null;

	public IdNode(String i) {
		id = i;
	}

	public STentry getEntry() {
		return entry;
	}

	public String toPrint(String s) {
		if (s == null) {
			System.out.println("id nulll");
		}
		return s + "Id:" + id;// + " at nestlev " + nestinglevel +"\n" + entry.toPrint(s+" ") ;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {

		// create result list
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		int j = env.nestingLevel;
		STentry tmp = null;
		if (env.insidefunction) {
			while (j >= 1 && tmp == null)
				tmp = (env.symTable.get(j--)).get(id);
		} else {
			while (j >= 0 && tmp == null)
				tmp = (env.symTable.get(j--)).get(id);
		}
		boolean found = false;
		if (tmp == null) {
			// Non ho trovato l'id nella symbol table
			if (env.insideClass != null) {
				// Se sono dentro una classe, controllo le dichiarazioni della superclasse
				if (exists(env.symTable.get(0), env.symTable.get(0).get(env.insideClass).getType1().getSuper())) {
					found = true;
				}
				if (found) {
					System.out.println("idsuper=: " + idSuper);
				} else {
					// Id non dichiarato neanche nella superclasse

					System.out.println(env.symTable.get(2).toString());
					res.add(new SemanticError("Id " + id + " not declared"));
				}
			} else {

				// Non sono dentro una class, id non è stato dichiarato
				res.add(new SemanticError("Id " + id + " not declared"));
			}

		} else {
			// tmp non è null, id è stato dichiarato
			entry = tmp;
			nestinglevel = env.nestingLevel;
		}
		if (found) {
			if (contextIdFound.type().getText().equals("int"))
				entry = new STentry(nasting, new IntTypeNode(), nasting);
			else if (contextIdFound.type().getText().equals("bool"))
				entry = new STentry(nasting, new BoolTypeNode(), nasting);
			else
				entry = new STentry(nasting, new ClassTypeNode(contextIdFound.type().getText()), nasting);

			nestinglevel = env.nestingLevel;
		}
		envSaved = env;
		insideClass = env.insideClass;
		return res;
	}

	public Node typeCheck() {

		if (idSuper == null) {
			if (entry.getType() instanceof ArrowTypeNode) { //
				System.out.println("Wrong usage of function identifier");
				System.exit(0);
			}
			return entry.getType();
		} else {
			if (contextIdFound.type().getText().equals("int"))
				return new IntTypeNode();
			else if (contextIdFound.type().getText().equals("bool"))
				return new BoolTypeNode();
			else
				return new ClassTypeNode(contextIdFound.type().getText());
		}
	}

	public String codeGeneration() {
		String getAR = "";
		if (insideClass != null) {
			nestinglevel--;
		}
		for (int i = 0; i < nestinglevel - entry.getNestinglevel(); i++)
			getAR += "lw\n";

		return "push " + entry.getOffset() + "\n" + // metto offset sullo stack
				"lfp\n" + getAR + // risalgo la catena statica
				"add\n" + "lw\n"; // carico sullo stack il valore all'indirizzo ottenuto*/

	}

	private boolean exists(HashMap<String, STentry> hm1, String classname) {

		if (classname == null)
			return false;
		else {
			STentry m;
			m = hm1.get(classname);
			for (VardecContext m1 : m.getListContext()) {

				if (m1.ID().getText().equals(id)) {
					idSuper = m1.ID().getText();
					contextIdFound = m1;
					nasting = m.getNestinglevel();
					return true;
				}
			}

			return exists(hm1, m.getType1().getSuper());
		}
	}
}
