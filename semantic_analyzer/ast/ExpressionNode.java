package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import error.semantic.sentences.InternalError;
import error.semantic.sentences.UnmatchedTypeError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Array;
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

	/**
	 * Valida que el tipo resuelto para la expresión coincide con el tipo esperado
	 * para esta expresión dado su contexto. Si no hay un tipo esperado definido,
	 * cualquier tipo resuelto es aceptado.
	 * 
	 * @param ts - Tabla de símbolos consolidada.
	 * @throws ASTError - Si el tipo resuelto no coincide con el tipo esperado.
	 */
	public void validateType(SymbolTable ts) throws ASTError {
		if (resolveType == null) {
			throw new InternalError(loc,
					"SE ESPERABA QUE LA EXPRESION TUVIERA UN TIPO RESUELTO PARA ESTE MOMENTO, PERO SU TIPO ES null.");
		}
		if (expectedResolveType != null
				&& !expectedResolveType.equals(resolveType)
				&& ((resolveType instanceof Array || !ts.isSubclass(resolveType, expectedResolveType)))) {
			// Si el tipo no coincide porque es una subclase, no se lanza excepción.
			// Si el tipo no coincide y es un arreglo, es porque el subtipo no
			// coincide y sí se lanza excepción.
			throw new UnmatchedTypeError(loc, expectedResolveType, resolveType);
		}
	}

	@Override
	abstract public void validate(SymbolTable ts) throws ASTError;

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
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
