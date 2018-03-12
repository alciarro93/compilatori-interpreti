package ast;

import java.util.ArrayList;
import java.util.HashMap;

import util.Environment;
import util.SemanticError;
import util.Util;
import lib.FOOLlib;

// classe della Varasm 
public class VarAsmNode implements Node {

	private Node vardec;
	private Node exp;

	public VarAsmNode(Node v, Node e) {
		vardec = v;
		exp = e;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// create result list
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();

		res.addAll(vardec.checkSemantics(env));

		res.addAll(exp.checkSemantics(env)); // add the single SemanticError of the node, to global Semantic Error

		return res;

	}

	public String toPrint(String s) {
		return s + "VarAsm:\n" + vardec.toPrint(s + "  ") + exp.toPrint(s + "  ");
	}

	
	public Node typeCheck() {
		
		// handle newExp from normal exp
		// i.e int a = 5;
		if(!(exp instanceof NewExpNode)) {
			if (!(FOOLlib.isSubtype(vardec.typeCheck(), exp.typeCheck()))) {
				// TODO clean
				// System.out.println("incompatible value for variable " );
				// System.exit(0);			
				FOOLlib.setTypeCheckError("Incompatible value for variable  " + ((VarDecNode) vardec).getId());
			}
		// .... or A a = new A  
		}else {
			
			// allow to compare two node of the same tipe = IdTypeNode 
			// the first that depends on VardecNode --> i.e. A a = ....
			// in the latter case depends on NewExpNode --> i.e = ... = new A()
			
			Node declarationClass = vardec.typeCheck(); // return ClassTypeNode from VarDecnode
			Node newClass = exp.typeCheck();			// return ClassTypeNode from NewExpNode
			if (!(FOOLlib.isSubtype(declarationClass, newClass))) {
				FOOLlib.setTypeCheckError("Incompatible value for variable  " + ((VarDecNode) vardec).getId() 
						+ " and " + ((VarDecNode) newClass).getId());
			}
		}
		
		return null;
	}

	public String codeGeneration() {
		return exp.codeGeneration();
	}

}