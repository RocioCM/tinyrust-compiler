package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import error.LexicalError;
import lexic_analyzer.Token;

public class Logger {

	public static void lexicError(LexicalError error, String outputPath) {

		try{
			FileWriter fileWriter = new FileWriter(outputPath);
			
			fileWriter.write("\nERROR: LEXICO ");
			fileWriter.write("\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
			fileWriter.write(error.getMessage());
			fileWriter.close();
		}catch(IOException e){
			System.out.println("Error de archivo");
			System.exit(0);
		}
	}

	public static void lexicSuccess(ArrayList<Token> tokensList, String outputPath) {
		try{
			FileWriter fileWriter = new FileWriter(outputPath);
			
			fileWriter.write("\nCORRECTO: ANALISIS LEXICO");
			fileWriter.write("\n| TOKEN | LEXEMA | NUMERO DE LINEA (NUMERO DE COLUMNA) |");
			System.out.println(tokensList.size());
			tokensList
					.forEach(
							token -> {
								try {
									fileWriter.write(
											"\n| " + token.getToken() +
													" | " + token.getLexema() +
													" | LINEA " + token.getLine() +
													" | (COLUMNA " + token.getCol() + ") |");
								} catch (IOException e) {
									e.printStackTrace();
								}
							});

			fileWriter.close();

		}catch(IOException e){
			System.out.println("Error de archivo");
			System.exit(0);
		}


	}
}
