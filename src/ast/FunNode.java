package ast;

import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class FunNode implements Node {

	private String id;
	private Node type;
	private ArrayList<Node> params = new ArrayList<Node>();
	private ArrayList<Node> letDecList;
	private Node exp;
	private ClassTypeNode classMembership; // classe di appartenenza // TODO settarlo nella creazione AST

	public FunNode(String id, Node type) {
		this(id, type, null); // default: null perchè non è di una classe di appartenenza

	}

	public FunNode(String id, Node type, ClassTypeNode classMembership) {
		this.id = id;
		this.type = type;
		this.classMembership = classMembership;
	}
	
	public ArrayList<Node> getParams() {
		return params;
	}

	public Node getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public void setClassMembership(ClassTypeNode classMembership) {
		this.classMembership = classMembership;
	}

	// letDecList can be empty because let is optional. In this case toPrint
	// printing only exp expression
	public void addLetInExp(ArrayList<Node> d, Node b) {
		letDecList = d;
		exp = b;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// create result listO
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		// env.offset = -2;
		HashMap<String, STentry> hm = env.getSymTable().get(env.getNestingLevel());
		STentry entry = new STentry(env.getNestingLevel(), env.getOffset()); // separo introducendo "entry"
		env.decOffset();

		if (!(type instanceof IntTypeNode) && !(type instanceof BoolTypeNode)) {
			String className = ((ClassTypeNode) type).getType();
			if (env.verifyClass(className))
				res.add(new SemanticError("Class " + className + " not declared"));
		}

		if (hm.put(id, entry) != null)
			res.add(new SemanticError("Function identifier " + id + " already declared"));
		else {
			// creare una nuova hashmap per la symTable
			env.incNestingLevel();
			HashMap<String, STentry> hmn = new HashMap<String, STentry>();
			env.getSymTable().add(hmn);

			ArrayList<Node> parTypes = new ArrayList<Node>();

			// Set use of parOffset
			env.setParOffset(1);

			// check args
			for (Node a : params) {
				VarDecNode arg = (VarDecNode) a;
				parTypes.add(arg.getType());
				res.addAll(arg.checkSemantics(env));
			}

			// remove use of parOffset
			env.setParOffset(-1);
			// set func type
			entry.addType(new ArrowTypeNode(parTypes, type));

			// check semantics in the dec list
			if (letDecList.size() > 0) {
				env.setOffset(-2);
				// if there are children then check semantics for every child and save the
				// results
				for (Node n : letDecList)
					res.addAll(n.checkSemantics(env));
			}

			// check body
			res.addAll(exp.checkSemantics(env));

			// close scope
			env.getSymTable().remove(env.getNestingLevel()); // TODO NON AGGIUNGE LA SYMBLE TABLE
			env.decNestingLevel();
		}

		return res;
	}

	public void addPar(Node p) {
		params.add(p);
	}

	public String toPrint(String s) {
		String parlstr = "";
		for (Node par : params)
			parlstr += par.toPrint(s + "  ");
		String declstr = "";
		if (letDecList != null)
			for (Node dec : letDecList)
				declstr += dec.toPrint(s + "  ");
		return s + "Fun:" + id + "\n" + type.toPrint(s + "  ") + parlstr + declstr + exp.toPrint(s + "  ");
	}

	// valore di ritorno non utilizzato
	public Node typeCheck() {
		if (letDecList != null)
			for (Node dec : letDecList)
				dec.typeCheck();
		if (!(FOOLlib.isSubtype(exp.typeCheck(), type))) {
			
			// TODO clean
//			System.out.println("Wrong return type for function " + id);
//			System.exit(0);
			FOOLlib.setTypeCheckError("Wrong return type for function  " + id);
		}
		return null;
	}

	public String codeGeneration() {

		String declCode = "";
		if (letDecList != null)
			for (Node dec : letDecList)
				declCode += dec.codeGeneration();

		String popDecl = "";
		if (letDecList != null)
			for (Node dec : letDecList)
				popDecl += "pop\n";

		String popParl = "";
		for (Node dec : params)
			popParl += "pop\n";

		String funl = FOOLlib.freshFunLabel();
		FOOLlib.putCode(funl + ":\n" + "cfp\n" + // setta $fp a $sp
				"lra\n" + // inserimento return address
				declCode + // inserimento dichiarazioni locali
				exp.codeGeneration() + "srv\n" + // pop del return value
				popDecl + "sra\n" + // pop del return address
				"pop\n" + // pop di AL
				popParl + "sfp\n" + // setto $fp a valore del CL
				"lrv\n" + // risultato della funzione sullo stack
				"lra\n" + "js\n" // salta a $ra
		);

		return "push " + funl + "\n";
	}

}