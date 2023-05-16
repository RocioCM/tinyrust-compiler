package semantic_analyzer.ast;

import util.Json;

public class ChainedConstructorAccessNode extends ChainedMethodAccessNode {
	public ChainedConstructorAccessNode(ExpressionNode accessedEntity, TreeList<ExpressionNode> arguments,
			AccessNode chainedAccess) {
		super(accessedEntity, arguments, chainedAccess);
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "llamada constructor");
		json.addAttr("clase", super.accessedEntity());
		json.addAttr("argumentos", arguments);
		json.addAttr("encadenado", super.chainedAccess());
		return json.toString();
	}
}
