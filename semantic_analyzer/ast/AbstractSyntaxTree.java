package semantic_analyzer.ast;

import error.semantic.sentences.InternalError;
import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Code;
import util.Json;

public class AbstractSyntaxTree implements Node {
	private String name;
	private TreeList<ClassNode> classes;

	public AbstractSyntaxTree(String name) {
		this.name = name;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("clases", classes);
		return json.toString();
	}

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		if (!ts.isConsolidated()) {
			throw new InternalError(new Location(-1, -1),
					"SOLO SE PUEDE VALIDAR EL AST LUEGO DE CONSOLIDAR LA TABLA DE SIMBOLOS.");
		}
		classes.validate(ts);
	}

	@Override
	public String generateCode() {
		Code asm = new Code(classes.generateCode());
		asm.addLine("li $v0, 10 # 10 is the exit syscall.");
		asm.addLine("syscall # execute the syscall.");
		return asm.getCode();
	}

	public void addMain(BlockNode block, Location loc) throws InternalError {
		if (classes != null) {
			// Lista de métodos de la clase fantasma main.
			TreeList<MethodNode> methods = new TreeList<MethodNode>(loc);
			methods.add(new MethodNode("main", block, loc)); // Método main.
			classes.addLast(new ClassNode("main", methods, loc)); // Clase fantasma main.
		} else {
			throw new InternalError(loc,
					"SE INTENTO REGISTRAR LA FUNCION MAIN EN EL AST PERO LA LISTA DE CLASES AUN NO SE HA INICIALIZADO.");
		}
	}

	private void addPredefinedClasses() throws InternalError {
		if (classes != null) {
			classes.add(new ClassNode("Object", new TreeList<MethodNode>()));
			classes.add(new ClassNode("IO", new TreeList<MethodNode>(
					new MethodNode("out_string"),
					new MethodNode("out_i32"),
					new MethodNode("out_bool"),
					new MethodNode("out_char"),
					new MethodNode("out_array"),
					new MethodNode("in_str"),
					new MethodNode("in_i32"),
					new MethodNode("in_bool"),
					new MethodNode("in_Char"))));
			classes.add(new ClassNode("I32", new TreeList<MethodNode>()));
			classes.add(new ClassNode("Str", new TreeList<MethodNode>(
					new MethodNode("length"),
					new MethodNode("concat"),
					new MethodNode("substr"))));
			classes.add(new ClassNode("Char", new TreeList<MethodNode>()));
			classes.add(new ClassNode("Bool", new TreeList<MethodNode>()));
			classes.add(new ClassNode("Array", new TreeList<MethodNode>(new MethodNode("length"))));
		} else {
			throw new InternalError(new Location(-1, -1),
					"SE INTENTO REGISTRAR LAS CLASES PREDEFINIDAS EN EL AST PERO LA LISTA DE CLASES ES NULA.");
		}
	}

	public void setClasses(TreeList<ClassNode> classes) throws InternalError {
		this.classes = classes;
		addPredefinedClasses();
	}
}
