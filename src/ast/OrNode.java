package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class OrNode implements Node {

	private Node left;
	private Node right;

	public OrNode(Node l, Node r) {
		left = l;
		right = r;
	}

	public ArrayList<SemanticError> checkSemantics(Environment env) {
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		res.addAll(left.checkSemantics(env));
		res.addAll(right.checkSemantics(env));
		return res;
	}

	public String toPrint(String s) {
		return s + "Or\n" + left.toPrint(s + "  ") + right.toPrint(s + "  ");
	}

	public Node typeCheck() {
		if (!(FOOLlib.isSubtype(left.typeCheck(), new BoolTypeNode())
				&& FOOLlib.isSubtype(right.typeCheck(), new BoolTypeNode()))) {
			System.out.println("Non booleans in OR operation");
			System.exit(0);
		}
		return new BoolTypeNode();
	}

	public String codeGeneration() {
		String or2 = FOOLlib.freshLabel();
		String orTrue = FOOLlib.freshLabel();
		String orFalse = FOOLlib.freshLabel();
		String end = FOOLlib.freshLabel();
		return left.codeGeneration() + "push 0" + "\n" + "beq " + or2 + "\n" + "b " + orTrue + "\n" + or2 + ": \n"
				+ right.codeGeneration() + "push 0" + "\n" + "beq " + orFalse + "\n" +
				// "b " + orTrue + "\n" +
				orTrue + ":" + "\n" + "push 1" + "\n" + "b " + end + "\n" + orFalse + ":" + "\n" + "push 0" + "\n" + end
				+ ":";

	}

}