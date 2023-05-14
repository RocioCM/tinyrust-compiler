package semantic_analyzer.ast;

import util.Json;

public class IfElseNode extends SentenceNode {
	private ExpressionNode condition;
	private SentenceNode block;
	private SentenceNode elseBlock;

	public IfElseNode(ExpressionNode condition, SentenceNode block, SentenceNode elseBlock) {
		this.condition = condition;
		this.block = block;
		this.elseBlock = elseBlock;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo-sentencia", "If-Else");
		json.addAttr("condicion", condition);
		json.addAttr("sentencia-if", block);
		json.addAttr("sentencia-else", elseBlock);
		return json.toString();
	}
}
