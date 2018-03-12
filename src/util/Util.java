package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

import ast.Node;
import ast.STentry;
import ast.VarAsmNode;

public class Util {

	// method that print an ArrayList of Node
	public static void printArrayListNode(ArrayList<Node> t) {
		if (t.isEmpty())
			System.out.println("ArrayList is empty");
		else
			for (int i = 0; i < t.size(); i++)
				System.out.println(t.get(i).toPrint(""));

	}

	// method to print HashMap
	public static void printHashMap(HashMap<String, STentry> hm, Object cl) {
		System.out.println("------------------------------");
		System.out.println("HashMap --> " + cl.getClass().getSimpleName());
		if (hm.isEmpty()) {
			System.out.println("Hashmap is empty");
		} else {

			for (String name : hm.keySet()) {
				String key = name.toString();
				String value = hm.get(name).toPrint("");
				System.out.println(key + " " + value);

			}
		}

		System.out.println("------------------------------");
	}

	// method to print ArrayList of HashMap
	public static void printSymbolTable(ArrayList<HashMap<String, STentry>> symTable, Object cl) {
		System.out.println("##########################################");
		System.out.println("ArrayList<Hashmap> --> " + cl.getClass().getSimpleName());

		if (symTable.isEmpty()) {
			System.out.println("SymbolTable is empty");
		} else {

			for (int i = 0; i < symTable.size(); i++) {

				for (String name : symTable.get(i).keySet()) {
					String key = name.toString();
					String value = symTable.get(i).get(name).toPrint("");					
					System.out.println(key + "\n" + value);
				}

			}
		}
		System.out.println("##########################################");

	}
	
	

	public static void printSTentry(STentry entry, Object cl) {
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("Entry --> " + cl.getClass().getSimpleName());
		System.out.println(entry.toPrint(""));
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++");

	}

}
