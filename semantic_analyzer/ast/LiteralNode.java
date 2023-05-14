package semantic_analyzer.ast;

import semantic_analyzer.types.Char;
import semantic_analyzer.types.I32;
import semantic_analyzer.types.Str;
import semantic_analyzer.types.Bool;
import semantic_analyzer.types.Void;
import semantic_analyzer.types.Type;

public class LiteralNode extends OperandNode {
	private String value;
	private Type type;

	public LiteralNode(String value, String type) {
		this.value = value;
		switch (type) {
			case "Bool":
				this.type = new Bool();
				break;
			case "Str":
				this.type = new Str();
				break;
			case "I32":
				this.type = new I32();
				break;
			case "Char":
				this.type = new Char();
				break;
			case "nil":
				this.type = new Void();
				break;
			default:
				this.type = new Void();
				break;
		}
	}

	@Override
	public Type resolveType() {
		return type;
	}

	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'toJson'");
	}

}
