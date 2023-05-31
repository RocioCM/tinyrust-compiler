package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import error.semantic.sentences.InternalError;
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
		try {
			// Se actualiza el estado de la TS para poder acceder a los métodos y atributos
			// de la clase.
			ts.startClass(name);
			methods.validate(ts); // Validar cada método de la clase.
			ts.endClass();

		} catch (error.semantic.declarations.InternalError e) {
			throw new InternalError(loc, e.getMessage());
		}
	}

	@Override
	public String generateCode(SymbolTable ts) throws ASTError {
		Code code = new Code();
		// TODO 1: agregar clase al CIR?

		methods.generateCode(ts); // Registrar el código de cada método.
		return code.getCode();
	}
}
