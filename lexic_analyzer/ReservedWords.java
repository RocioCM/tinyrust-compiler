package lexic_analyzer;

public class ReservedWords {
	public static Token isReservedWord(Token token){
		if(token.getToken() != "id"){ // Si no es un identificador, no es una palabra reservada. Verificamos por las dudas
			return token;
		}else{
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
					return token;	
			}
		}
		return token;
	}
}
