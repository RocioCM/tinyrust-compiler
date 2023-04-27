package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import error.lexic.LexicalError;
import error.semantic.SemanticalError;
import error.syntactic.SyntacticalError;
import lexic_analyzer.Token;

/**
 * Provee métodos estáticos para hacer output de información relacionada al
 * compilado de TinyRust+.
 * Dependiendo de si se especificó un archivo de salida,
 * el output se guardará en dicho archivo o se hará mediante Standard Output.
 */
public class Logger {
	/**
	 * Muestra con formato la lista de tokens generados por el analizador léxico.
	 * 
	 * @param tokensList - lista de tokens generados por el analizador léxico.
	 * @param outputPath (Opcional) - Ruta del archivo de salida
	 */
	public static void lexicSuccess(ArrayList<Token> tokensList, String outputPath) {
		if (outputPath == null) { // No se especificó archivo de salida.
			// Se escribe el output en la terminal.
			System.out.println("\nCORRECTO: ANALISIS LEXICO");
			System.out.println("| TOKEN | LEXEMA | NUMERO DE LINEA (NUMERO DE COLUMNA) |");

			tokensList
					.forEach(
							token -> System.out.println(
									"| " + token.getToken() +
											" | " + token.getLexema() +
											" | LINEA " + token.getLine() +
											" | (COLUMNA " + token.getCol() +
											") |"));
		} else {
			try {
				// Se abre el archivo de salida especificado y se escribe el output.
				FileWriter fileWriter = new FileWriter(outputPath, true);

				fileWriter.write("CORRECTO: ANALISIS LEXICO\n");
				fileWriter.write("| TOKEN | LEXEMA | NUMERO DE LINEA (NUMERO DE COLUMNA) |\n");

				for (int i = 0; i < tokensList.size(); i++) {
					Token token = tokensList.get(i);
					fileWriter.write(
							"| " + token.getToken() +
									" | " + token.getLexema() +
									" | LINEA " + token.getLine() +
									" | (COLUMNA " + token.getCol() +
									") |\n");
				}

				fileWriter.close();

			} catch (IOException e) {
				System.out.println("ERROR IO: NO SE PUDO ESCRIBIR EN EL ARCHIVO DE SALIDA.");
				System.exit(1);
			}
		}
	}

