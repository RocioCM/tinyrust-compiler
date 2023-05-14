package semantic_analyzer.ast;

public class BinaryExpressionNode extends ExpressionNode {
	private OperandNode leftOperand;
	private OperandNode rightOperand;
	private String operator;
	private String expectedOperandType;

	public BinaryExpressionNode(OperandNode leftOperand, OperandNode rightOperand, String operator,
			String expectedOperandType) {
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		this.operator = operator;
		this.expectedOperandType = expectedOperandType;
	}

	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'toJson'");
	}
}
