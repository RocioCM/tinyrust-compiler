package semantic_analyzer.symbol_table;

import semantic_analyzer.symbol_table.types.Type;

public class SymbolTable implements TableElement {
	private TableList<ClassEntry> classes;
	private ClassEntry currentClass;
	private TableElement currentMethod;
	private String name;

	public SymbolTable(String name) {
		this.name = name;
		classes = new TableList<ClassEntry>();
	}

	public String toJson() {
		String json = "";
		json += "\"clases\": \n";
		json += classes.toJson();
		json += ", \"main\": \n";
		ClassEntry main = classes.get("main");
		if (main != null) {
			json += main.toJson();
		} else {
			json += "{}";
		}
		return json;
	}

	public void addClass(String name) {
		ClassEntry newClass = new ClassEntry(name, classes.size() + 1);
		currentClass = newClass;
	}

	public void addMain() {
		ClassEntry phantomClass = new ClassEntry("main", 0);
		currentClass = phantomClass;
	}

	public void addConstructor(String name, Type returnType) {
		ConstructorEntry constructor = new ConstructorEntry();
		currentClass.setConstructor(constructor);
		currentMethod = constructor;
	}

	public void addMethod(String name, Type returnType) {
		MethodEntry newMethod = new MethodEntry(name, returnType);
		currentClass.addMethod(name, newMethod);
		currentMethod = newMethod;
	}

	public ClassEntry currentClass() {
		return currentClass;
	}

	public TableElement currentMethod() {
		return currentMethod;
	}
}
