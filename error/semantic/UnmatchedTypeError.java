package error.semantic;

import semantic_analyzer.types.Type;

public class UnmatchedTypeError extends ASTError {

	public UnmatchedTypeError(int line, int column, Type expected, Type resolved) {
		super(line, column,
				"SE ESPERABA UNA EXPRESION DE TIPO " + expected.type() + " PERO SE ENCONTRO UNA EXPRESION DE TIPO " + resolved);
	}

}
