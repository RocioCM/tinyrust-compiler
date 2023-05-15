package semantic_analyzer.ast;

import semantic_analyzer.symbol_table.TableElement;
import semantic_analyzer.types.Type;

public abstract class ExpressionNode implements TableElement {
	private Type resolveType; // Tipo de dato que la expresión resolverá.

	public ExpressionNode() {
		this.resolveType = null;
	}

	public ExpressionNode(Type resolveType) {
		this.resolveType = resolveType;
	}

	public Type resolveType() {
		return resolveType;
	}
}
