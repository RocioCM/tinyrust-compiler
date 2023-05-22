package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Json;

public class ClassNode implements Node {
	private String name;
	private TreeList<MethodNode> methods;
	protected Location loc; // Declaration location.

	public ClassNode(String name, TreeList<MethodNode> methods, Location loc) {
		this.name = name;
		this.methods = methods;
		this.loc = loc;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("metodos", methods);
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		methods.validate(ts); // Validar cada método de la clase.
	}
}
