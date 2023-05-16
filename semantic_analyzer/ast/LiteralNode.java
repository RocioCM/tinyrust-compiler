package semantic_analyzer.ast;

import semantic_analyzer.types.Char;
import semantic_analyzer.types.I32;
import semantic_analyzer.types.Str;
import semantic_analyzer.types.Bool;
import semantic_analyzer.types.Void;
import semantic_analyzer.types.Type;
import util.Json;

public class LiteralNode extends ExpressionNode {
	private String value;
	private Type type;

	public LiteralNode(String value, String type) {
		this.value = value;
		switch (type) {
			case "p_true":
			case "p_false":
				this.type = new Bool();
				break;
			case "lit_string":
				this.type = new Str();
				break;
			case "lit_int":
				this.type = new I32();
				break;
			case "lit_char":
				this.type = new Char();
				break;
			case "p_nil":
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
		Json json = new Json();
		json.addAttr("tipo", "literal");
		json.addAttr("valor", value);
		json.addAttr("tipo-dato", type);
		return json.toString();
	}

}
