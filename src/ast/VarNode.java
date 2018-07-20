package ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.antlr.v4.runtime.misc.Pair;

import parser.FOOLParser.FunContext;
import parser.FOOLParser.VardecContext;
import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class VarNode implements Node {

	private static final boolean String = false;
	private String id;
	private Node type;
	private Node exp;
	private String idright;
	private String idleft;
	private DotCallNode dotCaseNode;
	private IdNode idCaseNode = null;
	private CallNode callCaseNode = null;
	private Node m = null;
	private Environment envSaved;
	private int nestingLevel;
	private int offsetIdleft;
	private int offsetIdright;
	private HashMap<String, String> subclass = new HashMap<String, String>();

	public VarNode(String i, Node t, Node v, String s1, String s) {
		id = i;
		type = t;
		exp = v;
		idright = s;
		idleft = s1;

	}

	public VarNode(String i, Node t, Node v, String s1, String s, IdNode n) {
		id = i;
		type = t;
		exp = v;
		idright = s;
		idleft = s1;
		idCaseNode = n;

	}

	public VarNode(String i, Node t, Node v, String s1, String s, DotCallNode n) {
		id = i;
		type = t;
		exp = v;
		idright = s;
		idleft = s1;
		dotCaseNode = n;

	}

	public VarNode(String i, Node t, Node v, String s1, String s, CallNode n) {
		id = i;
		type = t;
		exp = v;
		idright = s;
		idleft = s1;
		callCaseNode = n;

	}

	public Node getExps() {
		return exp;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// create result list
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		// env.offset = -2;
		HashMap<String, STentry> hm = env.symTable.get(env.nestingLevel);
		HashMap<String, STentry> hm0 = env.symTable.get(0);
		// System.out.println("offsetlet="+ id+" "+env.offset);
		offsetIdleft = env.offset;
		STentry entry = new STentry(env.nestingLevel, type, env.offset--);

		// separo introducendo "entry"
		if (hm.put(id, entry) != null)
			res.add(new SemanticError("Var id " + id + " already declared"));

		res.addAll(exp.checkSemantics(env));

		res.addAll(type.checkSemantics(env));
		if (idCaseNode != null) {
			ArrayList<SemanticError> res1 = new ArrayList<SemanticError>();
			res1.addAll(idCaseNode.checkSemantics(env));
			if (res1.size() == 0) {
				STentry m = idCaseNode.getEntry();
				if (m.getType() instanceof ClassTypeNode) {
					ClassTypeNode m1 = (ClassTypeNode) m.getType();
					offsetIdright = m.getOffset();
					idright = m1.getTypeID();
				} else if (m.getType() instanceof IntTypeNode) {
					idright = "int";
				} else {
					idright = "bool";
				}
			}
		} else if (dotCaseNode != null) {
			// System.out.println(dotCaseNode.toPrint(""));
			ArrayList<SemanticError> res1 = new ArrayList<SemanticError>();
			res1.addAll(dotCaseNode.checkSemantics(env));

			if (res1.size() == 0) {
				dotCaseNode.typeCheck();
				idright = dotCaseNode.getReturnType();

			}
		} else if (callCaseNode != null) {
			// System.out.println(dotCaseNode.toPrint(""));
			ArrayList<SemanticError> res1 = new ArrayList<SemanticError>();
			res1.addAll(callCaseNode.checkSemantics(env));

			if (res1.size() == 0) {
				if (callCaseNode.typeCheck() instanceof ClassTypeNode) {
					ClassTypeNode m1 = (ClassTypeNode) callCaseNode.typeCheck();
					// offsetIdright=m.getOffset();
					idright = m1.getTypeID();
				} else if (callCaseNode.typeCheck() instanceof IntTypeNode) {
					idright = "int";
				} else {
					idright = "bool";
				}
			}
		} else {
			// System.out.println(exp.toPrint(indent));
			if (exp.typeCheck() instanceof ClassTypeNode) {
				ClassTypeNode c = (ClassTypeNode) exp.typeCheck();
				idright = c.getTypeID();

			}
		}
		subclass = env.subClassTable;
		envSaved = env;
		nestingLevel = env.nestingLevel;
		if (type instanceof ClassTypeNode) {
			ClassTypeNode m1 = ((ClassTypeNode) type);
			m1.setTypeId(idright);
			STentry entry1 = new STentry(env.nestingLevel, m1, offsetIdleft);
			hm.put(id, entry1);
		}
		return res;
	}

	public String toPrint(String s) {
		return s + "Var:" + id + "\n" + type.toPrint(s + "  ") + exp.toPrint(s + "  ");
	}

	public Node typeCheck() {

		HashMap<String, String> assgn = new HashMap<String, String>();
		if (type instanceof ClassTypeNode) {
			if (!(FOOLlib.isSubtype(idleft, idright, subclass))) {
				System.out.println("incompatible value for variable " + id);
				System.exit(0);
			} else {
				((ClassTypeNode) type).setTypeId(idright);
			}
		} else {

			if (!(FOOLlib.isSubtype(exp.typeCheck(), type))) {
				System.out.println("incompatible value for variable " + id);
				System.exit(0);

			}
		}
		return exp.typeCheck();
	}

	public String codeGeneration() {
		String getAR = "";
		String s = id + nestingLevel;
		if ((idCaseNode != null) && (!idright.equals("int")) && (!idright.equals("bool"))) {
			return "lfp\n" + "push " + offsetIdright + "\n" + "add\n" + "lw\n";
		}
		String s1 = "pop\n";
		if ((type instanceof ClassTypeNode) && dotCaseNode != null)
			return type.codeGeneration() + exp.codeGeneration() + s1;
		else
			return type.codeGeneration() + exp.codeGeneration();
	}

}