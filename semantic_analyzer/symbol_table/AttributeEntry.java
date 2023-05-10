package semantic_analyzer.symbol_table;

import semantic_analyzer.types.Type;
import util.Json;

public class AttributeEntry extends VariableEntry {
	private boolean isPublic = false;
	private Location locationDecl;

	public AttributeEntry(String name, Type type, int position, boolean isPublic, int line, int col) {
		// Inicialización por defecto.
		super(name, type, position);
		this.isPublic = isPublic;
		locationDecl = new Location(line, col);
	}

	public AttributeEntry(AttributeEntry attr, int position) {
		// Clonar instancia.
		super(attr.name, attr.type, position);
		this.isPublic = attr.isPublic;
	}

	@Override
	public String toJson() {
		Json json = new Json();
		json.addAttr("nombre", name);
		json.addAttr("posicion", position);
		json.addAttr("public", isPublic);
		json.addAttr("tipo", type);
		return json.toString();
	}

	public Location locationDecl() {
		return locationDecl;
	}
}
