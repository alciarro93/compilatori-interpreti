package ast;

import java.util.ArrayList;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class NewExpNode implements Node {

	private String id;
	private ArrayList<Node> expList;

	// CHECKSEMANTIC AND TYPE CHECKING
	private ClassDecNode cl;

	public NewExpNode(String id) {
		this.id = id;
		this.expList = new ArrayList<Node>();
	}

	public void addExpList(Node n) {
		this.expList.add(n);
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		// this is the right left part of assignment of a Class
		// i.e --> A a = new A(); --> new A();

		// verify if the class exist:
		cl = env.getClassFromST(id);

		if (cl == null) {
			res.add(new SemanticError("class " + id + " not declared"));
		} else {

			// if new A(param1, param2) == class A(bool bField1, bField2)
			// if number param instance of class == number fields Class
			// param1, param2 == bField1, bField2
			if (cl.getFieldsList().size() == expList.size()) {
				for (Node field : expList) {
					res.addAll(field.checkSemantics(env));
				}
			} else {

				int paramRequired = cl.getFieldsList().size();
				int paramPassed = expList.size();
				res.add(new SemanticError("Error in class " + id + ". " + "Number of param required: " + paramRequired
						+ ". " + "Number of param passed: " + paramPassed));

			}
		}

		return res;
	}

	@Override
	public String toPrint(String s) {
		String exp = "";
		for (Node dec : expList)
			exp += dec.toPrint(s + "  ");

		return s + "NewExp:" + id + "\n" + exp;
	}

	@Override
	public Node typeCheck() {

		// NOTA: the size of the number of param is checked from checkSemantic

		ArrayList<Node> t = cl.getFieldsList();
		// A a = new A(2,3); --> new A(2,3);
		// handle field of declaration of class
		// i.e --> 2, 3
		for (int i = 0; i < expList.size(); i++) {

			if (!(FOOLlib.isSubtype(expList.get(i).typeCheck(), t.get(i).typeCheck())))
				FOOLlib.setTypeCheckError("Type mismatch for param " + (i + 1)); // TODO vedere se si puo richiamare la
																					// variabile
		}
		// return Id node of the class.
		// it's important because in typecheck @VarAsmNode
		// ...see VarAsmNode - typecheck()
		return new ClassTypeNode(id);
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

}
