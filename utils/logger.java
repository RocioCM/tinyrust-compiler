package utils;

class Logger {
	public static void lexicError(String token, String lexema, String line, String col){
		System.out.println("ERROR: LEXICO ");
		System.out.println("| LINEA " + line + " | COLUMNA " + col + " | TOKEN " + token + " | LEXEMA " + lexema);
	}

	public static void lexicSuccess(String token, String lexema, String line, String col){
		System.out.println("CORRECTO: LEXICO ");
		System.out.println(token + " | " + lexema + " | LINEA " + line + " (COLUMNA " + col + ")");
	}
}
