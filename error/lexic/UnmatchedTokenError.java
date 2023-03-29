package error.lexic;

public class UnmatchedTokenError extends LexicalError {
	public UnmatchedTokenError(int line, int column, String lexema) {
		super(line, column, "TOKEN INCOMPLETO O DESCONOCIDO: " + lexema);
	}
}
