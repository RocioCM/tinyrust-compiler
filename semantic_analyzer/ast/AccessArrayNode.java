package semantic_analyzer.ast;

import util.Json;

public class AccessArrayNode extends AccessNode {
	private VariableNode variable;
	private ExpressionNode indexAccess; // Índice del elemento que se quiere acceder.

	public AccessArrayNode(VariableNode variable, ExpressionNode indexAccess) {
		this.variable = variable;
		this.indexAccess = indexAccess;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "acceso arreglo simple");
		json.addAttr("nombre-variable", variable);
		json.addAttr("expresion-acceso", indexAccess);
		return json.toString();
	}

}
