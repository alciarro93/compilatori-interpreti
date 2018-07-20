package ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import lib.FOOLlib;
import parser.FOOLParser.ExpContext;
import parser.FOOLParser.FunContext;
import parser.FOOLParser.VardecContext;
import util.Environment;
import util.SemanticError;

public class NewExpNode implements Node {
	private String id = "";
	private List<ExpContext> exps;
	private STentry m;
	private String superclass = null;
	private Environment envSaved;
	private int nestinglevel;
	private int numberOfp;
	private List<VardecContext> varDeclaraion = null;
	private String classname = "noclass";
	private HashMap<String, STentry> symTableL0 = new HashMap<String, STentry>();
	FoolVisitorImpl app = new FoolVisitorImpl();
	FoolVisitorImpl app1 = new FoolVisitorImpl();
	List<Node> exp = new ArrayList<Node>();

	public NewExpNode(String s, List<ExpContext> l) {
		id = s;
		exps = l;
		for (ExpContext e : exps) {
			exp.add(app.visitExp(e));
		}
	}

	@Override
	public String toPrint(String indent) {
		// TODO Auto-generated method stub
		return "new instance of: " + id + "\n";
	}

	public String getID() {
		return id;
	}

	@Override
	public ClassTypeNode typeCheck() {

		int counter = 0;
		// La classe non ha campi
		if (m == null) {
			return new ClassTypeNode(id);
		}
		// Se la classe ha campi, devo controllare che i tipi siano compatibili
		for (ExpContext e : exps) {
			if (exp.get(counter).typeCheck() instanceof IntTypeNode
					|| exp.get(counter).typeCheck() instanceof BoolTypeNode) {
				if (!(FOOLlib.isSubtype(exp.get(counter).typeCheck(),
						app1.visitType((varDeclaraion.get(counter).type()))))) {
					System.out.println("incompatible value for the class constructor of " + id);
					System.exit(0);
				}
			} else {

				if (!(FOOLlib.isSubtype(varDeclaraion.get(counter).type().getText(),
						((ClassTypeNode) exp.get(counter).typeCheck()).getTypeID(), envSaved.subClassTable))) {
					System.out.println("incompatible value for the class constructor of " + id);
					System.exit(0);
				}
			}
			counter++;
		}

		return new ClassTypeNode(id, m.getListContext(), m.getFunContext(), superclass, numberOfp, varDeclaraion);
	}

	@Override
	public String codeGeneration() {
		String getAR = "";
		// FoolVisitorImpl app = new FoolVisitorImpl();
		String parList = "";
		int counter = 0;
		for (ExpContext e : exps) {
			parList += exp.get(counter).codeGeneration();
			// CARICHIAMO LE VARIABILI PASSATE AL COSTRUTTORE DELLA CLASSE (parametri del
			// new) NEL HEAP
			parList += "lhp\n" + "sw\n" + "push 1\n" + "lhp\n" + "add\n" + "shp\n";
			counter++;
		}

		for (int i = 0; i < symTableL0.get(id).getListContext().size() - exps.size(); i++) {
			parList += "push 0\n" + "lhp\n" + "sw\n" + "push 1\n" + "lhp\n" + "add\n" + "shp\n";
		}

		return parList;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {
		ArrayList<SemanticError> res = new ArrayList<SemanticError>();
		HashMap<String, STentry> hm = env.symTable.get(0);
		if (hm.get(id) == null)
			res.add(new SemanticError("Class " + id + " not declared"));
		else {
			m = hm.get(id);
			this.nestinglevel = env.nestingLevel;
			if (m.getType1().getNumberOfpar() != exps.size())
				res.add(new SemanticError("Number of parameters incorrect for class: " + id));

			for (Node e : exp) {

				res.addAll(e.checkSemantics(env));
			}
			ClassTypeNode m1 = m.getType1();
			superclass = m1.getSuper();
			numberOfp = m.getType1().getNumberOfpar();
			varDeclaraion = m.getType1().getDecLocal();
		}
		if (env.insideClass != null)
			classname = env.insideClass;
		symTableL0.putAll(env.symTableL0);

		envSaved = env;
		return res;
	}

}
