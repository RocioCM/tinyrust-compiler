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
			String inputPath = args[0];
			String outputPath = null;

			if (!inputPath.endsWith(".rs")) {
				throw new IOException("ERROR: EL ARCHIVO DE ENTRADA DEBE TENER EXTENSION .rs");
			}

			// Si se especificó una segunda ruta, se inicializa el archivo de salida.
			if (args.length > 1) {
				outputPath = args[1];
			}

			// Se inicializa la ejecución del compilador.
			new Executor().run(inputPath, outputPath);

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("ERROR: DEBE ESPECIFICARSE UN ARCHIVO DE ENTRADA EN EL COMANDO.");
			System.exit(1);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
}