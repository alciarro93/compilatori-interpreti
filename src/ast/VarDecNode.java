package ast;

import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class VarDecNode implements Node {

	private Node type;
	private String id;

	public VarDecNode(String id, Node type) {
		this.type = type;
		this.id = id;
	}

	public Node getType() {
		return type;
	}

	public void setType(Node newtype) {
		type = newtype;
	}

	public String getId() {
		return id;
	}

	public void setID(String newid) {
		id = newid;
	}

	@Override
	public String toPrint(String s) {
		// TODO Auto-generated method stub

		return s + "VarDec:" + id + "\n" + type.toPrint(s + "  ");
	}

	@Override
	public Node typeCheck() {

		if (type instanceof BoolTypeNode)
			return new BoolTypeNode();
		else if (type instanceof IntTypeNode)
			return new IntTypeNode();
		else if (type instanceof ClassTypeNode)
			return new ClassTypeNode(id);

		return null;
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		
		// create result list
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		// retrieve symbol table at current level
		HashMap<String, STentry> hm = env.getSymTable().get(env.getNestingLevel());
		STentry entry = new STentry(env.getNestingLevel(), type, env.getOffset()); // separo introducendo "entry"
		env.decOffset();
		// check valid type
		if (!(type instanceof IntTypeNode) && !(type instanceof BoolTypeNode)) {
			String className = ((ClassTypeNode) type).getType();
			if (env.verifyClass(className))
				res.add(new SemanticError("Class " + className + " not declared"));
		}
		// check exist
		if (hm.put(id, entry) != null) {
			res.add(new SemanticError("Identifier " + id + " already declared"));
		} else {
			//TODO save type forse
		}

		return res;
	}

}
