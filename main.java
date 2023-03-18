import lexic_analyzer.Executor;

class TinyRustCompiler {
	public static void main(String[] args) {
		String inputPath = "./tests/test1.rs"; // TODO - Leer el path de entrada del comando.
		String outputPath = "./out"; // TODO - Leer el path de output del comando.

		new Executor().run(inputPath, outputPath);
	}
}