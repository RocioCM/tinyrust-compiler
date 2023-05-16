package semantic_analyzer.ast;

import semantic_analyzer.types.Type;
import util.Json;

public class ChainedConstructorArrayAccessNode extends AccessNode {
	private Type elementsType;
	private ExpressionNode size;

	public ChainedConstructorArrayAccessNode(Type elementsType, ExpressionNode size) {
		this.elementsType = elementsType;
		this.size = size;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "constructor arreglo");
		json.addAttr("tipo-dato", elementsType);
		json.addAttr("tamaño", size);
		return json.toString();
	}
}
