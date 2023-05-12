package semantic_analyzer.ast;

import error.semantic.InternalError;
import semantic_analyzer.symbol_table.SymbolTable;
import semantic_analyzer.symbol_table.TableElement;
import util.Json;

public class AbstractSyntaxTree implements TableElement {
	private String name;
	private TreeList<ClassNode> classes;
	private ClassNode currentClass = null;
	private MethodNode currentMethod = null;

	public AbstractSyntaxTree(String name) {
		this.name = name;
		this.classes = new TreeList<ClassNode>();
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

	public void addClass(String name) {
		ClassNode newClass = new ClassNode(name);
		classes.add(newClass);
		currentClass = newClass;
	}

	public void endClass() {
		currentClass = null;
		currentMethod = null;
	}

	public void addMain() {
		addClass("main");
		addMethod("main");
	}

	public void addMethod(String name) {
		MethodNode newMethod = currentClass.addMethod(name);
		currentMethod = newMethod;
	}

	public void endMethod() {
		currentMethod = null;
	}

	public void addSentence(SentenceNode sent) throws InternalError {
		if (currentClass != null && currentMethod != null) {
			currentMethod.addSentence(sent);
		} else {
			throw new InternalError("SE INTENTO AGREGAR UNA SENTENCIA Y NO HAY UNA CLASE O METODO ACTUAL.");
		}
	}
}
