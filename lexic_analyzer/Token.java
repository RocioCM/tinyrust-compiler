package lexic_analyzer;

import semantic_analyzer.symbol_table.Location;

/**
 * La clase Token representa cualquier token del lenguaje TinyRust+.
 * Registra el número de linea y columna de comienzo del token, identificador de
 * tipo de token y su lexema.
 */
public class Token extends Location {
	private String token;
	private String lexema;

	public Token(String token, String lexema, int line, int col) {
		super(line, col);
		this.token = token;
		this.lexema = lexema;
	}

	/**
	 * Agrega un caracter nuevo al final del lexema previo.
	 * 
	 * @param lastChar - caracter para agregar al lexema.
	 */
	public void appendLexema(char lastChar) {
		this.lexema += lastChar;
	}

	public String getToken() {
		return this.token;
	}

	public String getLexema() {
		return this.lexema;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setLexema(String lexema) {
		this.lexema = lexema;
	}
}
