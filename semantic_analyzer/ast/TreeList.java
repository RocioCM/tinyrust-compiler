package semantic_analyzer.ast;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import error.semantic.ASTError;
import semantic_analyzer.symbol_table.SymbolTable;
import util.Json;

public class TreeList<T extends Node> extends LinkedList<T> implements Node {

	public TreeList() {
		super();
	}

	public TreeList(T... items) {
		super(Arrays.asList(items)); // Agregar a la lista los items predefinidos.
	}

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

	@Override
	public void validate(SymbolTable ts) throws ASTError {
		Iterator<T> nodesIter = this.iterator();
		while (nodesIter.hasNext()) {
			// Validar cada nodo de la lista.
			nodesIter.next().validate(ts);
		}
	}
}
