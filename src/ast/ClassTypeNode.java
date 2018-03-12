package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public class ClassTypeNode implements Node{
	
	private String id; // type
	
	public ClassTypeNode(String id) {
		this.id = id;
	}

	public String getType() {
		return id;
	}
	
	@Override
	public String toPrint(String indent) {
		
		return "ClassType: " + this.id + "\n";
	}
	
	// TODO verificare cosa fare 
	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		ArrayList<SemanticError> res = new ArrayList<>();
		
		return res;
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

	

}
