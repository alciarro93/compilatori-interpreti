package util;

import java.util.ArrayList;
import java.util.HashMap;

import ast.ClassDecNode;
import ast.STentry;

public class Environment {

	// THESE VARIABLES SHOULDN'T BE PUBLIC
	// THIS CAN BE DONE MUCH BETTER

	public final int GLOBAL_SCOPE = 0; // start scope

	private ArrayList<HashMap<String, STentry>> symTable = new ArrayList<HashMap<String, STentry>>();

	private ArrayList<String> listOfClass = new ArrayList<>();

	// nestinglevel = current scope
	private int nestingLevel = -1;

	// used for code-generation (see slide)
	private int offset = 0;
	// offset for function/method params declaration
	private int parOffset = -1;
	
	//class environment
	private String classEnvironment = "";

	public String getClassEnvironment() {
		return classEnvironment;
	}

	public void setClassEnvironment(String classEnvironment) {
		this.classEnvironment = classEnvironment;
	}

	public void setParOffset(int parOffset) {
		this.parOffset = parOffset;
	}

	public ArrayList<HashMap<String, STentry>> getSymTable() {
		return symTable;
	}

	public void setSymTable(ArrayList<HashMap<String, STentry>> symTable) {
		this.symTable = symTable;
	}

	public int getNestingLevel() {
		return nestingLevel;
	}

	public void setNestingLevel(int nestingLevel) {
		this.nestingLevel = nestingLevel;
	}

	public void incNestingLevel() {
		this.nestingLevel++;
	}

	public void decNestingLevel() {
		this.nestingLevel--;
	}

	public int getOffset() {
		int res = parOffset > 0 ? parOffset++ : offset;
		return res;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void incOffset() {
		this.offset++;
	}

	public void decOffset() {
		if (parOffset <= 0)
			this.offset--;
	}

	// return the list of the class
	public ArrayList<String> getListOfClass() {
		return listOfClass;
	}

	// add class to arrayList
	public void addClass(String id) {
		listOfClass.add(id);
	}

	// return TRUE if class doesn't appear in the symble table
	public boolean verifyClass(String cl) {
		return symTable.get(GLOBAL_SCOPE).get(cl) == null;
	}

	// This methods retrieve the fields and method of class passed
	// as parameter.
	// Keep in mind that is deleted the hashmap inside the ArrayList symtable but
	// not the entry
	public ClassDecNode getClassFromST(String className) {
		ClassDecNode cn = null;
		STentry classEntry = symTable.get(GLOBAL_SCOPE).get(className);

		if (classEntry != null)
			cn = (ClassDecNode) classEntry.getType();

		return cn;
	}

	// debug method
	public void printSymTable(int level) {
		System.out.println("+===============================");
		System.out.println("| SYMBOL TABLE");
		System.out.println("+----------------------------");
		System.out.println("| Nesting Level: " + level);
		System.out.println("+===============================");
		for (String key : this.symTable.get(level).keySet()) {
			STentry st = this.symTable.get(level).get(key);
			System.out.println("| Symbol: " + key);
			System.out.println(st.toPrint("| "));
			System.out.println("| offset: " + st.getOffset());
			System.out.println("+----------------------------");
		}
		System.out.println("    |\n    |\n    V");
	}

	// livello ambiente con dichiarazioni piu' esterno è 0 (prima posizione
	// ArrayList) invece che 1 (slides)
	// il "fronte" della lista di tabelle è symTable.get(nestingLevel)

}
