package semantic_analyzer.ast;

import semantic_analyzer.types.Type;
import util.Json;

public class BinaryExpressionNode extends ExpressionNode {
	private ExpressionNode leftOperand;
	private ExpressionNode rightOperand;
	private String operator;
	private Type expectedOperandType; // Si es null, indica que ambos operandos deben ser del mismo tipo,
																		// sin importar qué tipo en específico.

	public BinaryExpressionNode(ExpressionNode rightOperand, String operator,
			Type expectedOperandType, Type resultType) {
		super(resultType);
		this.leftOperand = null;
		this.rightOperand = rightOperand;
		this.operator = operator;
		this.expectedOperandType = expectedOperandType;
	}

	public BinaryExpressionNode(ExpressionNode leftOperand, ExpressionNode rightOperand, String operator,
			Type expectedOperandType, Type resultType) {
		super(resultType);
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		this.operator = operator;
		this.expectedOperandType = expectedOperandType;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "expresion binaria");
		json.addAttr("operador", operator);
		json.addAttr("tipo-de-retorno", super.resolveType());
		if (expectedOperandType != null) {
			json.addAttr("tipo-de-operandos", expectedOperandType);
		} else {
			json.addAttr("tipo-de-operandos", "Mismo tipo");
		}
		json.addAttr("operando-derecha", leftOperand);
		json.addAttr("operando-izquierda", rightOperand);
		return json.toString();
	}

	public void setLeftOperand(ExpressionNode leftOperand) {
		this.leftOperand = leftOperand;
	}
}
