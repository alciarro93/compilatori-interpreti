package ast;

import java.util.ArrayList;
import java.util.HashMap;

import util.Environment;
import util.SemanticError;
import util.Util;

public class ClassDecNode implements Node {

	private String id;
	private String impl;
	private ArrayList<Node> fieldsList;
	private ArrayList<Node> methodsList;

	public ClassDecNode(String id) {
		this.id = id;
		this.impl = "";
		this.fieldsList = new ArrayList<Node>();
		this.methodsList = new ArrayList<Node>();
	}

	public void setImpl(String impl) {
		this.impl = impl;
	}

	public void addFieldToClass(Node n) {
		this.fieldsList.add(n);
	}

	public void addMethodToClass(Node n) {
		this.methodsList.add(n);
	}

	public String getId() {
		return id;
	}

	public String getImpl() {
		return impl;
	}

	public ArrayList<Node> getMethodsList() {
		return methodsList;
	}
	// TODO delete
	// public ArrayList<Node> getFieldsOfMethod(String id){
	// ArrayList<Node> fieldsOfMethod = new ArrayList<Node>();
	//
	//
	// for(int i = 0; i < methodsList.size(); i++) {
	// String name = ((FunNode) methodsList.get(i)).getId();
	// if(name.equals(id)) {
	// fieldsOfMethod.addAll(((FunNode) methodsList.get(i)).getParams());
	// }
	// }
	// return fieldsOfMethod;
	//
	// }

	public ArrayList<Node> getFieldsList() {
		return fieldsList;
	}

	// TODO i pedico delete
	// public void incFields(ArrayList<Node> currentElement, ArrayList<Node>
	// superElement) {
	//
	// for (int i = 0; i < superElement.size(); i++) {
	//
	// VarDecNode sE = (VarDecNode) superElement.get(i);
	// boolean override = false;
	//
	// for (int j = 0; j < currentElement.size(); j++) {
	// VarDecNode e = (VarDecNode) currentElement.get(j);
	// if (sE.getId().equals(e.getId())) {
	// override = true;
	// }
	//
	// if (j == currentElement.size() - 1 && !override) {
	// currentElement.add(superElement.get(i));
	// break;
	// }
	//
	// }
	// }
	//
	// }

	private void checkFieldsOverride(ArrayList<Node> superClassField) {
		for (Node sn : superClassField) {
			VarDecNode sf = (VarDecNode) sn;
			boolean overrided = false;
			for (int j = 0; j < fieldsList.size(); j++) {
				VarDecNode cf = (VarDecNode) fieldsList.get(j);
				if (sf.getId().equals(cf.getId())) {
					overrided = true;
					break;
				}
			}
			if (!overrided) {
				fieldsList.add(sn);
			}
		}
	}

	// TODO i pedico delete
	// public void incMethods(ArrayList<Node> currentElement, ArrayList<Node>
	// superElement) {
	//
	// for (int i = 0; i < superElement.size(); i++) {
	//
	// FunNode sE = (FunNode) superElement.get(i);
	// boolean override = false;
	//
	// for (int j = 0; j < currentElement.size(); j++) {
	// FunNode e = (FunNode) currentElement.get(j);
	// if (sE.getId().equals(e.getId())) {
	// override = true;
	// }
	//
	// if (j == currentElement.size() - 1 && !override) {
	// currentElement.add(superElement.get(i));
	// break;
	// }
	//
	// }
	// }
	//
	// }

	private void checkMethodsOverride(ArrayList<Node> superClassMethods) {
		for (Node sn : superClassMethods) {
			FunNode sm = (FunNode) sn;
			boolean overrided = false;
			for (int j = 0; j < methodsList.size(); j++) {
				FunNode cm = (FunNode) methodsList.get(j);
				if (sm.getId().equals(cm.getId())) {
					overrided = true;
					break;
				}
			}
			if (!overrided) {
				methodsList.add(sn);
			}
		}
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(Environment env) {

		// TODO: settare environment della classe per poter implementare il this in
		// modo che capisca che si trova all'interno di una dichiarazione di una classe

		ArrayList<SemanticError> res = new ArrayList<>();

		env.setClassEnvironment(this.id);

		HashMap<String, STentry> hmn = new HashMap<String, STentry>();
		env.getSymTable().add(hmn);

		boolean inheritance = false; // default: NO INHERITANCE
		if (!impl.equals(""))
			inheritance = true;

		// if inheritance something then
		if (inheritance) {

			if (id.equals(impl)) {
				res.add(new SemanticError(id + " cannot extend itself."));
			} else if (env.verifyClass(impl)) {
				res.add(new SemanticError("Class " + impl + " doesn't exist."));
			} else {

				// retrieve the superclass node
				ClassDecNode superclass = env.getClassFromST(impl);

				// override attributes
				ArrayList<Node> fieldsSuperClass = superclass.getFieldsList();

				// check if fields are overrided or not
				checkFieldsOverride(fieldsSuperClass);

				// TODO delete
				// System.out.println("-----------------------------");
				// Util.printArrayListNode(fieldsList);

				// override methods
				ArrayList<Node> methodsListSuperClass = superclass.getMethodsList();

				// check if methods are overrided or not
				checkMethodsOverride(methodsListSuperClass);

				// TODO delete
				// Util.printArrayListNode(methodsList);
				// System.out.println("-----------------------------");
			}
		}

		// param class
		// the list of params of class are inside the scope 0 + 1 - always
		env.setNestingLevel(env.GLOBAL_SCOPE + 1);
		// enable use of parOffset
		env.setParOffset(1);
		for (Node par : fieldsList) {
			res.addAll(par.checkSemantics(env));
		}
		// disable use of parOffset
		env.setParOffset(-1);

		// checksemantic (recursive) for all methods
		for (Node method : methodsList) {
			res.addAll(method.checkSemantics(env));
		}

		env.setClassEnvironment("");
		env.getSymTable().remove(env.getNestingLevel());
		env.decNestingLevel();

		return res;
	}

	@Override
	public String toPrint(String s) {

		String varD = "";
		for (Node dec : fieldsList)
			varD += dec.toPrint(s + "  ");
		String fun = "";
		for (Node f : methodsList)
			fun += f.toPrint(s + "  ");

		if (impl.equals("")) {

			return s + "Classdec:" + id + "\n" + varD + fun + "\n";
		} else {

			return s + "Classdec:" + id + "\n" + s + "Implements:" + impl + "\n" + varD + fun + "\n";
		}
		// return s+"ClassExp\n" + declstr + exp.toPrint(s+" ") ;

	}

	@Override
	public Node typeCheck() {

		// TODO gestire l'implements
		for (Node field : fieldsList) {
			field.typeCheck();
		}
		for (Node method : methodsList) {
			method.typeCheck();
		}
		return new ClassTypeNode(this.id);
	}

	@Override
	public String codeGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

}
