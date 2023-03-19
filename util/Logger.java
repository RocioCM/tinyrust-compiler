package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import error.LexicalError;
import lexic_analyzer.Token;

public class Logger {
	public static void lexicError(LexicalError error, String outputPath) {
		if (outputPath == null) { // No se especificó archivo de salida.
			// Se escribe el output en la terminal.
			System.out.println("\nERROR: LEXICO ");
			System.out.println("| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
			System.out.println(error.getMessage());
		} else {
			try {
				// Se abre el archivo de salida especificado y se escribe el output.
				FileWriter fileWriter = new FileWriter(outputPath);
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
											" | (COLUMNA " + token.getCol() + ") |"));
		} else {
			try {
				// Se abre el archivo de salida especificado y se escribe el output.
				FileWriter fileWriter = new FileWriter(outputPath);

				fileWriter.write("CORRECTO: ANALISIS LEXICO\n");
				fileWriter.write("| TOKEN | LEXEMA | NUMERO DE LINEA (NUMERO DE COLUMNA) |\n");

				for (int i = 0; i < tokensList.size(); i++) {
					Token token = tokensList.get(i);
					fileWriter.write(
							"| " + token.getToken() +
									" | " + token.getLexema() +
									" | LINEA " + token.getLine() +
									" | (COLUMNA " + token.getCol() + ") |\n");
				}

				fileWriter.close();

			} catch (IOException e) {
				System.out.println("ERROR IO: NO SE PUDO ESCRIBIR EN EL ARCHIVO DE SALIDA.");
				System.exit(1);
			}
		}
	}
}
