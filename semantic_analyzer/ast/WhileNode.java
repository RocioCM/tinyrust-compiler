package semantic_analyzer.ast;

import util.Json;

public class WhileNode extends SentenceNode {
	private ExpressionNode condition;
	private SentenceNode block;

	public WhileNode(ExpressionNode condition, SentenceNode block) {
		this.condition = condition;
		this.block = block;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo-sentencia", "While");
		json.addAttr("condicion", condition);
		json.addAttr("sentencia", block);
		return json.toString();
	}
}
