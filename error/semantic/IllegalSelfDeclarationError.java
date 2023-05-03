package error.semantic;

public class IllegalSelfDeclarationError extends SemanticalError {

	public IllegalSelfDeclarationError() {
		super("NO ESTA PERMITIDO ASIGNAR EL IDENTIFICADOR \"self\" A ATRIBUTOS, VARIABLES O METODOS.");
	}

}
