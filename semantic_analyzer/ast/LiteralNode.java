package semantic_analyzer.ast;

import semantic_analyzer.types.Char;
import semantic_analyzer.types.I32;
import semantic_analyzer.types.PrimitiveType;
import semantic_analyzer.types.Str;
import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.types.Bool;
import semantic_analyzer.types.Void;
import util.Code;
import util.Json;

public class LiteralNode extends ExpressionNode {
	static private int stringLiteralCounter = 0;
	private int stringId;
	private PrimitiveType<?> literal;

	public LiteralNode(String value, String type, Location loc) {
		super(loc);
		switch (type) {
			case "p_true":
				this.literal = new Bool(true);
				break;
			case "p_false":
				this.literal = new Bool(false);
				break;
			case "lit_string":
				this.literal = new Str(value.substring(1, value.length() - 1)); // Eliminar comillas dobles del literal.
				this.stringId = LiteralNode.stringLiteralCounter + 1;
				LiteralNode.stringLiteralCounter++;
				break;
			case "lit_int":
				this.literal = new I32(Integer.valueOf(value)); // Convierte el valor de String a int.
				break;
			case "lit_char":
				this.literal = new Char(value.charAt(1)); // Elimina comillas simples del literal.
				break;
			case "p_nil":
				this.literal = new Void();
				break;
			default:
				this.literal = new Void();
				break;
		}
		super.setResolveType(this.literal);
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		super.validateType(ts); // Validar que esta expresión es del tipo esperado para su contexto.
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("tipo", "literal");
		json.addAttr("valor", String.valueOf(literal.value()));
		json.addAttr("tipo-dato", super.resolveType());
		return json.toString();
	}

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		Code code = new Code();
		if (literal instanceof Str) {
			String label = "literal-str-" + stringId;
			code.addLine(".data # Save string into the data segment...");
			code.addLine(label, ".asciiz \"", String.valueOf(literal.value()), "\"");
		} else {
			// TODO: generate code for other literals.
		}

		return code.getCode();
	}

}
