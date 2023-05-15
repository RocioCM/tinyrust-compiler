package semantic_analyzer.ast;

import semantic_analyzer.types.Type;
import util.Json;

public class UnaryExpressionNode extends ExpressionNode {
	private ExpressionNode operand;
	private String operator;
	private Type expectedOperandType;

	public UnaryExpressionNode(ExpressionNode operand, String operator, Type expectedOperandType, Type resolveType) {
		super(resolveType);
		this.operand = operand;
		this.operator = operator;
		this.expectedOperandType = expectedOperandType;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "expresion unaria");
		json.addAttr("operador", operator);
		json.addAttr("tipo-de-retorno", super.resolveType());
		json.addAttr("tipo-operando", expectedOperandType);
		json.addAttr("operando", operand);
		return json.toString();
	}
}
