package semantic_analyzer.types;

public class Array extends Type {
	private PrimitiveType<?> itemsType;

	public Array() {
		super("Array");
		this.itemsType = null;
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

	/**
	 * Compares this Array to the specified object. The result is true if and only
	 * if the object is an Array and the items type of the object represents the
	 * same Type as the items type this Array.
	 *
	 * @param t - The object to compare this Array against
	 * @return true if the given object represents an Array equivalent to this
	 *         array, false otherwise
	 */
	@Override
	public boolean equals(Type t) {
		Boolean equal = false;
		if (t instanceof Array) {
			Array a = (Array) (t);
			if (this.itemsType == null) {
				// Si no se especifica tipo para este arreglo, la igualdad es válida.
				equal = true;
			} else {
				equal = this.itemsType.equals(a.itemsType());
			}
		}
		return equal;
	}

	public PrimitiveType<?> itemsType() {
		return itemsType;
	}

}
