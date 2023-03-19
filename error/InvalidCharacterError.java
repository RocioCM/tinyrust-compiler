package error;

public class InvalidCharacterError extends LexicalError {

	public InvalidCharacterError(int line, int column, char invalidChar) {
		super(line, column, "CARACTER INVALIDO: " + invalidChar + " (ASCII: " + (int) invalidChar + ")");
	}

}
