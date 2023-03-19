package util;

import java.util.ArrayList;

import error.LexicalError;
import lexic_analyzer.Token;

public class Logger {
	// TODO - Enviar el Standard Output al archivo de salida (del comando).

	public static void lexicError(LexicalError error) {
		System.out.println("\nERROR: LEXICO ");
		System.out.println("| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
		System.out.println(error.getMessage());
	}

	public static void lexicSuccess(ArrayList<Token> tokensList) {
		System.out.println("\nCORRECTO: ANALISIS LEXICO");
		System.out.println("| TOKEN | LEXEMA | NUMERO DE LINEA (NUMERO DE COLUMNA) |");

		tokensList
				.forEach(
						token -> System.out.println(
								"| " + token.getToken() +
										" | " + token.getLexema() +
										" | LINEA " + token.getLine() +
										" | (COLUMNA " + token.getCol() + ") |"));
	}
}
