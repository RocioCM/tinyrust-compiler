import java.io.IOException;

import syntactic_analyzer.Executor;

/**
 * Punto de entrada del compilador de TinyRust+.
 * Analiza los argumentos para la ejecución recibidos en el comando e inicia el
 * proceso de compilación.
 */
class TinyRustCompiler {
	public static void main(String[] args) {
		try {
			// Se lee la ruta del archivo de los argumentos de entrada.
			String inputPath = "./tests/semantic/test (2).rs"; /// TODO: RESTORE args[0];

			if (!inputPath.endsWith(".rs")) {
				throw new IOException("ERROR: EL ARCHIVO DE ENTRADA DEBE TENER EXTENSION .rs");
			}

			// Se inicializa la ejecución del compilador.
			new Executor().run(inputPath);

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("ERROR: DEBE ESPECIFICARSE UN ARCHIVO DE ENTRADA EN EL COMANDO.");
			System.exit(1);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
}