package semantic_analyzer.ast;

import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.ClassEntry;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.MethodEntry;
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

		// Generate class Virtual Method Table (VT).
		ClassEntry classTSEntry = ts.getClass(name);
		String vtLabel = "vtable_" + name;
		code.addLine(".data");
		code.addLine(vtLabel, ":"); // Declare class VTable.
		code.addLine(".word ", Code.generateLabel("method", name, "create")); // Add constructor to VT.

		// Retrieve methods from TS instead of AST, because AST doesn't include
		// inherited methods.
		classTSEntry.methods().values().forEach((method) -> {
			if (method.isInherited()) {
				// Method is inherited, so get the super-method label "recursively".
				ClassEntry superClassTSEntry = ts.getClass(classTSEntry.extendsFrom());
				MethodEntry superMethod = superClassTSEntry.methods().get(method.name());
				while (superMethod.isInherited()) {
					// Go up the classes tree until the method implementation.
					superClassTSEntry = ts.getClass(superClassTSEntry.extendsFrom());
					superMethod = superClassTSEntry.methods().get(method.name());
				}

				// Found method implementation, then generate label on that super-class.
				String methodLabel = Code.generateLabel("method", superClassTSEntry.name(), method.name());
				code.addLine(".word ", methodLabel);

			} else {
				// Method isn't inherited, so its label is resolved directly.
				String methodLabel = name.equals("main") && method.name().equals("main") ? "main"
						: Code.generateLabel("method", name, method.name());
				code.addLine(".word ", methodLabel);
			}
		});

		code.addLine(".text");

		// If the constructor is implicit, generate label and empty code block for it.
		if (classTSEntry.constructor() == null || !classTSEntry.constructor().isAlreadyDeclared()) {
			code.addLine("");
			code.addLine(Code.generateLabel("method", name, "create") + ":");
			code.addLine("j cleanup_method    # Jump to method cleanup and return to caller.");
		}

		code.add(methods.generateCode(ts)); // Generate code for each method.
		return code.getCode();
	}

	public String name() {
		return this.name;
	}
}
