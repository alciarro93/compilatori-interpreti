package ast;

import java.util.ArrayList;

import lib.FOOLlib;
import util.Environment;
import util.SemanticError;
import util.Util;

public class IdNode implements Node {

  private String id;
  private STentry entry;
  private int nestinglevel;
  
  public IdNode (String i) {
    id=i;
  }
  
//  public String toPrint(String s) {
//	return s+"Id:" + id + " at nestlev " + nestinglevel +"\n" + entry.toPrint(s+"  ") ;  
//  }
  // TODO temp da eliminare ed usare quello di sopra. 
  public String toPrint(String s) {
		// return s+"Id:" + id + " at nestlev " + nestinglevel +"\n" + entry.toPrint(s+"  ") ;  
	  return s+"Id:" + id + "\n";
	  }
  
  public String getId() {
	  return id;
  }
  
  public STentry getEntry() {
	  return entry;
  }
  @Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {	  
	  //create result list
	  ArrayList<SemanticError> res = new ArrayList<SemanticError>();
	  
	  // start to the end of List of Hashtable and climb this list for find out if a variable is declared or not
	  int j=env.getNestingLevel(); 
	  STentry tmp=null; 
	  while (j>=0 && tmp==null)
		  tmp=(env.getSymTable().get(j--)).get(id);
      if (tmp==null) {
          res.add(new SemanticError("Id "+id+" not declared"));      		
      }else{
    	  entry = tmp;
    	  nestinglevel = env.getNestingLevel();
    	  
      }
     
      
	  return res;
	}
  
  public Node typeCheck () {
	  //TODO se si fa il typecheck di un oggetto istanziato, entry è null perchè
	  // noi non salviamo l'oggetto e quindi fallisce
	  // dobbiamo decidere se creare un nodo ExpNode oppure usare una ObjectTable dove salvare
	  // il valore degli oggetti così da poterli recuperare con solo il nome.

	if (entry.getType() instanceof ArrowTypeNode) { //
		// TODO clean
//	  System.out.println("Wrong usage of function identifier");
//      System.exit(0);
		FOOLlib.setTypeCheckError("Wrong usage of function identifier");
    }	 
	
    return entry.getType();
  }
  
  public String codeGeneration() {
      String getAR="";
	  for (int i=0; i<nestinglevel-entry.getNestinglevel(); i++) 
	    	 getAR+="lw\n";
	    return "push "+entry.getOffset()+"\n"+ //metto offset sullo stack
		       "lfp\n"+getAR+ //risalgo la catena statica
			   "add\n"+ 
               "lw\n"; //carico sullo stack il valore all'indirizzo ottenuto

  }
}  