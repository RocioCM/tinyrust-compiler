package semantic_analyzer.symbol_table;

import semantic_analyzer.types.Type;
import util.Json;

public class AttributeEntry extends VariableEntry {
	private boolean isPublic = false;
	private boolean isInherited = false;

	public AttributeEntry(String name, Type type, int position, boolean isPublic, Location loc) {
		// Inicialización por defecto.
		super(name, type, position, loc);
		this.isPublic = isPublic;
		this.isInherited = false;
	}

	public AttributeEntry(AttributeEntry attr, int position) {
		// Clonar instancia.
		super(attr.name, attr.type, position, attr.locationDecl);
		this.isPublic = attr.isPublic;
		this.isInherited = true;
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

	public boolean isPublic() {
		return isPublic;
	}

	public boolean isInherited() {
		return isInherited;
	}
}
