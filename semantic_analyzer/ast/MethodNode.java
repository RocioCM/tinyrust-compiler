package semantic_analyzer.ast;

import error.semantic.ASTError;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Json;

public class MethodNode implements Node {
	private String name;
	private BlockNode block; // Es un bloque simple ya que las decl. de variables se guardan en la TS.

	public MethodNode(String name, BlockNode block) {
		this.name = name;
		this.block = block;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("Bloque", block);
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		block.validate(ts);
	}
}
