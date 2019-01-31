package lib;

import java.util.HashMap;

import ast.*;

public class FOOLlib {

	private static int labCount = 0;
	private static int funLabCount = 0;
	private static int ClassLabCount = 0;
	private static String funCode = "";
	private static String classCode = "";
	private static String actual = "";

	// valuta se il tipo "a" <= al tipo "b", dove "a" e "b" sono tipi di base:
	// int o bool
	public static boolean isSubtype(Node a, Node b) {
		
		return a.getClass().equals(b.getClass()); // ||
	}

	public static String freshLabel() {
		return "label" + (labCount++);
	}

	public static String freshFunLabel() {
		return "function" + (funLabCount++);
	}

	public static String freshClassLabel() {
		actual = "Class" + (ClassLabCount);
		return "Class" + (ClassLabCount++);
	}

	public static String getActual() {
		return actual;
	}

	public static void putCode(String c) {
		funCode += "\n" + c; // aggiunge una linea vuota di separazione prima di funzione
	}

	public static void putCodeClass(String c) {
		classCode += "\n" + c; // aggiunge una linea vuota di separazione prima di funzione
	}

	public static String getCode() {
		return funCode;
	}

	public static String getCodeClass() {
		return classCode;
	}

	// Dati due id di classi e la subclass table, controlla se la prima è sottotipo
	// della seconda
	public static boolean isSubtype(String idleft, String idright, HashMap<String, String> subclass) {
		String arg = subclass.get(idright);
		// Se sono uguali, restituisco true
		if (idleft.equals(idright))
			return true;
		else if (arg == null)
			// Se arg è null, la classe non è sottotipo di nessuno: restituisco false
			return false;
		else {
			// Richiamo ricorsivamente la funzione per risalire la catena
			// Serve per il caso in cui dobbiamo controllare che A <: C, e A <: B <: C
			return isSubtype(idleft, arg, subclass);
		}
	}

}