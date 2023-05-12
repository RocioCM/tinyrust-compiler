package semantic_analyzer.ast;

import java.util.LinkedList;

import semantic_analyzer.symbol_table.TableElement;
import util.Json;

public class TreeList<T extends TableElement> extends LinkedList<T> implements TableElement {

	@Override
	public String toJson() {
		return Json.toStringArray(this.stream()
				.map(elem -> elem.toJson()) // Generar el JSON de cada elemento
				.toList());
	}
}
