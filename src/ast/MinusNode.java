package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;
/**
 * 
 * @author antonio paride e mattia 
 * Create first node MinusClass. It will accept the minus symbol 
 *
 */
public class MinusNode implements Node {

  private Node left;
  private Node right;
  
  public MinusNode (Node l, Node r) {
    left=l;
    right=r;
  }
  
  @Override
 	public ArrayList<SemanticError> checkSemantics(Environment env) {
	  //create the result
	  ArrayList<SemanticError> res = new ArrayList<SemanticError>();
	  
	  //check semantics in the left and in the right exp
	  
	  res.addAll(left.checkSemantics(env));
	  res.addAll(right.checkSemantics(env));
	  
 	  return res;
 	}
  
  public String toPrint(String s) {
    return s+"Minus\n" + left.toPrint(s+"  ")  
                      + right.toPrint(s+"  ") ; 
  }
  
  public Node typeCheck() {
    if (! ( FOOLlib.isSubtype(left.typeCheck(),new IntTypeNode()) &&
            FOOLlib.isSubtype(right.typeCheck(),new IntTypeNode()) ) ) {
    	// TODO clean
//      System.out.println("Non integers in diff");
//      System.exit(0);
    	FOOLlib.setTypeCheckError("Non integers in diff ");
    }
    return new IntTypeNode();
  }
  
  // TODO change code generation
  public String codeGeneration() {
		return left.codeGeneration()+
			   right.codeGeneration()+
			   "add\n";
  }
  
}  