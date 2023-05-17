package semantic_analyzer.ast;

import error.semantic.ASTError;
import error.semantic.UnmatchedTypeError;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;

public abstract class ExpressionNode implements Node {
	private Type resolveType; // Tipo de dato que la expresión resolverá. Puede o no saberse a priori.
	private Type expectedResolveType; // Tipo de dato que la expresión debería resolver
																		// para ser válida en su contexto.

	public ExpressionNode() {
		this.resolveType = null;
		this.expectedResolveType = null;
	}

	public ExpressionNode(Type resolveType) {
		this.resolveType = resolveType;
		this.expectedResolveType = null;
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		if (resolveType == null) {
			throw new ASTError(0, 0,
					"ERROR INTERNO: SE ESPERABA QUE LA EXPRESION TUVIERA UN TIPO RESUELTO PARA ESTE MOMENTO, PERO SU TIPO ES null.");
			// TODO LINES
		}
		if (expectedResolveType != null
				&& !resolveType.equals(expectedResolveType)) {
			throw new UnmatchedTypeError(0, 0, expectedResolveType, resolveType); // TODO LINES
		}
	}

	public Type resolveType() {
		return resolveType;
	}

	public Type getExpectedResolveType() {
		return this.expectedResolveType;
	}

	public void setResolveType(Type resolveType) {
		this.resolveType = resolveType;
	}

	public void setExpectedResolveType(Type expectedResolveType) {
		this.expectedResolveType = expectedResolveType;
	}
}
