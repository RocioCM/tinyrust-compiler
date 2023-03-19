package lexic_analyzer;

/**
 * La clase Token representa cualquier token del lenguaje TinyRust+.
 * Registra el número de linea y columna de comienzo del token, identificador de
 * tipo de token y su lexema.
 */
public class Token {
	private String token;
	private String lexema;
	private int line;
	private int col;

	public Token(String token, String lexema, int line, int col) {
		this.token = token;
		this.lexema = lexema;
		this.line = line;
		this.col = col;
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

	public int getLine() {
		return this.line;
	}

	public int getCol() {
		return this.col;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setLexema(String lexema) {
		this.lexema = lexema;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public void setCol(int col) {
		this.col = col;
	}
}
