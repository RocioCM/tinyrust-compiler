package semantic_analyzer.types;

// import error.semantic.sentences.ASTError;
// import error.semantic.sentences.UnmatchedTypeError;
// import semantic_analyzer.ast.ExpressionNode;
// import semantic_analyzer.symbol_table.Location;
// import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.symbol_table.TableElement;

public abstract class Type implements TableElement {
	// Type extiende de ExpressionNode ya que Void y Array pueden usarse como
	// expresiones en la TS.
	private String type;

	protected Type(String type) {
		// TODO: check this is not outputting.
		// super(new Location(-1, -1));
		this.type = type;
	}

	// protected Type(String type, Location loc) {
	// // super(loc);
	// this.type = type;
	// }

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

	/// TODO: remove this commented code.

	// public void validate(SymbolTable ts) throws ASTError {
	// if (super.getExpectedResolveType() != null &&
	// !this.equals(super.getExpectedResolveType())) {
	// // Tirar una excepción si esta instancia de Type no es del tipo esperado para
	// // esta expresión.
	// throw new UnmatchedTypeError(loc, super.getExpectedResolveType(), this);
	// }
	// super.setResolveType(this); // Actualizar el tipo resuelto para la expresión.

	// // Nota: esta subclase no debería llamar a validate() de su superclase,
	// // ya que puede fallar inesperadamente al comparar tipos consigo misma.
	// }
}
