package ast;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import parser.FOOLParser.FunContext;
import parser.FOOLParser.VardecContext;
import util.Environment;
import util.SemanticError;

public class ClassTypeNode implements Node {
	private String typeName = "";
	private String superclass = null;
	private List<VardecContext> fieldsName = null;
	private List<FunContext> funlist = null;
	private int numberOfpar;
	private List<VardecContext> varDeclaration = null;

	public ClassTypeNode(String s, List<VardecContext> v, List<FunContext> f) {
		typeName = s;
		fieldsName = v;
		funlist = f;
	}

	public ClassTypeNode(String s, List<VardecContext> v, List<FunContext> f, String s1, int n,
			List<VardecContext> v1) {
		typeName = s;
		fieldsName = v;
		funlist = f;
		superclass = s1;
		numberOfpar = n;
		varDeclaration = v1;
	}

	public ClassTypeNode(String s) {
		typeName = s;
	}

	public List<VardecContext> getVardec() {
		return fieldsName;
	}

	public List<FunContext> getFunList() {
		return funlist;
	}

	public String getSuper() {
		return superclass;
	}

	public int getNumberOfpar() {
		return numberOfpar;
	}

	public List<VardecContext> getDecLocal() {
		return varDeclaration;
	}

	public String getTypeID() {
		return typeName;
	}

	public void setTypeId(String id) {
		typeName = id;
	}

	@Override
	public String toPrint(String s) {
		return "	CLASSType: " + typeName + "\n";
	}

	@Override
	public ClassTypeNode typeCheck() {
		// Creo ClassTypeNode con informazioni necessarie
		return new ClassTypeNode(typeName, fieldsName, funlist, superclass, numberOfpar, varDeclaration);
	}

	@Override
	public String codeGeneration() {
		return "lhp\n";
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		HashMap<String, STentry> hm = env.symTable.get(0);
		STentry app = null;
		app = hm.get(typeName);
		if (hm.get(typeName) == null)
			res.add(new SemanticError(typeName + " does not exist"));
		else {
			fieldsName = app.getListContext();
		}
		return res;
	}

}
