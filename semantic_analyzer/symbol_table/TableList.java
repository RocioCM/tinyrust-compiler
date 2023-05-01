package semantic_analyzer.symbol_table;

import java.util.HashMap;

import util.Json;

public class TableList<T extends TableElement> extends HashMap<String, T> implements TableElement {
	@Override
	public String toJson() {
		return Json.toStringArray(
				this.values().stream()
						.map(elem -> elem.toJson()) // Generar el JSON de cada elemento
						.toList());
	}

}
