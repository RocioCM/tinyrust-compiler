package lexic_analyzer;

public class LexicalError extends Exception {

	public LexicalError(String message) {
		super(message);
	}
}

// public class BadIdentifierError extends LexicalError {
// }

// public class UnclosedMultiLineCommentError extends LexicalError {
// }

// public class InvalidCharactedError extends LexicalError {
// }
