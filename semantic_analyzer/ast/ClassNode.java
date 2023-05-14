package semantic_analyzer.ast;

import semantic_analyzer.symbol_table.TableElement;
import util.Json;

public class ClassNode implements TableElement {
	private String name;
	private TreeList<MethodNode> methods;

	public ClassNode(String name, TreeList<MethodNode> methods) {
		this.name = name;
		this.methods = methods;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("metodos", methods);
		return json.toString();
	}
}
