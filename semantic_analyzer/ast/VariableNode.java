package semantic_analyzer.ast;

public class VariableNode extends AccessNode {
	private String identifier;

	public VariableNode(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public String toJson() {
		return '"' + identifier + '"';
	}

	public String identifier() {
		return identifier;
	}
}
