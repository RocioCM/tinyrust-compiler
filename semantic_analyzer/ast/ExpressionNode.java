package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import error.semantic.sentences.InternalError;
import error.semantic.sentences.UnmatchedTypeError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Type;

public abstract class ExpressionNode implements Node {
	private Type resolveType; // Tipo de dato que la expresión resolverá. Puede o no saberse a priori.
	private Type expectedResolveType; // Tipo de dato que la expresión debería resolver
										// para ser válida en su contexto.
	protected Location loc; // Declaration location.

	public ExpressionNode(Location loc) {
		this.loc = loc;
		this.resolveType = null;
		this.expectedResolveType = null;
	}

	public ExpressionNode(Type resolveType, Location loc) {
		this.resolveType = resolveType;
		this.expectedResolveType = null;
		this.loc = loc;
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		if (resolveType == null) {
			throw new InternalError(loc,
					"ERROR INTERNO: SE ESPERABA QUE LA EXPRESION TUVIERA UN TIPO RESUELTO PARA ESTE MOMENTO, PERO SU TIPO ES null.");
		}
		if (expectedResolveType != null
				&& !resolveType.equals(expectedResolveType)) {
			throw new UnmatchedTypeError(loc, expectedResolveType, resolveType);
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
