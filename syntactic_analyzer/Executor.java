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
	public void run(String inputPath) {
		String outputPath = null; // En esta etapa toda la salida se hace por pantalla.
		String basePath = inputPath.substring(0, inputPath.length() - 3); // Eliminar extensión .rs

		try {
			// Patrón Singleton: se utiliza una única instancia del analizador.
			SyntacticAnalyzer syntactic = new SyntacticAnalyzer(inputPath);

			String tsJson = syntactic.run();
			String astJson = syntactic.getAstJson();

			Logger.semanticDeclSuccess(outputPath);
			Logger.createJson(tsJson, basePath.concat(".ts.json"));
			Logger.createJson(astJson, basePath.concat(".ast.json"));
		} catch (FileNotFoundException error) {
			System.out.println("\nERROR IO: NO SE ENCONTRO UN ARCHIVO DE ENTRADA EN LA RUTA " + inputPath);
			System.exit(1);
		} catch (LexicalError error) {
			Logger.lexicError(error, outputPath);
			System.exit(1);
		} catch (SyntacticalError error) {
			Logger.syntacticError(error, outputPath);
			System.exit(1);
		} catch (SemanticalError error) {
			Logger.semanticDeclError(error, outputPath);
			System.exit(1);
		}
	}
}
