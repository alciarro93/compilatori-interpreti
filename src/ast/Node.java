package ast;

import java.util.ArrayList;

import util.Environment;
import util.SemanticError;

public interface Node {

	String toPrint(String indent);

	ArrayList<SemanticError> checkSemantics(Environment env);
	
	// fa il type checking e ritorna:
	// per una espressione, il suo tipo (oggetto BoolTypeNode o IntTypeNode)
	// per una dichiarazione, "null"
	Node typeCheck();

	String codeGeneration();

	

}