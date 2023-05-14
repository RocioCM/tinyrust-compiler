package semantic_analyzer.ast;

import semantic_analyzer.types.Type;

public abstract class OperandNode extends ExpressionNode {

	public Type resolveType() {
		// TODO: implement this in the subclasses
		return null;
	}
}
