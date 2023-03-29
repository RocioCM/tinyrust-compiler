package error.lexic;

public class BadIdentifierError extends LexicalError {

	public BadIdentifierError(int line, int column, String identifier) {
		super(line, column, "IDENTIFICADOR NO VALIDO: " + identifier);
	}

}
