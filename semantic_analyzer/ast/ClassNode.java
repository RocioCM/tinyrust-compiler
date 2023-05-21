package semantic_analyzer.ast;

import error.semantic.ASTError;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Json;

public class ClassNode implements Node {
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

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		ts.startClass(name); // Se actualiza el estado de la TS para poder acceder a los métodos y atributos
													// de la clase.
		methods.validate(ts); // Validar cada método de la clase.
		ts.endClass();
	}
}
