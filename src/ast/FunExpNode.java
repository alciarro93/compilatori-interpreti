package ast;
import java.util.ArrayList;

import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class FunExpNode implements Node {

  private String id;
  private STentry entry; 
  private ArrayList<Node> parlist; 
  private int nestinglevel;

  
  public FunExpNode (String i, STentry e, ArrayList<Node> p, int nl) {
    id=i;
    entry=e;
    parlist = p;
    nestinglevel=nl;
  }
  
  public FunExpNode(String text, ArrayList<Node> args) {
	id=text;
    parlist = args;
}

public String toPrint(String s) {  //
    String parlstr="";
	for (Node par:parlist)
	  parlstr+=par.toPrint(s+"  ");		
	return s+"Call:" + id + "\n"
           +parlstr; 
	// TODO clear code
//	return s+"Call:" + id + " at nestlev " + nestinglevel +"\n" 
//    +entry.toPrint(s+"  ")
//    +parlstr; 
  }

@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		//create the result
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		
		 int j=env.getNestingLevel();
		 STentry tmp=null; 
		 // slide entire HashTable for find out if the specific variable exist or not
		 while (j>=0 && tmp==null)
		     tmp=(env.getSymTable().get(j--)).get(id);
		 if (tmp==null)
			 res.add(new SemanticError("Id "+id+" not declared"));
		 
		 else{
			 this.entry = tmp;
			 this.nestinglevel = env.getNestingLevel();
			 
			 for(Node arg : parlist)
				 res.addAll(arg.checkSemantics(env));
		 }
		 return res;
	}
  
  public Node typeCheck () {  //                           
	 ArrowTypeNode t=null;
     if (entry.getType() instanceof ArrowTypeNode) t=(ArrowTypeNode) entry.getType(); 
     else {
       System.out.println("Invocation of a non-function "+id);
       System.exit(0);
     }
     ArrayList<Node> p = t.getParList();
     if ( !(p.size() == parlist.size()) ) {
       System.out.println("Wrong number of parameters in the invocation of "+id);
       System.exit(0);
     } 
     for (int i=0; i<parlist.size(); i++) 
       if ( !(FOOLlib.isSubtype( (parlist.get(i)).typeCheck(), p.get(i)) ) ) {
         System.out.println("Wrong type for "+(i+1)+"-th parameter in the invocation of "+id);
         System.exit(0);
       } 
     return t.getRet();
  }
  
  public String codeGeneration() {
	    String parCode="";
	    for (int i=parlist.size()-1; i>=0; i--)
	    	parCode+=parlist.get(i).codeGeneration();
	    
	    String getAR="";
		  for (int i=0; i<nestinglevel-entry.getNestinglevel(); i++) 
		    	 getAR+="lw\n";
	    
		return "lfp\n"+ //CL
               parCode+
               "lfp\n"+getAR+ //setto AL risalendo la catena statica
               // ora recupero l'indirizzo a cui saltare e lo metto sullo stack
               "push "+entry.getOffset()+"\n"+ //metto offset sullo stack
		       "lfp\n"+getAR+ //risalgo la catena statica
			   "add\n"+ 
               "lw\n"+ //carico sullo stack il valore all'indirizzo ottenuto
		       "js\n";
  }

    
}  