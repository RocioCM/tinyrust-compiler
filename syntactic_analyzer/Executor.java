package syntactic_analyzer;

import java.io.FileNotFoundException;

import error.lexic.LexicalError;
import error.semantic.SemanticalError;
import error.syntactic.SyntacticalError;
import util.Logger;

/**
 * Ejecutor que instancia el analizador sintáctico e inicia su ejecución.
 * Muestra los resultados de la ejecución, interceptando todas las posibles
 * excepciones que se puedan lanzar durante el proceso.
 */
public class Executor {
	public void run(String inputPath, String outputPath) throws FileNotFoundException {
		try {
			// Patrón Singleton: se utiliza una única instancia del analizador.
			SyntacticAnalyzer syntactic = new SyntacticAnalyzer(inputPath);

			syntactic.run();

			Logger.success("ANALISIS SINTACTICO", outputPath);

		} catch (LexicalError error) {
			Logger.error("LEXICO", error, outputPath);
			System.exit(1);
		} catch (SyntacticalError error) {
			Logger.error("SINTACTICO", error, outputPath);
			System.exit(1);
		} catch (SemanticalError error) {
			Logger.error("SEMANTICO - DECLARACIONES", error, outputPath);
			System.exit(1);
		}
	}
}
