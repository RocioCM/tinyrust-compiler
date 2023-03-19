package lexic_analyzer;

/**
 * Funcionalidad para reconocer tokens de tipo palabra reservada.
 */
public class ReservedWords {
	/**
	 * Recibe un token de tipo identificador y actualiza su tipo de forma acorde si
	 * se trata de una palabra reservada de TinyRust+.
	 * 
	 * @param token - token de tipo identificador.
	 * @return El mismo token modificado.
	 */
	public static Token matchReservedWord(Token token) {
		// Solo puede ser palabra reservada si es un identificador.
		// Verificamos por las dudas.
		if (token.getToken() == "id") {
			switch (token.getLexema()) {
				case "fn":
					token.setToken("p_func");
					break;
				case "class":
					token.setToken("p_class");
					break;
				case "false":
					token.setToken("p_false");
					break;
				case "true":
					token.setToken("p_true");
					break;
				case "if":
					token.setToken("p_if");
					break;
				case "else":
					token.setToken("p_else");
					break;
				case "while":
					token.setToken("p_while");
					break;
				case "return":
					token.setToken("p_return");
					break;
				case "new":
					token.setToken("p_new");
					break;
				case "nil":
					token.setToken("p_nil");
					break;
				case "static":
					token.setToken("p_static");
					break;
				case "pub":
					token.setToken("p_pub");
					break;
				case "private":
					token.setToken("p_private");
					break;
				case "void":
					token.setToken("p_void");
					break;
				default:
					break;
			}
		}
		return token;
	}
}
