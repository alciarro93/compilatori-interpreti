package ast;

import java.util.ArrayList;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class AndNode implements Node {
	private Node left;
	private Node right;

	public AndNode(Node l, Node r) {
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
		return s + "And\n" + left.toPrint(s + "  ") + right.toPrint(s + "  ");
	}

	public Node typeCheck() {
		if (!(FOOLlib.isSubtype(left.typeCheck(), new BoolTypeNode())
				&& FOOLlib.isSubtype(right.typeCheck(), new BoolTypeNode()))) {
			System.out.println("Non booleans in AND operation");
			System.exit(0);
		}
		return new BoolTypeNode();
	}

	public String codeGeneration() {
		String andFalse = FOOLlib.freshLabel();
		String andTrue = FOOLlib.freshLabel();
		String end = FOOLlib.freshLabel();

		return left.codeGeneration() + "push 0 \n" + "beq " + andFalse + "\n" + right.codeGeneration() + "push 1" + "\n"
				+ "beq " + andTrue + "\n" + "b " + andFalse + "\n" + andTrue + ": \n" + "push 1" + "\n" + "b " + end
				+ "\n" + andFalse + ": \n" + "push 0" + "\n" + end + ":";

	}

}
