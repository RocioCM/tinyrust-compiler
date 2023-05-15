package semantic_analyzer.ast;

import util.Json;

public class AssignNode extends SentenceNode {
	AccessNode leftSide;
	ExpressionNode rightSide;

	public AssignNode(AccessNode leftSide, ExpressionNode rightSide) {
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo-sentencia", "Asignacion");
		json.addAttr("izquierda", leftSide);
		json.addAttr("derecha", rightSide);
		return json.toString();
	}

}
