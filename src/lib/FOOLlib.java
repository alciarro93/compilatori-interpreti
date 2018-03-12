package lib;

import java.util.ArrayList;

import ast.*;
import util.TypeCheckError;

public class FOOLlib {

	private static int labCount = 0;

	private static int funLabCount = 0;

	private static String funCode = "";

	private static ArrayList<TypeCheckError> typeErr = new ArrayList<>();

	/*
	 * in which the two operands will be evaluated to boolean and you will apply an
	 * logical OR on them only if the first evaluates to false. If the first operand
	 * evaluates to true the second will not be evaluated and true will be retured.
	 */

	// valuta se il tipo "a" e' <: al tipo "b", dove "a" e "b" sono tipi di base:
	// int o bool
	// public static boolean isSubtype(Node a, Node b) {
	// return a.getClass().equals(b.getClass()) || ((a instanceof BoolTypeNode) &&
	// (b instanceof IntTypeNode)); //
	// }

	// https://stackoverflow.com/questions/13308102/instanceof-class-parameter
	// valuta se il tipo "a" e' <: al tipo "b", dove "a" e "b" sono tipi di base:
	// int o bool
	public static boolean isSubtype(Node a, Node b) {

		// DEBUG
		// System.out.println(a.getClass() + " " + b.getClass());
		boolean result = false;

		if (a instanceof ClassDecNode && b instanceof ClassDecNode) {
			ClassDecNode c1 = (ClassDecNode) a;
			ClassDecNode c2 = (ClassDecNode) b;

			// a and b are of same class
			if ((c1.getId().compareTo(c2.getId()) == 0)) {
				return true;
			}
			// a extend b
			if (c1.getImpl().compareTo(c2.getId()) == 0) {
				// check fields overrided type
				for (Node f2 : c2.getFieldsList()) {
					for (Node f1 : c1.getFieldsList()) {
						// check if c2 field is overridden in c1
						if (((VarDecNode) f2).getId().compareTo(((VarDecNode) f1).getId()) == 0) {
							if (isSubtype(f1.typeCheck(), f2.typeCheck())) {
								result = true;
							} else {
								return false;
							}
						}
					}
				}
				// check methods overrided type
				for (Node m2 : c2.getMethodsList()) {
					for (Node m1 : c1.getMethodsList()) {
						// check if c2 method is overridden in c1
						if (((FunNode) m2).getId().compareTo(((VarDecNode) m1).getId()) == 0) {
							if (isSubtype(m1.typeCheck(), m2.typeCheck())) {
								result = true;
							} else {
								return false;
							}
						}
					}
				}
			}

			// TODO implementare le altre condizioni delle specifiche
		} else {
			// Integer and boolean
			if (a.getClass() == b.getClass())
				result = true;
		}
		return result;

	}

	public static ArrayList<TypeCheckError> getTypeCheckError() {
		return typeErr;
	}

	public static void setTypeCheckError(String err) {
		typeErr.add(new TypeCheckError(err));
	}

	public static String freshLabel() {
		return "label" + (labCount++);
	}

	public static String freshFunLabel() {
		return "function" + (funLabCount++);
	}

	public static void putCode(String c) {
		funCode += "\n" + c; // aggiunge una linea vuota di separazione prima di funzione
	}

	public static String getCode() {
		return funCode;
	}

}