package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class DivNode implements Node {

	private Node left;
	private Node right;

	public DivNode(Node l, Node r) {
		left = l;
		right = r;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// create the result
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		// check semantics in the left and in the right exp

		res.addAll(left.checkSemantics(env));
		res.addAll(right.checkSemantics(env));

		return res;
	}

	public String toPrint(String s) {
		return s + "Div\n" + left.toPrint(s + "  ") + right.toPrint(s + "  ");
	}

	public Node typeCheck() {
		if (!(FOOLlib.isSubtype(left.typeCheck(), new IntTypeNode())
				&& FOOLlib.isSubtype(right.typeCheck(), new IntTypeNode()))) {
			// TODO clean
			// System.out.println("Non integers in division");
			// System.exit(0);
			FOOLlib.setTypeCheckError("Non integers in division ");
		}
		return new IntTypeNode();
	}

	// TODO codegeneration division
	public String codeGeneration() {
		return left.codeGeneration() + right.codeGeneration() + "mult\n";
	}

}