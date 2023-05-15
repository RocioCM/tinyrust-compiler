package semantic_analyzer.ast;

import util.Json;

public class AccessSimpleChain extends AccessNode {
	private VariableNode variable;
	private AccessNode chainedAccess;

	public AccessSimpleChain(VariableNode variable) {
		this.variable = variable;
		this.chainedAccess = null; // No hay más accesos encadenados.
	}

	public AccessSimpleChain(VariableNode variable, AccessNode chainedAccess) {
		this.variable = variable;
		this.chainedAccess = chainedAccess;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "acceso variable simple");
		json.addAttr("nombre-variable", variable);
		json.addAttr("encadenado", chainedAccess);
		return json.toString();
	}

}
