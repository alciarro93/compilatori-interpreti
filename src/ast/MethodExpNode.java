package ast;

import java.util.ArrayList;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;

public class MethodExpNode implements Node {

	private Node firstID; // l'ho cambiato perchè non serve specificare che sia di IdNode ma ANTLR lo
							// capisce da solo
	private Node method; // dovrebbe essere di tipo FunNode
	private ArrayList<Node> paramsMethod;

	// CHECKSEMANTIC AND TYPECHECK
	private FunNode methodNode;

	public MethodExpNode() {
		this.paramsMethod = new ArrayList<Node>();
	}

	public void setFirstID(String firstID) {
		this.firstID = new IdNode(firstID);
	}

	public void setMethodClass(String secondID) {
		this.method = new IdNode(secondID);
	}

	public void addExpList(Node n) {
		this.paramsMethod.add(n);
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {

		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		// if @firstID is not null, then is variable. @firstID is of the type
		// IdNode and for this call the checksemantinc on this variable
		// will verify if exist going up the Symbol Table
		String className = "";
		if (firstID != null) {
			res.addAll(firstID.checkSemantics(env));
			STentry idEntry = ((IdNode) firstID).getEntry();
			if (idEntry != null) {
				if (idEntry.getType() instanceof ClassTypeNode) {
					// get the type of variable that call the method
					className = ((ClassTypeNode) idEntry.getType()).getType();
				} else {
					res.add(new SemanticError("Identifier " + ((IdNode) firstID).getId() + " is not an object"));
				}
			}
			// if we are using this.method
		} else {
			className = env.getClassEnvironment();
			if (className.compareTo("") == 0) {
				res.add(new SemanticError("Use of 'THIS' allowed only inside class declaration"));
			}
		}

		// TODO
		// DA IMPLEMENTARE IL DINAMIC DISPATCH QUI

		// check if exist the method of the class. For this we retrieve the node of
		// SymbleTable because now we are outside of the scope and for this deleted from
		// Symbol table
		ClassDecNode cl = env.getClassFromST(className);

		if (cl != null) {
			methodNode = null;
			boolean exist = false;
			String m = ((IdNode) method).getId();

			// check if current method exist inside implementation of class
			for (int i = 0; i < cl.getMethodsList().size(); i++) {
				methodNode = (FunNode) cl.getMethodsList().get(i);
				if (methodNode.getId().compareTo(m) == 0) {
					exist = true;
					break;
				}
			}

			if (!exist)
				res.add(new SemanticError("Method " + m + " not declared inside class " + className));
			else {
				if (methodNode.getParams().size() != paramsMethod.size()) {
					// check number of params passed
					int paramRequired = methodNode.getParams().size();
					int paramPassed = paramsMethod.size();
					res.add(new SemanticError(
							"Error in method " + methodNode.getId() + ". " + "Number of param required: "
									+ paramRequired + ". " + "Number of param passed: " + paramPassed));
				}

			}

			if (exist) {
				for (int i = 0; i < paramsMethod.size(); i++) {
					res.addAll(paramsMethod.get(i).checkSemantics(env));
				}

			}

		}

		return res;
	}

	@Override
	public String toPrint(String s) {
		String exp = "";
		for (Node dec : paramsMethod)
			exp += dec.toPrint(s + " ");
		String f = firstID == null ? "this" : firstID.toPrint("");

		return s + "MethodExp:" + f + "\n" + s + method.toPrint(s) + "\n" + s + exp;
	}

	@Override
	public Node typeCheck() {

		for (int i = 0; i < paramsMethod.size(); i++) {
			if (!(FOOLlib.isSubtype(paramsMethod.get(i).typeCheck(), methodNode.getParams().get(i).typeCheck())))
				FOOLlib.setTypeCheckError(
						"Type mismatch for param " + (i + 1) + " of method " + ((IdNode) method).getId());
		}

		// System.out.println(method.getClass());
		// return the type of the function ArrowTypeNode
		return methodNode.getType();
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

}