	/**
	 * Muestra con formato un error generado por el analizador léxico
	 * 
	 * @param error      - error generado por el analizador léxico
	 * @param outputPath (Opcional) - Ruta del archivo de salida
	 */
	public static void lexicError(LexicalError error, String outputPath) {
		if (outputPath == null) { // No se especificó archivo de salida.
			// Se escribe el output en la terminal.
			System.out.println("\nERROR: LEXICO ");
			System.out.println("| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
			System.out.println(error.getMessage());
		} else {
			try {
				// Se abre el archivo de salida especificado y se escribe el output.
				FileWriter fileWriter = new FileWriter(outputPath, true);
				fileWriter.write("ERROR: LEXICO\n");
				fileWriter.write("| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |\n");
				fileWriter.write(error.getMessage() + "\n");
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("ERROR IO: NO SE PUDO ESCRIBIR EN EL ARCHIVO DE SALIDA.");
				System.exit(1);
			}
		}
	}

	/**
	 * Imprime un mensaje de éxito en el análisis sintáctico.
	 * 
	 * @param outputPath (Opcional) - Ruta del archivo de salida
	 */
	public static void syntacticSuccess(String outputPath) {
		if (outputPath == null) { // No se especificó archivo de salida.
			// Se escribe el output en la terminal.
			System.out.println("\nCORRECTO: ANALISIS SINTACTICO");
		} else {
			try {
				// Se abre el archivo de salida especificado y se escribe el output.
				FileWriter fileWriter = new FileWriter(outputPath, true);
				fileWriter.write("CORRECTO: ANALISIS SINTACTICO\n");
				fileWriter.close();

			} catch (IOException e) {
				System.out.println("ERROR IO: NO SE PUDO ESCRIBIR EN EL ARCHIVO DE SALIDA.");
				System.exit(1);
			}
		}
	}

	/**
	 * Muestra con formato un error generado por el analizador sintáctico
	 * 
	 * @param error      - error generado por el analizador sintáctico
	 * @param outputPath (Opcional) - Ruta del archivo de salida
	 */
	public static void syntacticError(SyntacticalError error, String outputPath) {
		if (outputPath == null) { // No se especificó archivo de salida.
			// Se escribe el output en la terminal.
			System.out.println("\nERROR: SINTACTICO ");
			System.out.println("| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
			System.out.println(error.getMessage());
		} else {
			try {
				// Se abre el archivo de salida especificado y se escribe el output.
				FileWriter fileWriter = new FileWriter(outputPath, true);
				fileWriter.write("ERROR: SINTACTICO\n");
				fileWriter.write("| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |\n");
				fileWriter.write(error.getMessage() + "\n");
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("ERROR IO: NO SE PUDO ESCRIBIR EN EL ARCHIVO DE SALIDA.");
				System.exit(1);
			}
		}
	}

	/**
	 * Imprime un mensaje de éxito en del análisis semántico de declaraciones.
	 * 
	 * @param outputPath (Opcional) - Ruta del archivo de salida
	 */
	public static void semanticDeclSuccess(String outputPath) {
		if (outputPath == null) { // No se especificó archivo de salida.
			// Se escribe el output en la terminal.
			System.out.println("\nCORRECTO: SEMANTICO - DECLARACIONES");
		} else {
			try {
				// Se abre el archivo de salida especificado y se escribe el output.
				FileWriter fileWriter = new FileWriter(outputPath, true);
				fileWriter.write("\nCORRECTO: SEMANTICO - DECLARACIONES\n");
				fileWriter.close();

			} catch (IOException e) {
				System.out.println("ERROR IO: NO SE PUDO ESCRIBIR EN EL ARCHIVO DE SALIDA.");
				System.exit(1);
			}
		}
	}

	/**
	 * Muestra con formato un error generado durante el análisis semántico de
	 * declaraciones.
	 * 
	 * @param error      - error generado por el analizador sintáctico
	 * @param outputPath (Opcional) - Ruta del archivo de salida
	 */
	public static void semanticDeclError(SemanticalError error, String outputPath) {
		if (outputPath == null) { // No se especificó archivo de salida.
			// Se escribe el output en la terminal.
			System.out.println("\nERROR: SEMANTICO - DECLARACIONES ");
			System.out.println("| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
			System.out.println(error.getMessage());
		} else {
			try {
				// Se abre el archivo de salida especificado y se escribe el output.
				FileWriter fileWriter = new FileWriter(outputPath, true);
				fileWriter.write("\nERROR: SEMANTICO - DECLARACIONES\n");
				fileWriter.write("| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |\n");
				fileWriter.write(error.getMessage() + "\n");
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("ERROR IO: NO SE PUDO ESCRIBIR EN EL ARCHIVO DE SALIDA.");
				System.exit(1);
			}
		}
	}

	/**
	 * Guarda en el archivo destino el JSON generado a partir de la Tabla de
	 * Símbolos.
	 * 
	 * @param json       - Cadena en formato JSON
	 * @param outputPath - Ruta del archivo de salida.
	 */
	public static void createTsJson(String json, String outputPath) {
		try {
			// Se abre el archivo de salida especificado y se escribe el output.
			FileWriter fileWriter = new FileWriter(outputPath, true);
			fileWriter.write(json);
			fileWriter.close();
		} catch (IOException e) {
			System.out.println("ERROR IO: NO SE PUDO ESCRIBIR EN EL ARCHIVO DE SALIDA.");
			System.exit(1);
		}
	}

}
