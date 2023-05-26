package semantic_analyzer.types;

import semantic_analyzer.symbol_table.TableElement;

public abstract class Type implements TableElement {
	private String type;

	protected Type(String type) {
		this.type = type;
	}

	@Override
	public String toJson() {
		return '"' + type + '"';
	}

	public String type() {
		return type;
	}

	/**
	 * Compares this Type to the specified object. The result is true if and only
	 * if the argument type key String of the object represents the same
	 * sequence of characters as the type key this object.
	 *
	 * @param t - The object to compare this Type against
	 * @return true if the given object represents a Type equivalent to this type,
	 *         false otherwise
	 */
	public boolean equals(Type t) {
		if (t == null) {
			return false;
		}
		return this.type.equals(t.type());
	}
}
