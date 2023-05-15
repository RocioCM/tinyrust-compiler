package semantic_analyzer.ast;

import semantic_analyzer.symbol_table.TableElement;
import semantic_analyzer.types.Type;

public abstract class ExpressionNode implements TableElement {
	private Type expectedResolvedType;
}
