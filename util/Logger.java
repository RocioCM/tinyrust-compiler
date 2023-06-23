package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
			System.out.println("CORRECTO: ANALISIS LEXICO");
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
				FileWriter fileWriter = getOutputFileWriter(outputPath, true);

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
	 * Muestra con formato un error generado en alguna etapa de análisis.
	 * 
	 * @param type       - Label de la etapa en que ocurrió el error.
	 * @param error      - error generado por el compilador de TinyRust+
	 * @param outputPath (Opcional) - Ruta del archivo de salida
	 */
	public static void error(String type, Exception error, String outputPath) {
		if (outputPath == null) { // No se especificó archivo de salida.
			// Se escribe el output en la terminal.
			System.out.println("ERROR: " + type);
			System.out.println("| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
			System.out.println(error.getMessage());
		} else {
			try {
				// Se abre el archivo de salida especificado y se escribe el output.
				FileWriter fileWriter = getOutputFileWriter(outputPath, true);
				fileWriter.write("ERROR: " + type + "\n");
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
	 * Imprime un mensaje de éxito en el proceso de compilación.
	 * 
	 * @param type       - Label del la etapa finalizada exitosamente
	 * @param outputPath (Opcional) - Ruta del archivo de salida
	 */
	public static void success(String type, String outputPath) {
		if (outputPath == null) { // No se especificó archivo de salida.
			// Se escribe el output en la terminal.
			System.out.println("CORRECTO: " + type);
		} else {
			try {
				// Se abre el archivo de salida especificado y se escribe el output.
				FileWriter fileWriter = getOutputFileWriter(outputPath, true);
				fileWriter.write("CORRECTO: " + type + "\n");
				fileWriter.close();

			} catch (IOException e) {
				System.out.println("ERROR IO: NO SE PUDO ESCRIBIR EN EL ARCHIVO DE SALIDA.");
				System.exit(1);
			}
		}
	}

	/**
	 * Guarda en el archivo destino el código generado a partir de la Tabla de
	 * Símbolos o AST.
	 * 
	 * @param json       - Cadena en cierto formato de código.
	 * @param outputPath - Ruta del archivo de salida.
	 */
	public static void createFile(String json, String outputPath) {
		try {
			// Se abre el archivo de salida especificado y se escribe el output.
			FileWriter fileWriter = getOutputFileWriter(outputPath, false);
			fileWriter.write(json);
			fileWriter.close();
		} catch (IOException e) {
			System.out.println("ERROR IO: NO SE PUDO ESCRIBIR EN EL ARCHIVO DE SALIDA.");
			System.exit(1);
		}
	}

	/**
	 * Crea un archivo de salida si aun no existe e inicializa un FileWriter para
	 * ese archivo.
	 * 
	 * @param outputPath - Ruta del archivo de salida.
	 * @param append     - true para escribir al final del archivo de entrada,
	 *                   false para sobreescribirlo.
	 * @return FileWriter para el archivo de salida.
	 * @throws IOException - Si el archivo no pudo ser creado o abierto.
	 */
	private static FileWriter getOutputFileWriter(String outputPath, boolean append) throws IOException {
		File outputFile = new File(outputPath);
		outputFile.createNewFile(); // Crear el archivo de salida si aun no existe.

		// Si el archivo ya existe, se escribe el output al final del archivo existente.
		return new FileWriter(outputPath, append);
	}
}
