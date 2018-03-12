package ast;

public class STentry {

	private int nl; // nesting Level = current scope
	private Node type;
	private int offset;

	
	public STentry(int n, int os) {
		nl = n;
		offset = os;
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

	public int getOffset() {
		return offset;
	}

	public int getNestinglevel() {
		return nl;
	}

	public String toPrint(String s) { //
		if (type == null) {
			return s + "STentry: nestlev " + Integer.toString(nl) + "\n" + s + "STentry: offset "
					+ Integer.toString(offset) + "\n";
		} else {

			return s + "STentry: nestlev " + Integer.toString(nl) + "\n" + s + "STentry: type" + type.toPrint(s + "  ")
					+ s + "STentry: offset " + Integer.toString(offset) + "\n";
		}
	}
}