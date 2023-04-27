package semantic_analyzer.symbol_table;

import semantic_analyzer.symbol_table.types.Type;

public class ClassEntry implements TableElement {
	private String name;
	private int position;
	private Type extendsFrom;
	private boolean extendable = true;
	private ConstructorEntry constructor;
	private TableList<MethodEntry> methods;
	private TableList<AttributeEntry> attributes;
	private TableList<AttributeEntry> constants;

	public ClassEntry(String name, int position) {
		this.name = name;
		this.position = position;
	}

	public ClassEntry(String name, int position, boolean extendable) {
		this.name = name;
		this.position = position;
		this.extendable = extendable;
	}

	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'toJson'");
	}

	public void addAttribute(String name, Type type) {
		AttributeEntry attr = new AttributeEntry(name, type, attributes.size() + 1, false);
		attributes.put(name, attr);
	}

	public void addConstant(String name, Type type) {
		AttributeEntry attr = new AttributeEntry(name, type, attributes.size() + 1, true);
		attributes.put(name, attr);
	}

	public void addMethod(String name, MethodEntry method) {
		method.setPosition(methods.size() + 1);
		methods.put(name, method);
	}

	public TableList<MethodEntry> methods() {
		return this.methods;
	}

	public void setExtendsFrom(Type extendsFrom) {
		this.extendsFrom = extendsFrom;
	}

	public void setConstructor(ConstructorEntry constructor) {
		this.constructor = constructor;
	}
}
