package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Json;

public class SimpleExpressionNode extends SentenceNode {
	private ExpressionNode expression;

	public SimpleExpressionNode(ExpressionNode expression, Location loc) {
		super(loc);
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
