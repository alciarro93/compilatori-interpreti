package ast;

import java.util.ArrayList;
import java.util.HashMap;

import util.Environment;
import util.SemanticError;
import util.Util;

/**
 * 
 * @author antonio
 * Class node. It's expand the left three of @classExp
 */
public class ClassExpNode implements Node {
	
	private ArrayList<Node> classdec;
	private ArrayList<Node> letInnerDec;
	private Node exp;
	
	

	public ClassExpNode(ArrayList<Node> classes, Node exp) {
		this.classdec = classes;
		this.letInnerDec = new ArrayList<Node>();
		this.exp = exp;
		
	}
	
	
	public Node getExp() {
		return exp;
	}
	
	public void setExp(Node n) {
		exp = n;
	}
	
	public ArrayList<Node> getClasses(){
		return classdec;
	}
	
	public ArrayList<Node> getLet(){
		return letInnerDec;
	}
	
	public void addLetInnerDec(Node n) {
		this.letInnerDec.add(n);
	}
	
	

	@Override
	public String toPrint(String s) {
		
		String cdec="";
	    for (Node dec:classdec)
	    	cdec+=dec.toPrint(s+"  ");
	    String let = "";
	    // TODO sistemare identazione 
	    if(letInnerDec.isEmpty()) {
	    	return s+"ClassExp\n" 
					+ cdec 
	    			+ exp.toPrint(s+"  ") ;
	    }else {
	    	for(Node l:letInnerDec)
		    	let+=l.toPrint(s+"  ");
	    	return s+"ClassExp\n" 
						+ cdec 
						+ let
						+ exp.toPrint(s+"  ") ;
	    }	   
	}
	
	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		
		env.incNestingLevel(); // start to 0
		
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		
		// Global Symbol Table
		HashMap<String, STentry> globalST = new HashMap<String,STentry>();
		env.getSymTable().add(globalST);	
		
		
		
		// for each class add new Entry to Symble Table (First point of slide pag. 35)
		for (Node entryClass : classdec) {			
			ClassDecNode cl = (ClassDecNode) entryClass;
			
			STentry entry = new STentry(env.GLOBAL_SCOPE, cl ,env.getOffset()); // TODO offset used in code generation.
			env.decOffset();
			if (globalST.put(cl.getId(), entry) != null)
				res.add(new SemanticError("class " + cl.getId() + " already declared"));				
		}
		
		// Enter inside declaration of class 
		for (Node cl : classdec) {
			res.addAll(cl.checkSemantics(env));
		}
		
		// LET ( if exist )
		// if exist let.dec() parser then cycle and make checkSemantic
		if(letInnerDec.size() > 0) {
			for (Node letdec : letInnerDec) {
				res.addAll(letdec.checkSemantics(env));
			}
		}		
		
		// EXP
		res.addAll(exp.checkSemantics(env));
		
		env.getSymTable().remove(env.getNestingLevel());
		env.decNestingLevel();
		
		return res;
	}

	@Override
	public Node typeCheck() {
		
		// call classdec typecheck
		for (Node cl : classdec) {
			cl.typeCheck();
		}
		
		// call letInnerDec typecheck
		for (Node let : letInnerDec) {
			let.typeCheck();
		}
		return exp.typeCheck();
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
