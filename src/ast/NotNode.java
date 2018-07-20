package ast;

import java.util.ArrayList;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class NotNode implements Node {

	private Node condition;

	public NotNode(Node c) {
		condition = c;
	}

	public ArrayList<SemanticError> checkSemantics(Environment env) {
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		res.addAll(condition.checkSemantics(env));
		return res;
	}

	public String toPrint(String s) {
		return s + "Not\n" + condition.toPrint(s + " ");
	}

	public Node typeCheck() {
		if (!(FOOLlib.isSubtype(condition.typeCheck(), new BoolTypeNode()))) {
			System.out.println("Non boolean in NOT operation");
			System.exit(0);
		}
		return new BoolTypeNode();
	}

	public String codeGeneration() {
		String nottrue = FOOLlib.freshLabel();
		String end = FOOLlib.freshLabel();
		return condition.codeGeneration() + "\n" + "push 1" + "\n" + "beq " + nottrue + "\n" + "push 1" + "\n" + "b "
				+ end + "\n" + nottrue + ": " + "push 0" + "\n" + end + ":" + "\n";
	}

}
