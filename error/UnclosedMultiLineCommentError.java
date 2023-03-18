package error;

public class UnclosedMultiLineCommentError extends LexicalError {

	public UnclosedMultiLineCommentError(int line, int column) {
		super(line, column, "COMENTARIO MULTILINEA SIN CERRAR");
	}

}
