package semantic_analyzer.ast;

import semantic_analyzer.types.Char;
import semantic_analyzer.types.I32;
import semantic_analyzer.types.Str;
import error.semantic.ASTError;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Bool;
import semantic_analyzer.types.Void;
import semantic_analyzer.types.Type;
import util.Json;

public class LiteralNode extends ExpressionNode {
	private String value;

	public LiteralNode(String value, String type) {
		this.value = value;
		Type resolveType;
		switch (type) {
			case "p_true":
			case "p_false":
				resolveType = new Bool();
				break;
			case "lit_string":
				resolveType = new Str();
				break;
			case "lit_int":
				resolveType = new I32();
				break;
			case "lit_char":
				resolveType = new Char();
				break;
			case "p_nil":
				resolveType = new Void();
				break;
			default:
				resolveType = new Void();
				break;
		}
		super.setResolveType(resolveType);
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		super.validate(ts); // Validar que esta expresión es del tipo esperado para su contexto.
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "literal");
		json.addAttr("valor", value);
		json.addAttr("tipo-dato", super.resolveType());
		return json.toString();
	}

}
