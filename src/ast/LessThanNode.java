package ast;

import java.util.ArrayList;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class LessThanNode implements Node {

	private Node right;
	private Node left;

	public LessThanNode(Node l, Node r) {
		left = l;
		right = r;
	}

	public ArrayList<SemanticError> checkSemantics(Environment env) {
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		res.addAll(left.checkSemantics(env));
		res.addAll(right.checkSemantics(env));
		return res;
	}

	@Override
	public String toPrint(String s) {
		return s + "LT\n" + left.toPrint(s + "  ") + right.toPrint(s + "  ");
	}

	@Override
	public Node typeCheck() {
		if (!(FOOLlib.isSubtype(left.typeCheck(), new IntTypeNode())
				&& FOOLlib.isSubtype(right.typeCheck(), new IntTypeNode()))) {
			System.out.println("Non integers in 'Less then' operation");
			System.exit(0);
		}
		return new BoolTypeNode();
	}

	@Override
	public String codeGeneration() {
		String fl1 = FOOLlib.freshLabel();
		String fl2 = FOOLlib.freshLabel();
		return left.codeGeneration() + right.codeGeneration() + "bleq " + fl1 + "\n" + "push 0\n" + "b " + fl2 + "\n"
				+ fl1 + ":\n" + "push 1\n" + fl2 + ":\n";
	}
}
