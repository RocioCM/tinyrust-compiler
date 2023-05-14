package semantic_analyzer.types;

import semantic_analyzer.ast.ExpressionNode;

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
}
