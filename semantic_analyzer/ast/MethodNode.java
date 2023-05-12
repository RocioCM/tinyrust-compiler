package semantic_analyzer.ast;

import semantic_analyzer.symbol_table.TableElement;
import util.Json;

public class MethodNode implements TableElement {
	private String name;
	private MethodBlockNode block;

	public MethodNode(String name) {
		this.name = name;
		this.block = new MethodBlockNode();
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("Bloque", block);
		return json.toString();
	}

	public void addSentence(SentenceNode sent) {
		block.addSentence(sent);
	}
}
