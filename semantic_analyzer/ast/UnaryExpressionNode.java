package semantic_analyzer.ast;

public class UnaryExpressionNode extends ExpressionNode {
	private OperandNode operand;
	private String operator;
	private String expectedOperandType;

	public UnaryExpressionNode(OperandNode operand, String operator, String expectedOperandType) {
		this.operand = operand;
		this.operator = operator;
		this.expectedOperandType = expectedOperandType;
	}

	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'toJson'");
	}
}
