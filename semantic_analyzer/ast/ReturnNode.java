package semantic_analyzer.ast;

import semantic_analyzer.types.Void;
import util.Json;

public class ReturnNode extends SentenceNode {
	private ExpressionNode returnValue;

	public ReturnNode() {
		this.returnValue = new Void();
	}

	public ReturnNode(ExpressionNode expression) {
		this.returnValue = expression;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo-sentencia", "Return");
		json.addAttr("expresion-retorno", returnValue);
		return json.toString();
	}
}
