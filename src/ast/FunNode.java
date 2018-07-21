package ast;

import java.util.ArrayList;
import java.util.HashMap;

import parser.FOOLParser.FunContext;
import parser.FOOLParser.VardecContext;
import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class FunNode implements Node {

	private String id;
	private Node type;
	private ArrayList<Node> parlist = new ArrayList<Node>();
	private ArrayList<Node> declist;
	private Node body;
	private String insideClass;
	private Environment envSaved;
	private STentry entry;
	private int nestingLevel;
	private boolean thisContext;

	public FunNode(String i, Node t) {
		id = i;
		// System.out.println("funnode constructor"+t.toPrint(""));
		type = t;
	}

	public void addDecBody(ArrayList<Node> d, Node b) {
		declist = d;
		body = b;
	}

	public STentry getEntry() {
		return entry;
	}

	public String getID() {
		return id;
	}

	public String getInsideClass() {
		return insideClass;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {

		// create result list
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		// env.offset = -2;
		HashMap<String, STentry> hm = env.symTable.get(env.nestingLevel);
		// System.out.println("OFFSET OF THE FUNCTION "+id+" = "+env.offset);

		if (env.insideClass == null)
			entry = new STentry(env.nestingLevel, env.offset--); // separo introducendo "entry"
		else {
			entry = new STentry(env.nestingLevel, env.offset--);
		}
		if ((hm.put(id, entry) != null) && (!env.skip)) {
			res.add(new SemanticError("Fun id " + id + " already declared"));
		} else {
			insideClass = env.insideClass;

			env.nestingLevel++;
			HashMap<String, STentry> hmn = new HashMap<String, STentry>();
			env.symTable.add(hmn);

			ArrayList<Node> parTypes = new ArrayList<Node>();
			int paroffset;
			if (env.insideClass == null)
				paroffset = 1;
			else
				paroffset = env.offsetAfterDec;

			// check args
			for (Node a : parlist) {
				ParNode arg = (ParNode) a;
				parTypes.add(arg.getType());
				if (hmn.put(arg.getId(), new STentry(env.nestingLevel, arg.getType(), paroffset++)) != null) {
					res.add(new SemanticError("In fun:" + id + " var:" + arg.getId() + " already declared"));
				}

			}

			// set func type
			entry.addType(new ArrowTypeNode(parTypes, type));
			// check semantics in the dec list
			int prec = env.offset;
			if (declist.size() > 0) {

				env.offset = -2;
				// if there are children then check semantics for every child and save the
				// results
				for (Node n : declist) {
					res.addAll(n.checkSemantics(env));
				}
			}
			env.offset = prec;

			// check body
			res.addAll(body.checkSemantics(env));
			nestingLevel = env.nestingLevel;
			// close scope
			env.symTable.remove(env.nestingLevel--);

		}
		envSaved = env;
		thisContext = env.thisContext;
		// System.out.println("this context for "+id+" ="+thisContext);
		return res;
	}

	public void addPar(Node p) {
		parlist.add(p);
	}

	public String toPrint(String s) {
		String parlstr = "";
		for (Node par : parlist)
			parlstr += par.toPrint(s + "  ");
		String declstr = "";
		if (declist != null)
			for (Node dec : declist)
				declstr += dec.toPrint(s + "  ");
		return s + "Fun:" + id + "\n" + type.toPrint(s + "  ") + parlstr + declstr + body.toPrint(s + "  ");
	}

	// valore di ritorno non utilizzato
	public Node typeCheck() {

		// TYPE CHECK BETWEEN SUBFUNCTION
		boolean ok = true;
		if (insideClass != null) {

			HashMap<String, STentry> hm2 = envSaved.symTableL0;
			String superc = hm2.get(insideClass).getType1().getSuper();

			ok = searchInSuper(superc, hm2);
		}
		if (!ok) {
			System.out.println("The function " + id + " in the class " + insideClass
					+ " cannot override the function in its superclass");
			System.exit(0);
		}

		for (Node dec : declist) {
			dec.typeCheck();
		}
		Node t = body.typeCheck();
		if ((type instanceof ClassTypeNode) && (t instanceof ClassTypeNode)) {
			if (!(FOOLlib.isSubtype(((ClassTypeNode) type).getTypeID(), ((ClassTypeNode) t).getTypeID(),
					envSaved.subClassTable))) {
				System.out.println("Wrong return type for function " + id);
				System.exit(0);
			}
		} else {
			if (!(FOOLlib.isSubtype(t, type))) {
				System.out.println("Wrong return type for function " + id);
				System.exit(0);
			}
		}
		return t;
	}

	private boolean searchInSuper(String superc, HashMap<String, STentry> hm2) {
		if (superc == null)
			return true;

		else {

			for (FunContext f : hm2.get(superc).getFunContext()) {

				if ((f.ID().getText().equals(id)) && !(subFun(f, insideClass))) {
					return false;
				}

			}

		}

		return searchInSuper(envSaved.symTableL0.get(superc).getType1().getSuper(), hm2);
	}

	public String codeGeneration() {

		String declCode = "";
		if (declist != null) {
			for (Node dec : declist) {
				declCode += dec.codeGeneration();
				if (dec instanceof IdNode) {
					// System.out.println(((idNode)dec).getEntry());
				}
			}
		}
		String popDecl = "";
		if (declist != null)
			for (Node dec : declist) {
				popDecl += "pop\n";
				if (dec instanceof VarNode) {
					if (((VarNode) dec).getExps() instanceof NewExpNode) {
					}

				}
			}

		String popParl = "";
		for (Node dec : parlist) {
			popParl += "pop\n";

		}

		// dichiarazioni globali
		if ((insideClass != null) && (nestingLevel < 3)) {
			for (VardecContext c : envSaved.symTableL0.get(insideClass).getListContext()) {
				popParl += "pop\n";
			}
		}

		String funl = FOOLlib.freshFunLabel();
		HashMap<String, String> funTable = new HashMap<String, String>();
		funTable.put(id, funl);
		if (nestingLevel == 2) {
			if (envSaved.dispatchTable.get(insideClass) == null)
				envSaved.dispatchTable.put(insideClass, funTable);
			else {
				funTable.putAll(envSaved.dispatchTable.get(insideClass));
				envSaved.dispatchTable.put(insideClass, funTable);
			}
		}

		FOOLlib.putCode(funl + ":\n" +

				"cfp\n" + // setta $fp a $sp
				"lra\n" + // inserimento return address
				declCode + // inserimento dichiarazioni locali
				body.codeGeneration() + "srv\n" + // pop del return value
				popDecl + "sra\n" + // pop del return address
				"pop\n" + // pop di AL
				popParl + "sfp\n" + // setto $fp a valore del CL
				"lrv\n" + // risultato della funzione sullo stack
				"lra\n" + "js\n" // salta a $ra
		);

		return "push " + funl + "\n";
	}

	private boolean subFun(FunContext nameFun, String idleft2) {

		STentry subEntry = envSaved.symTableL0.get(insideClass);

		for (FunContext f1 : subEntry.getFunContext()) {
			// se hanno lo stesso id e lo stesso numero di parametri
			if ((nameFun.ID().getText().equals(f1.ID().getText())) && (f1.ID().getText().equals(id))
					&& (nameFun.vardec().size() == f1.vardec().size())) {
				int c2 = 0;

				if (nameFun.type().getText().equals("int") || nameFun.type().getText().equals("bool")) {
					if (!(nameFun.type().getText().equals(f1.type().getText()))) {
						return false;
					}

				}
				if (!(FOOLlib.isSubtype(nameFun.type().getText(), f1.type().getText(), envSaved.subClassTable)))

					return false;

				for (VardecContext v : nameFun.vardec()) {
					// "+c2+" "+insideClass);
					if (v.type().getText().equals("int") || v.type().getText().equals("bool")) {
						if (!(v.type().getText().equals(f1.vardec(c2).type().getText()))) {
							return false;
						}
					} else if (!(FOOLlib.isSubtype(v.type().getText(), f1.vardec(c2).type().getText(),
							envSaved.subClassTable))) {
						return false;
					}

					c2++;
				}
				return true;
			}

		}
		return true;
	}
}