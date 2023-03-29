package syntactic_analyzer;

import java.io.FileNotFoundException;

import error.lexic.LexicalError;
import error.syntactic.SyntacticalError;
import lexic_analyzer.LexicAnalyzer;

public class SyntacticAnalizer {
	private LexicAnalyzer lexic;

	public SyntacticAnalizer(String inputPath) throws FileNotFoundException {
		// Patrón Singleton: se utiliza una única instancia de la clase LexicAnalyzer.
		lexic = new LexicAnalyzer(inputPath);
	}

	public boolean run() throws LexicalError, SyntacticalError {
		/// TODO: implementarlo xD
		// Identificar todos los tokens del archivo de entrada.
		while (lexic.hasNextToken()) {
			lexic.nextToken();
		}
		return true;
	}
}
