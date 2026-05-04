package semantic_analyzer.symbol_table;

import java.io.FileNotFoundException;

import error.lexic.LexicalError;
import error.semantic.SemanticalError;
import error.semantic.sentences.ASTError;
import error.syntactic.SyntacticalError;
import semantic_analyzer.ast.AbstractSyntaxTree;
import syntactic_analyzer.SyntacticAnalyzer;
import util.Logger;

/**
 * Ejecutor que instancia la TS y el analizador sintáctico e inicia su
 * ejecución.
 * Muestra los resultados de la ejecución, interceptando todas las posibles
 * excepciones que se puedan lanzar durante el proceso.
 * Genera el archivo de salida JSON de la TS si el análisis es exitoso.
 */
public class Executor {
	public void run(String inputPath, String outputPath) throws FileNotFoundException {
		String basePath = inputPath.substring(0, inputPath.length() - 3); // Eliminar extensión .rs

		try {
			// Inicializar el analizador y las estructuras semánticas intermedias:
			SymbolTable ts = new SymbolTable(inputPath);
			AbstractSyntaxTree ast = new AbstractSyntaxTree(inputPath); // Se instancia pero no es validado.
			SyntacticAnalyzer syntactic = new SyntacticAnalyzer(inputPath, ts, ast);

			// Primera pasada de análisis:
			syntactic.run(); // Si la entrada no es sintácticamente correcta, esta llamada lanza una
			// excepción, por lo que si termina de ejecutarse, implica que la entrada es
			// correcta y la tabla de símbolos se construyó por completo.

			// Segunda pasada de análisis semántico:
			ts.consolidate();

			// Generar el archivo de salida:
			String tsJson = ts.toJson();

			Logger.success("SEMANTICO - DECLARACIONES", outputPath);

			Logger.createFile(tsJson, basePath.concat(".ts.json"));

		} catch (LexicalError error) {
			Logger.error("LEXICO", error, outputPath);
			System.exit(1);
		} catch (SyntacticalError error) {
			Logger.error("ANALISIS SINTACTICO", error, outputPath);
			System.exit(1);
		} catch (ASTError error) {
			Logger.error("SEMANTICO - SENTENCIAS", error, outputPath);
			System.exit(1);
		} catch (SemanticalError error) {
			Logger.error("SEMANTICO - DECLARACIONES", error, outputPath);
			System.exit(1);
		}
	}
}
