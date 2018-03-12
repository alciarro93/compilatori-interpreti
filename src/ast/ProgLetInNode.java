package ast;

import java.util.ArrayList;
import java.util.HashMap;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

// prima regola del parser rule
public class ProgLetInNode implements Node {

	private ArrayList<Node> declist; // LET
	private Node exp; // EXP (FOOL.g4)

	public ProgLetInNode(ArrayList<Node> d, Node e) {
		declist = d;
		exp = e;
	}

	// TODO : clean code
	public String toPrint(String s) {
		String declstr = "";
		for (Node dec : declist)
			declstr += dec.toPrint(s + "  ");
		// return s+"ProgLetIn\n" + declstr; // TODO delete this line
		return s + "ProgLetIn\n" + declstr + exp.toPrint(s + "  ");

	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {

		env.incNestingLevel();

		// tabella dei simboli
		HashMap<String, STentry> hm = new HashMap<String, STentry>();
		env.getSymTable().add(hm);

		// declare resulting list
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		// check semantics in the dec list
		if (declist.size() > 0) {
			env.setOffset(-2);
			// if there are children then check semantics for every child and save the
			// results
			for (Node n : declist)
				res.addAll(n.checkSemantics(env));
		}

		// check semantics in the exp body
		res.addAll(exp.checkSemantics(env));

		// Util.printSymbolTable(env.symTable, this); // TODO Delete

		// clean the scope, we are leaving a let scope
		env.getSymTable().remove(env.getNestingLevel());
		env.decNestingLevel();

		// return the result
		return res;

	}

	public Node typeCheck() {
		for (Node dec : declist)
			dec.typeCheck();
		return exp.typeCheck();
	}

	public String codeGeneration() {
		String declCode = "";
		for (Node dec : declist)
			declCode += dec.codeGeneration();
		return "push 0\n" + declCode + exp.codeGeneration() + "halt\n" + FOOLlib.getCode();

	}

}