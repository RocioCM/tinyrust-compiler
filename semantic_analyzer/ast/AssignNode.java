package semantic_analyzer.ast;

import util.Json;

public class AssignNode extends SentenceNode {
	ExpressionNode leftSide; /// TODO: put the correct type.
	ExpressionNode rightSide;

	public AssignNode(ExpressionNode leftSide, ExpressionNode rightSide) {
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
