package semantic_analyzer.ast;

import semantic_analyzer.symbol_table.TableElement;

public class VariableNode implements TableElement {
	private String identifier;

	public VariableNode(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public String toJson() {
		return '"' + identifier + '"';
	}
}
