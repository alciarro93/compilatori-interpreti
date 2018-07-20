package ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parser.FOOLParser.ClassdecContext;
import parser.FOOLParser.VardecContext;
import util.Environment;
import util.SemanticError;
import lib.FOOLlib;

public class ClassdecNode implements Node {

	private ArrayList<String> id;
	private ArrayList<Node> type;
	private Environment envSaved = null;
	private String insideclass = null;

	public ClassdecNode(ArrayList<String> i, ArrayList<Node> t) {
		id = i;
		type = t;

	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		// create result list
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		HashMap<String, STentry> hm = env.symTable.get(env.nestingLevel);

		for (int y = 0; y < type.size(); y++) {
			STentry entry = new STentry(env.nestingLevel, type.get(y), env.offset++); // separo introducendo "entry"
			if (entry == null) {
				System.out.println("Entry null in classDecNode");
			}
			// inserisco la variabile e l'entry corrispondente nella symbol table
			if (hm.put(id.get(y), entry) != null) {
				// esiste già  un id uguale
				res.add(new SemanticError("Var id " + id.get(y) + " already declared"));
			}

		}
		if (env.varSub != null) {
			// Metto le variabili della superclasse nella symboltable
			FoolVisitorImpl m = new FoolVisitorImpl();
			for (VardecContext v : env.varSub) {
				// System.out.println("var=" + v.ID().getText());
				if (hm.get(v.ID().getText()) == null) {
					STentry s = new STentry(env.nestingLevel, m.visitType(v.type()), env.offset++);
					hm.put(v.ID().getText(), s);
				}
			}
		}
		insideclass = env.insideClass;
		env.offsetAfterDec = env.offset;
		envSaved = env;
		return res;
	}

	public String toPrint(String s) {
		String decList = "";
		for (int k = 0; k < type.size(); k++) {
			decList = decList + "Var:" + id.get(k) + "\n" + type.get(k).toPrint(s + "  ");
		}
		return decList;
	}

	// valore di ritorno non utilizzato
	public Node typeCheck() {
		boolean ok = true;
		for (String i : id) {
			HashMap<String, STentry> hm2 = envSaved.symTableL0;
			// Per ogni id, qualora sia una classe, recupero l'id della superclasse
			String superc = hm2.get(insideclass).getType1().getSuper();
			// Controllo se è dichiarato un campo con le stesso id in qualche superclasse:
			// se si, controllo se i tipi sono compatibili
			ok = searchInSuper(i, superc, hm2);

			if (!ok) {
				// I tipi non sono compatibili
				System.out.println(
						"The id " + i + " in the class " + insideclass + " cannot override the id in the superclass");
				System.exit(0);
			}
		}
		return null;
	}

	public String codeGeneration() {
		return "";
	}

	//
	private boolean searchInSuper(String id, String superc, HashMap<String, STentry> hm2) {
		// Se superc è null, il campo non era una classe oppure abbiamo già  controllato
		// tutte le superclassi
		if (superc == null)
			return true;
		else {
			for (VardecContext f : hm2.get(superc).getListContext()) {
				// Se trovo nella superclasse un campo con lo stesso id, e i tipi non sono
				// comptabili, restituisco false
				if ((f.ID().getText().equals(id)) && !(subID(f, insideclass))) {
					return false;
				} else {
					// System.out.println(f.ID().getText()+" and "+id);
				}

			}

		}
		// Altrimenti richiamo ricorsivamente la funzione per risalire la catena di
		// superclassi ( caso A <: B <: C)
		return searchInSuper(id, envSaved.symTableL0.get(superc).getType1().getSuper(), hm2);
	}

	// Data un VardecContext e un id di una classe, controlla se c'è un campo con lo
	// stesso id nella lista dei campi di quest'ultima
	// Nel caso in cui ci sia, controlla se i tipi sono compatibili: se lo sono
	// restituisce true, altrimenti false
	// Se non c'Ã¨ nessun campo con lo stesso id, restituisce true
	private boolean subID(VardecContext d, String idleft2) {

		// Prendo la symbol table entry della superclasse
		STentry superEntry = envSaved.symTableL0.get(idleft2);
		boolean subFunc;

		for (VardecContext f1 : superEntry.getListContext()) {

			if ((d.ID().getText().equals(f1.ID().getText()))) {
				// Se trovo un campo nella lista dei campi della superclasse con lo stesso id,
				// devo verificare che i tipi siano compatibili
				int c2 = 0;

				if (d.type().getText().equals("int") || d.type().getText().equals("bool")) {
					// Se il tipo del campo della sottoclasse è int o bool, controllo che quello
					// della superclasse sia uguale: se si, restituisco true, altrimenti false
					if (!(d.type().getText().equals(f1.type().getText()))) {
						return false;
					} else
						return true;
				} else {
					// Se il tipo non è ne int ne bool (e quindi è una classe), controllo che la
					// classe sia la stessa o un sottotipo della classe del campo della superclasse
					if (!(FOOLlib.isSubtype(f1.type().getText(), d.type().getText(), envSaved.subClassTable)))
						return false;
					else
						return true;

				}

			}
		}
		// Se non ho trovato nessun campo con lo stesso id, restituisco true
		return true;
	}

}