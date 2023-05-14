package semantic_analyzer.ast;

import semantic_analyzer.symbol_table.TableElement;
import util.Json;

public class MethodNode implements TableElement {
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
}
