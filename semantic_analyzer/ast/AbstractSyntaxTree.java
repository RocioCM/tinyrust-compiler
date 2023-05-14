package semantic_analyzer.ast;

import error.semantic.InternalError;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.symbol_table.TableElement;
import util.Json;

public class AbstractSyntaxTree implements TableElement {
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

	public void consolidate(SymbolTable ts) {
	}

	public void addMain(BlockNode block) throws InternalError {
		if (classes != null) {
			TreeList<MethodNode> methods = new TreeList<MethodNode>(); // Lista de métodos de la clase fantasma main.
			methods.add(new MethodNode("main", block)); // Método main.
			classes.addLast(new ClassNode("main", methods)); // Clase fantasma main.
		} else {
			throw new InternalError(
					"SE INTENTO REGISTRAR LA FUNCION MAIN EN EL AST PERO LA LISTA DE CLASES AUN NO SE HA INICIALIZADO.");
		}
	}

	private void addPredefinedClasses() throws InternalError {
		if (classes != null) {
			// TODO: agregar los metodos predefinidos de cada clase.
			classes.add(new ClassNode("Object", new TreeList<MethodNode>()));
			classes.add(new ClassNode("IO", new TreeList<MethodNode>()));
			classes.add(new ClassNode("I32", new TreeList<MethodNode>()));
			classes.add(new ClassNode("Str", new TreeList<MethodNode>()));
			classes.add(new ClassNode("Char", new TreeList<MethodNode>()));
			classes.add(new ClassNode("Bool", new TreeList<MethodNode>()));
			classes.add(new ClassNode("Array", new TreeList<MethodNode>()));
		} else {
			throw new InternalError(
					"SE INTENTO REGISTRAR LAS CLASES PREDEFINIDAS EN EL AST PERO LA LISTA DE CLASES ES NULA.");
		}
	}

	public void setClasses(TreeList<ClassNode> classes) throws InternalError {
		this.classes = classes;
		addPredefinedClasses();
	}
}
