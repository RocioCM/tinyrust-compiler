package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Code;
import util.Json;

public class ClassNode implements Node {
	private String name;
	private TreeList<MethodNode> methods;
	protected Location loc; // Declaration location.

	public ClassNode(String name, TreeList<MethodNode> methods) {
		// Constructor usado para las clases predefinidas.
		this.name = name;
		this.methods = methods;
		this.loc = new Location(-1, -1);
	}

	public ClassNode(String name, TreeList<MethodNode> methods, Location loc) {
		// Constructor usado para la declaración de clases del usuario.
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
		// Se actualiza el estado de la TS para poder acceder a los métodos y atributos
		// de la clase.
		ts.startClass(name);
		methods.validate(ts); // Validar cada método de la clase.
		ts.endClass();

	}

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		Code code = new Code();
		// TODO 1: agregar la VT de la clase?
		// Generarla con los labels de los métodos de la TS, porque en los métodos del
		// AST faltan los métodos heredados.

		code.add(methods.generateCode(ts)); // Registrar el código de cada método.
		return code.getCode();
	}
}
