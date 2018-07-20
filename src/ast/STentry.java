package ast;

import java.util.List;

import parser.FOOLParser.FunContext;
import parser.FOOLParser.VardecContext;

public class STentry {

	private int nl;
	private Node type;
	private ClassTypeNode type1 = null;
	private int offset;
	private List<VardecContext> vardec;
	private List<FunContext> funlist;

	public STentry(int n, int os) {
		nl = n;
		offset = os;
	}

	public STentry(int n, ClassTypeNode t, int os, List<VardecContext> r, List<FunContext> f) {
		nl = n;
		type1 = t;
		offset = os;
		vardec = r;
		funlist = f;
	}

	public STentry(int n, Node t, int os) {
		nl = n;
		type = t;
		offset = os;
	}

	public void addType(Node t) {
		type = t;
	}

	public Node getType() {
		return type;
	}

	public ClassTypeNode getType1() {
		return type1;
	}

	public List<VardecContext> getListContext() {
		return vardec;
	}

	public List<FunContext> getFunContext() {
		return funlist;
	}

	public int getOffset() {
		return offset;
	}

	public int getNestinglevel() {
		return nl;
	}

	public String toPrint(String s) { //
		return s + "STentry: nestlev " + Integer.toString(nl) + "\n" + s + "STentry: type\n" + type.toPrint(s + "  ")
				+ s + "STentry: offset " + Integer.toString(offset);
	}
}