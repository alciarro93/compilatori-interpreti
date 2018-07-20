package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public class FieldNode implements Node {

	private String id;
	private Node type;

	public FieldNode(String i, Node t) {
		id = i;
		type = t;
	}

	public String getId() {
		return id;
	}

	public Node getType() {
		return type;
	}

	@Override
	public String toPrint(String indent) {
		return indent + "Field:" + id + "\n" + type.toPrint(indent + "  ");
	}

	@Override
	public Node typeCheck() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

}
