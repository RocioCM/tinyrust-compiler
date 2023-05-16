package semantic_analyzer.ast;

import semantic_analyzer.types.Type;
import util.Json;

public class ChainedStaticMethodAccessNode extends ChainedMethodAccessNode {
	private Type classType;

	public ChainedStaticMethodAccessNode(Type classType, ChainedMethodAccessNode methodAccess,
			AccessNode chainedAccess) {
		super(methodAccess.accessedEntity, methodAccess.arguments,
				new ChainedAccessNode(methodAccess.chainedAccess, chainedAccess));
		this.classType = classType;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "acceso metodo estatico");
		json.addAttr("clase", classType);
		json.addAttr("nombre-metodo", super.accessedEntity());
		json.addAttr("argumentos", arguments);
		return json.toString();
	}

}
