package semantic_analyzer.ast;

import error.semantic.ASTError;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Json;

public class SimpleExpressionNode extends SentenceNode {
	private ExpressionNode expression;

	public SimpleExpressionNode(ExpressionNode expression) {
		this.expression = expression;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo-sentencia", "Sentencia-Simple");
		json.addAttr("expresion", expression);
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		expression.validate(ts);
	}
}
