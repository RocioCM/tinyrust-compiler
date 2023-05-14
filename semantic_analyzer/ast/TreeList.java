package semantic_analyzer.ast;

import java.util.LinkedList;

import semantic_analyzer.symbol_table.TableElement;
import util.Json;

public class TreeList<T extends TableElement> extends LinkedList<T> implements TableElement {

	/**
	 * Inserts the specified element at the beggining of this list.
	 *
	 * <p>
	 * This method is equivalent to {@link #addFirst}.
	 *
	 * @param e element to be added to this list
	 * @return {@code true} (as specified by {@link Collection#add})
	 */
	@Override
	public boolean add(T e) {
		// Las listas en el AST se arman en orden inverso, agregando primero el último
		// elemento y continuando hasta agregar el primer elemento de la lista al final,
		// por lo cual deben agregarse los nodos a esta lista en orden inverso.
		super.addFirst(e);
		return true;
	}

	@Override
	public String toJson() {
		return Json.toStringArray(this.stream()
				.map(elem -> elem.toJson()) // Generar el JSON de cada elemento
				.toList());
	}
}
