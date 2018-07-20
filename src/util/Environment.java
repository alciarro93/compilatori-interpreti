package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.misc.Pair;

import parser.FOOLParser.FunContext;
import parser.FOOLParser.VardecContext;
import ast.Node;
import ast.STentry;

public class Environment {

	// THESE VARIABLES SHOULDN'T BE PUBLIC
	// THIS CAN BE DONE MUCH BETTER
	public ArrayList<HashMap<String, STentry>> symTable = new ArrayList<HashMap<String, STentry>>();
	public HashMap<String, String> subClassTable = new HashMap<String, String>();
	public HashMap<Pair<String, String>, Pair<String, String>> subFuncTableEnv = new HashMap<Pair<String, String>, Pair<String, String>>();
	public int nestingLevel = -1;
	public int offset = 0;
	public boolean insidefunction = true;
	public HashMap<String, HashMap<String, STentry>> virtualsTables = new HashMap<String, HashMap<String, STentry>>();
	public HashMap<String, STentry> symTableL0 = new HashMap<String, STentry>();
	public String insideClass = null;
	public int offsetAfterDec;
	public HashMap<String, HashMap<String, String>> dispatchTable = new HashMap<String, HashMap<String, String>>();
	public List<VardecContext> varSub;
	public List<FunContext> listFunc = new ArrayList<FunContext>();
	public ArrayList<Node> toADD = null;
	public boolean skip = false;
	public boolean thisContext = false;

}
