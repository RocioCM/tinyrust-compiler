package semantic_analyzer.symbol_table;

/**
 * La clase Location representa una posición en un archivo fuente del lenguaje
 * TinyRust+. Registra un número de linea y columna asociados a algún token.
 */
public class Location {
	private int line;
	private int col;

	public Location(int line, int col) {
		this.line = line;
		this.col = col;
	}

	public int getLine() {
		return this.line;
	}

	public int getCol() {
		return this.col;
	}
}
