package semantic_analyzer.types;

public class Array extends Type {
	PrimitiveType<?> itemsType;

	public Array() {
		super("Array");
	}

	public Array(PrimitiveType<?> type) {
		super("Array");
		this.itemsType = type;
	}

	@Override
	public String toJson() {
		String json;
		if (itemsType != null) {
			// Mostrar también el tipo de dato de los elementos del arreglo.
			json = '"' + super.type() + '-' + itemsType.type() + '"';
		} else {
			json = '"' + super.type() + '"';
		}
		return json;
	}
}
