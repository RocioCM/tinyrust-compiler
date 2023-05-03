package error.semantic;

public class MultipleConstructorsError extends SemanticalError {
	public MultipleConstructorsError() {
		super("SOLO SE PERMITE DECLARAR HASTA UN CONSTRUCTOR POR CLASE.");
	}
}
