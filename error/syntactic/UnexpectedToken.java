package error.syntactic;

import lexic_analyzer.Token;

public class UnexpectedToken extends SyntacticalError {

	public UnexpectedToken(Token token, String expectedType) {
		super(token.getLine(), token.getCol(),
				"TOKEN INESPERADO: SE ESPERABA UN TOKEN " + expectedType + " Y SE ENCONTRO EL LEXEMA " + token.getLexema());
	}

}
