import java.io.File;
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
			// Se leen las rutas de los archivos de los argumentos de entrada.
			String inputPath = "args[0]"; /// TODO: des-hardcodear los tests.
			String outputPath = null;

			// Si se especificó una segunda ruta, se inicializa el archivo de salida.
			if (args.length > 1) {
				outputPath = args[1];

				File outputFile = new File(outputPath);
				// Si el archivo ya existe, lo sobreescribimos
				if (outputFile.createNewFile()) {
					System.out.println("ARCHIVO DE SALIDA CREADO EN " + outputPath);
				} else {
					System.out.println("ARCHIVO DE SALIDA SOBREESCRITO EN " + outputPath);
				}
			}

			// Se inicializa la ejecución del compilador.
			for (int i = 68; i < 76; i++) {
				inputPath = "./tests/syntactic/test (" + i + ").rs";
				System.out.println("------------> \nEjecutando " + inputPath);
				new Executor().run(inputPath, outputPath);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("ERROR: DEBE ESPECIFICARSE UN ARCHIVO DE ENTRADA EN EL COMANDO.");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("ERROR IO: NO SE PUDO CREAR EL ARCHIVO DE SALIDA.");
			System.exit(1);
		}
	}
}