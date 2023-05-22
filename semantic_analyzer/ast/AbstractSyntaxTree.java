package semantic_analyzer.ast;

import error.semantic.sentences.InternalError;
import error.semantic.sentences.ASTError;
import semantic_analyzer.symbol_table.Location;
import semantic_analyzer.symbol_table.SymbolTable;
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
		classes.validate(ts);
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
			// TODO: agregar los metodos predefinidos de cada clase.
			classes.add(new ClassNode("Object", new TreeList<MethodNode>(), new Location(-1, -1)));
			classes.add(new ClassNode("IO", new TreeList<MethodNode>(), new Location(-1, -1)));
			classes.add(new ClassNode("I32", new TreeList<MethodNode>(), new Location(-1, -1)));
			classes.add(new ClassNode("Str", new TreeList<MethodNode>(), new Location(-1, -1)));
			classes.add(new ClassNode("Char", new TreeList<MethodNode>(), new Location(-1, -1)));
			classes.add(new ClassNode("Bool", new TreeList<MethodNode>(), new Location(-1, -1)));
			classes.add(new ClassNode("Array", new TreeList<MethodNode>(), new Location(-1, -1)));
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
