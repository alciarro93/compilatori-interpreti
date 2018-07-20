package ast;

import java.util.ArrayList;
import java.util.HashMap;

import parser.FOOLParser.FunContext;
import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class CallNode implements Node {

	private String id;
	private STentry entry;
	private ArrayList<Node> parlist;
	private int nestinglevel;
	private Environment envSaved = null;
	private boolean thisContext = false;
	private String insideClass = null;

	public CallNode(String i, STentry e, ArrayList<Node> p, int nl) {
		id = i;
		entry = e;
		parlist = p;
		nestinglevel = nl;
	}

	public CallNode(String text, ArrayList<Node> args) {
		id = text;
		parlist = args;
	}

	public String toPrint(String s) { //
		String parlstr = "";
		for (Node par : parlist)
			parlstr += par.toPrint(s + "  ");
		return s + "Call:" + id + "\n"
		// +entry.toPrint(s+" ")
				+ parlstr;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// create the result
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		int j = env.nestingLevel;
		STentry tmp = null;
		if (env.insideClass == null) {
			while (j >= 0 && tmp == null)
				tmp = (env.symTable.get(j--)).get(id);
		} else {

			while (j >= 1 && tmp == null) {

				tmp = (env.symTable.get(j--)).get(id);
			}

			insideClass = env.insideClass;

			if (j + 1 <= 1)
				thisContext = true;

			env.thisContext = thisContext;
		}
		boolean go = false;
		if (tmp == null) {
			if (env.listFunc != null) {
				int counter = 0;
				for (FunContext f : env.listFunc) {
					if (f.ID().getText().equals(id)) {
						FoolVisitorImpl m = new FoolVisitorImpl();

						Node fun = m.visitFun(f);
						int before = env.nestingLevel;
						HashMap<String, STentry> sytSaved = env.symTable.get(env.nestingLevel);
						env.symTable.remove(env.nestingLevel);
						env.nestingLevel = 1;
						res.addAll(fun.checkSemantics(env));
						this.entry = env.symTable.get(1).get(id);
						env.nestingLevel = before;
						env.symTable.add(sytSaved);
						env.toADD.add(fun);
						// env.listFunc.remove(counter);
						go = true;
						break;

					}
					counter++;
				}
			}
			if (!go)
				res.add(new SemanticError("Id " + id + " not declared"));

		} else {
			this.entry = tmp;
			go = true;
		}
		if (go) {

			this.nestinglevel = env.nestingLevel;

			for (Node arg : parlist)
				res.addAll(arg.checkSemantics(env));
		}
		envSaved = env;
		return res;
	}

	public Node typeCheck() { //
		ArrowTypeNode t = null;

		if (entry.getType() instanceof ArrowTypeNode)
			t = (ArrowTypeNode) entry.getType();
		else {
			System.out.println("Invocation of a non-function " + id);
			System.exit(0);
		}
		ArrayList<Node> p = t.getParList();
		if (!(p.size() == parlist.size())) {
			System.out.println("Wrong number of parameters in the invocation of " + id);
			System.exit(0);
		}
		for (int i = 0; i < parlist.size(); i++) {
			if (((parlist.get(i)).typeCheck() instanceof ClassTypeNode) && (p.get(i) instanceof ClassTypeNode)) {
				if (!(FOOLlib.isSubtype(((ClassTypeNode) p.get(i)).getTypeID(),
						((ClassTypeNode) (parlist.get(i)).typeCheck()).getTypeID(), envSaved.subClassTable))) {
					System.out.println("Wrong type for " + (i + 1) + "-th parameter in the invocation of " + id);
					System.exit(0);
				} else {
					((ClassTypeNode) p.get(i)).setTypeId(((ClassTypeNode) (parlist.get(i)).typeCheck()).getTypeID());
				}
			} else {
				if (!(FOOLlib.isSubtype((parlist.get(i)).typeCheck(), p.get(i)))) {
					System.out.println("Wrong type for " + (i + 1) + "-th parameter in the invocation of " + id);
					System.exit(0);
				}
			}
		}
		return t.getRet();
	}

	public String codeGeneration() {
		String parCode = "";

		int offset = entry.getOffset();
		int nestinglevel1 = entry.getNestinglevel();
		for (int i = parlist.size() - 1; i >= 0; i--)
			parCode += parlist.get(i).codeGeneration();

		String getAR = "";
		for (int i = 0; i < nestinglevel - nestinglevel1; i++)
			getAR += "lw\n";
		String parlistNew = "";

		if (!thisContext) {
			return "lfp\n" + // CL
					parCode + "lfp\n" + getAR + // setto AL risalendo la catena statica
					// ora recupero l'indirizzo a cui saltare e lo metto sullo stack
					"push " + offset + "\n" + // metto offset sullo stack
					"lfp\n" + getAR + // risalgo la catena statica
					"add\n" + "lw\n" + // carico sullo stack il valore all'indirizzo ottenuto
					"js\n";
		} else {
			// in this context inside class cannot be null

			for (int i = (envSaved.symTableL0.get(insideClass).getListContext().size()) - 1; i >= 0; i--) {
				int index = i + parlist.size() - 1;
				parlistNew += "lfp\n" + "push 1\n" + "add\n" + // to jump CL
						"push " + i + "\n" + "add\n" + "lw\n";
			}

			return "lfp\n" + // CL
					parCode + parlistNew + // Recupero assegnamenti per il this
					"lfp\n" + getAR + "push " + envSaved.dispatchTable.get(insideClass).get(id) + "\n" + "js\n";
		}
	}

}