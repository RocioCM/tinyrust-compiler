package semantic_analyzer.symbol_table;

import semantic_analyzer.symbol_table.types.Type;

public class MethodEntry implements TableElement {
	private String name;
	private int position;
	private Type returnType;
	private boolean isPublic = false;
	private TableList<ArgumentEntry> arguments;

	public MethodEntry(String name, Type returnType) {
		this.name = name;
		this.returnType = returnType;
	}

	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'toJson'");
	}

	public void addArgument(String name, Type type) {
		ArgumentEntry arg = new ArgumentEntry(name, type, arguments.size() + 1);
		arguments.put(name, arg);
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}
}
