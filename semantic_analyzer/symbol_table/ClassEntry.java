package semantic_analyzer.symbol_table;

import semantic_analyzer.symbol_table.types.Type;
import util.Json;

public class ClassEntry implements TableElement {
	private String name;
	private int position;
	private String extendsFrom = "Object";
	private boolean extendable = true;
	private ConstructorEntry constructor;
	private TableList<MethodEntry> methods;
	private TableList<AttributeEntry> attributes;

	public ClassEntry(String name, int position) {
		this.name = name;
		this.position = position;
		this.constructor = new ConstructorEntry();
		this.methods = new TableList<MethodEntry>();
		this.attributes = new TableList<AttributeEntry>();
	}

	public ClassEntry(String name, int position, boolean extendable) {
		this(name, position);
		this.extendable = extendable;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("posicion", position);
		json.addAttr("heredaDe", extendsFrom);
		json.addAttr("heredable", extendable);
		json.addAttr("atributos", attributes);
		json.addAttr("constructor", constructor);
		json.addAttr("metodos", methods);
		return json.toString();
	}

	public AttributeEntry addAttribute(String name, Type type, boolean isPublic) {
		AttributeEntry attr = new AttributeEntry(name, type, attributes.size() + 1, isPublic);
		attributes.put(name, attr);
		return attr;
	}

	public MethodEntry addMethod(String name, boolean isStatic) {
		MethodEntry method = new MethodEntry(name, isStatic, methods.size() + 1);
		methods.put(name, method);
		return method;
	}

	public ConstructorEntry addConstructor() {
		ConstructorEntry constructor = new ConstructorEntry();
		this.constructor = constructor;
		return constructor;
	}

	public TableList<MethodEntry> methods() {
		return this.methods;
	}

	public void setExtendsFrom(String extendsFrom) {
		this.extendsFrom = extendsFrom;
	}
}
