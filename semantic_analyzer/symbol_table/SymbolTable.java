package semantic_analyzer.symbol_table;

import java.util.HashMap;
import java.util.Iterator;

import error.semantic.SemanticalError;
import error.semantic.declarations.ConsolidationError;
import error.semantic.declarations.DuplicatedEntityIdError;
import error.semantic.sentences.InternalError;
import error.semantic.declarations.MultipleConstructorsError;
import semantic_analyzer.symbol_table.predefined_classes.Array;
import semantic_analyzer.symbol_table.predefined_classes.Bool;
import semantic_analyzer.symbol_table.predefined_classes.Char;
import semantic_analyzer.symbol_table.predefined_classes.I32;
import semantic_analyzer.symbol_table.predefined_classes.IO;
import semantic_analyzer.symbol_table.predefined_classes.Object;
import semantic_analyzer.symbol_table.predefined_classes.Str;
import semantic_analyzer.types.Type;
import semantic_analyzer.types.Void;
import util.Json;

public class SymbolTable implements TableElement {
	private String name;
	private TableList<ClassEntry> classes;
	private ClassEntry currentClass = null;
	private MethodEntry currentMethod = null;
	private boolean isConsolidated = false;

	public SymbolTable(String name) throws InternalError {
		this.name = name;
		classes = new TableList<ClassEntry>();
		addPredefinedClasses();
	}

	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("clases", classes);
		return json.toString();
	}

	public void consolidate() throws SemanticalError {
		// Key: subclass name, Value: superclass entry.
		HashMap<String, ClassEntry> ancestorsTree = new HashMap<String, ClassEntry>();
		Iterator<ClassEntry> classesIter = classes.values().iterator();

		while (classesIter.hasNext()) {
			ClassEntry entry = classesIter.next();

			// 1. Validar que no exista herencia circular.
			ClassEntry subClass = entry;
			ancestorsTree.put(entry.name(), null);
			while (subClass.extendsFrom() != null && !subClass.isConsolidated()) {
				String superClassName = subClass.extendsFrom();
				String subClassName = subClass.name();

				// Validar que la superclase exista:
				ClassEntry superClass = classes.get(superClassName);
				if (superClass == null) {
					throw new ConsolidationError(subClass.locationExtendsFrom(),
							"LA CLASE " + subClassName + " HEREDA DE LA CLASE INEXISTENTE " + superClassName);
				}

				// Validar que no haya dependencia circular:
				ClassEntry ancestorFromTree = ancestorsTree.get(superClassName);
				if (ancestorFromTree != null && !ancestorFromTree.isConsolidated()) {
					throw new ConsolidationError(subClass.locationExtendsFrom(),
							"HERENCIA CIRCULAR: LA CLASE " + subClassName + " HEREDA DE LA CLASE "
									+ superClassName + " QUE HEREDA DIRECTA O INDIRECTAMENTE DE " + subClassName);
				}

				// Agregar la relacion de herencia al árbol de ancestros:
				ancestorsTree.put(subClassName, superClass);
				subClass = superClass;
			}
			// 2. Consolidar atributos y métodos de cada ancestro en el árbol.
			entry.consolidate(classes, ancestorsTree);
			isConsolidated = true;
		}
	}

	public void addClass(String name, Location loc) throws DuplicatedEntityIdError {
		if (classes.containsKey(name)) {
			throw new DuplicatedEntityIdError(" LA CLASE", name, loc);
		}

		ClassEntry newClass = new ClassEntry(name, classes.size() + 1, loc);
		classes.put(name, newClass);
		currentClass = newClass;
	}

	public void endClass() {
		currentClass = null;
		currentMethod = null;
	}

	public void startClass(String name) throws InternalError {
		ClassEntry classEntry = classes.get(name);
		if (classEntry == null) {
			throw new InternalError("SE INTENTO ACCEDER A LA CLASE INEXISTENTE " + name);
		}
		currentClass = classEntry;
	}

	public void addMethod(String name, boolean isStatic, Location loc) throws SemanticalError {
		MethodEntry newMethod = currentClass.addMethod(name, isStatic, loc);
		currentMethod = newMethod;
	}

	public void startMethod(String name) throws InternalError {
		MethodEntry method;
		if (name.equals("create")) {
			method = currentClass().constructor();
		} else {
			method = currentClass().methods().get(name);
		}

		if (method == null) {
			throw new InternalError(
					"SE INTENTO ACCEDER AL METODO " + name + " DE LA CLASE ACTUAL, PERO LA CLASE NO POSEE TAL METODO.");
		}
		currentMethod = method;
	}

	public void endMethod() {
		currentMethod = null;
	}

	public void addMain(Location loc) throws SemanticalError {
		ClassEntry phantomClass = new ClassEntry("main", false, null, classes.size() + 1, loc);
		classes.put("main", phantomClass);
		currentClass = phantomClass;
		addMethod("main", true, loc);
	}

	public void addConstructor(Location loc) throws MultipleConstructorsError {
		MethodEntry constructor = currentClass.addConstructor(loc);
		currentMethod = constructor;
	}

	public void addVar(String name, Type type, boolean isPublic, Location loc) throws SemanticalError {
		if (currentMethod == null && currentClass != null) {
			// Si la variable se declaró en la raiz de una clase,
			// se guarda como atributo de la clase.
			currentClass.addAttribute(name, type, isPublic, loc);
		} else {
			if (currentMethod != null && currentClass != null) {
				// Si la variable se declaró dentro de un método, se agrega a la TS para
				// posterior uso por el AST durante la consolidación.
				currentMethod.addVar(name, type, loc);
			} else {
				throw new InternalError("SE INTENTO AGREGAR UNA VARIABLE Y NO HAY CLASE NI METODO ACTUAL.");
			}
			// Si la variable se declaró fuera de una clase, se lanza una excepción.

		}
	}

	/**
	 * Dados dos tipos de datos o clases, valida si la primera es una subclase de la
	 * segunda.
	 * 
	 * @return true si la primer clase es igual o es una subclase de la segunda,
	 *         false en el caso contrario.
	 */
	public boolean isSubclass(Type subclass, Type superclass) {
		Boolean isSub = false;

		if (!subclass.equals(new Void())) { // Void no está en la TS.
			// Se sube por el árbol de clases desde la subclase hasta hallar la superclase o
			// hasta llegar a la clase base Object.
			String className = subclass.type();
			while (className != null && !isSub) {
				if (className.equals(superclass.type())) {
					// Se encontró la superclase en el árbol.
					isSub = true;
				} else {
					// Subir en el arbol de clases.
					className = classes.get(className).extendsFrom();
				}
			}
		}

		return isSub;
	}

	/**
	 * Dado el identificador de una variable, busca la entidad dentro del método
	 * actual o como atributo de la clase actual.
	 * 
	 * @param name - Identificador de la variable buscada.
	 * @return Retorna la entrada de clase correspondiente al Tipo de la variable
	 *         hallada. Si no se encuentra ninguna entidad con tal identificador,
	 *         devuelve null.
	 * @see - Validación de AST
	 */
	public VariableEntry getVariable(String name) throws InternalError {
		// 1. Buscar la variable dentro del método o como argumento.
		VariableEntry var = currentMethod().getVariable(name);

		// 2. Buscar la variable como atributo de la clase.
		if (var == null) {
			var = currentClass().attributes().get(name);
		}

		// Si el nombre no se corresponde con ninguna variable, se retorna null y el
		// invocador debe manejar el caso.
		return var;
	}

	/**
	 * @param name - Nombre de la clase.
	 * @return Retorna la entrada de la clase con el nombre dado.
	 *         En caso de no existir tal clase retorna null.
	 */
	public ClassEntry getClass(String name) {
		return classes.get(name);
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

	private void addPredefinedClasses() throws InternalError {
		classes.put("Object", new Object(1));
		classes.put("IO", new IO(2));
		classes.put("I32", new I32(3));
		classes.put("Str", new Str(4));
		classes.put("Char", new Char(5));
		classes.put("Bool", new Bool(6));
		classes.put("Array", new Array(7));
	}

	public boolean isConsolidated() {
		return isConsolidated;
	}
}
