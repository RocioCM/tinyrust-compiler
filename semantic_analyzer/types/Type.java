package semantic_analyzer.types;

import error.semantic.ASTError;
import error.semantic.UnmatchedTypeError;
import semantic_analyzer.ast.ExpressionNode;
import semantic_analyzer.symbol_table.SymbolTable;

// TODO: why this extends from expression? Porque el constructor de arreglos lo uso.
public abstract class Type extends ExpressionNode {
	private String type;

	protected Type(String type) {
		this.type = type;
	}

	@Override
	public String toJson() {
		return '"' + type + '"';
	}

	public String type() {
		return type;
	}

	public boolean equals(Type t) {
		return this.type.equals(t.type());
	}

	public void validate(SymbolTable ts) throws ASTError {
		if (super.getExpectedResolveType() != null && !this.equals(super.getExpectedResolveType())) {
			// Tirar una excepción si esta instancia de Type no es del tipo esperado para
			// esta expresión.
			throw new UnmatchedTypeError(0, 0, super.getExpectedResolveType(), this); // TODO LINES
		}
		super.setResolveType(this); // Actualizar el tipo resuelto para la expresión.

		// Nota: esta subclase no debería llamar a validate() de su superclase,
		// ya que puede fallar inesperadamente al comparar tipos consigo misma.
	}
}
