package error.semantic;

public class BadFormedConstructorError extends SemanticalError {
	public BadFormedConstructorError() {
		super("LA SINTAXIS CORRECTA PARA DECLARAR EL CONSTRUCTOR create ES \"create(<parametros>) {<sentencias>}\".");
	}
}
