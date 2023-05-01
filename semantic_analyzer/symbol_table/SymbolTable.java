package semantic_analyzer.symbol_table;

import error.semantic.InternalError;
import error.semantic.SemanticalError;
import semantic_analyzer.symbol_table.types.Type;
import util.Json;

public class SymbolTable implements TableElement {
	private String name;
	private TableList<ClassEntry> classes;
	private ClassEntry currentClass = null;
	private MethodEntry currentMethod = null;

	public SymbolTable(String name) {
		this.name = name;
		classes = new TableList<ClassEntry>();
		// addPredefinedClasses();
	}

	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("clases", classes);
		return json.toString();
	}

	public void consolidate() throws SemanticalError {
		/// TODO: agregar clases predefinidas y consolidar.
	}

	public void addClass(String name) {
		ClassEntry newClass = new ClassEntry(name);
		classes.put(name, newClass);
		currentClass = newClass;
	}

	public void endClass() {
		currentClass = null;
		currentMethod = null;
	}

	public void addMethod(String name, boolean isStatic) {
		MethodEntry newMethod = currentClass.addMethod(name, isStatic);
		currentMethod = newMethod;
	}

	public void endMethod() {
		currentMethod = null;
	}

	public void addMain() {
		ClassEntry phantomClass = new ClassEntry("main");
		classes.put(name, phantomClass);
		currentClass = phantomClass;
	}

	public void addConstructor() {
		MethodEntry constructor = currentClass.addConstructor();
		currentMethod = constructor;
	}

	public void addVar(String name, Type type, boolean isPublic) throws InternalError {
		if (currentMethod == null) {
			// Si la variable se declaró dentro de un método, no se agrega a la TS.
			// Si se declaró en la raiz de una clase, se guarda como atributo de la clase.
			// Si se declaró fuera de una clase, se lanza una excepción.
			currentClass().addAttribute(name, type, isPublic);
		}
	}

	public ClassEntry currentClass() throws InternalError {
		if (currentClass == null) {
			throw new InternalError("SE INTENTO ACCEDER A LA CLASE ACTUAL Y NO EXISTE.");
		}
		return currentClass;
	}

	public MethodEntry currentMethod() throws InternalError {
		if (currentMethod == null) {
			throw new InternalError("SE INTENTO ACCEDER AL METODO ACTUAL Y NO EXISTE.");
		}
		return currentMethod;
	}
}
