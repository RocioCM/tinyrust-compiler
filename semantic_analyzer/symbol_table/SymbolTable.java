package semantic_analyzer.symbol_table;

import java.util.HashMap;
import java.util.Iterator;

import error.semantic.ConsolidationError;
import error.semantic.DuplicatedEntityIdError;
import error.semantic.InternalError;
import error.semantic.MultipleConstructorsError;
import error.semantic.SemanticalError;
import semantic_analyzer.symbol_table.predefined_classes.Array;
import semantic_analyzer.symbol_table.predefined_classes.Bool;
import semantic_analyzer.symbol_table.predefined_classes.Char;
import semantic_analyzer.symbol_table.predefined_classes.I32;
import semantic_analyzer.symbol_table.predefined_classes.IO;
import semantic_analyzer.symbol_table.predefined_classes.Object;
import semantic_analyzer.symbol_table.predefined_classes.Str;
import semantic_analyzer.types.Type;
import util.Json;

public class SymbolTable implements TableElement {
	private String name;
	private TableList<ClassEntry> classes;
	private ClassEntry currentClass = null;
	private MethodEntry currentMethod = null;

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
					throw new ConsolidationError(subClass.locationExtendsFrom().getLine(),
							subClass.locationExtendsFrom().getCol(),
							"LA CLASE " + subClassName + " HEREDA DE LA CLASE INEXISTENTE " + superClassName);
				}

				// Validar que no haya dependencia circular:
				ClassEntry ancestorFromTree = ancestorsTree.get(superClassName);
				if (ancestorFromTree != null && !ancestorFromTree.isConsolidated()) {
					throw new ConsolidationError(subClass.locationExtendsFrom().getLine(),
							subClass.locationExtendsFrom().getCol(),
							"HERENCIA CIRCULAR: LA CLASE " + subClassName + " HEREDA DE LA CLASE "
									+ superClassName + " QUE HEREDA DIRECTA O INDIRECTAMENTE DE " + subClassName);
				}

				// Agregar la relacion de herencia al árbol de ancestros:
				ancestorsTree.put(subClassName, superClass);
				subClass = superClass;
			}
			// 2. Consolidar atributos y métodos de cada ancestro en el árbol.
			entry.consolidate(ancestorsTree);
		}
	}

	public void addClass(String name) throws DuplicatedEntityIdError {
		if (classes.containsKey(name)) {
			throw new DuplicatedEntityIdError(" LA CLASE", name);
		}

		ClassEntry newClass = new ClassEntry(name);
		classes.put(name, newClass);
		currentClass = newClass;
	}

	public void endClass() {
		currentClass = null;
		currentMethod = null;
	}

	public void addMethod(String name, boolean isStatic, int line, int col) throws SemanticalError {
		MethodEntry newMethod = currentClass.addMethod(name, isStatic, line, col);
		currentMethod = newMethod;
	}

	public void endMethod() {
		currentMethod = null;
	}

	public void addMain() {
		ClassEntry phantomClass = new ClassEntry("main", false, null);
		classes.put(name, phantomClass);
	}

	public void addConstructor() throws MultipleConstructorsError {
		MethodEntry constructor = currentClass.addConstructor();
		currentMethod = constructor;
	}

	public void addVar(String name, Type type, boolean isPublic, int line, int col) throws SemanticalError {
		if (currentMethod == null && currentClass != null) {
			// Si la variable se declaró dentro de un método, no se agrega a la TS.
			// Si se declaró en la raiz de una clase, se guarda como atributo de la clase.
			// Si se declaró fuera de una clase, se lanza una excepción.
			currentClass.addAttribute(name, type, isPublic, line, col);
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

	private void addPredefinedClasses() throws InternalError {
		classes.put("Object", new Object());
		classes.put("IO", new IO());
		classes.put("I32", new I32());
		classes.put("Str", new Str());
		classes.put("Char", new Char());
		classes.put("Bool", new Bool());
		classes.put("Array", new Array());
	}
}
