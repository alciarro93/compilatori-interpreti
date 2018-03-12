package util;

/**
 * 
 * @author antonio
 * This class is equal to SemanticError. It will be use for handle the type check error
 */
public class TypeCheckError {

	public final String msg;

	public TypeCheckError(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return msg;
	}
}
