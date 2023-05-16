package semantic_analyzer.ast;

import util.Json;

public class ChainedMethodAccessNode extends ChainedAccessNode {
	protected TreeList<ExpressionNode> arguments;

	public ChainedMethodAccessNode(ExpressionNode accessedEntity, TreeList<ExpressionNode> arguments,
			AccessNode chainedAccess) {
		super(accessedEntity, chainedAccess);
		this.arguments = arguments;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "acceso metodo simple");
		json.addAttr("nombre-metodo", super.accessedEntity());
		json.addAttr("argumentos", arguments);
		json.addAttr("encadenado", super.chainedAccess());
		return json.toString();
	}
}
