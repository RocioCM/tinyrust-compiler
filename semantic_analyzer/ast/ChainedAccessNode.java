package semantic_analyzer.ast;

import util.Json;

public class ChainedAccessNode extends AccessNode {
	protected ExpressionNode accessedEntity;
	protected AccessNode chainedAccess;

	public ChainedAccessNode(ExpressionNode accessedEntity, AccessNode chainedAccess) {
		this.accessedEntity = accessedEntity;
		this.chainedAccess = chainedAccess;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "acceso variable simple");
		json.addAttr("entidad-accedida", accessedEntity);
		json.addAttr("encadenado", chainedAccess);
		return json.toString();
	}

	public ExpressionNode accessedEntity() {
		return accessedEntity;
	}

	public AccessNode chainedAccess() {
		return chainedAccess;
	}

}
