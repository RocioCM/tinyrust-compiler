import lexic_analyzer.Executor;

class TinyRustCompiler {
	public static void main(String[] args) {
		String inputPath = "./tests/test1.rs"; // TODO - Leer el path de entrada del comando.
		String outputPath = "./out"; // TODO - Leer el path de output del comando.

		for (int i = 1; i <= 2; i++) {
			inputPath = "./tests/test (" + i + ").rs";
			new Executor().run(inputPath, outputPath);
		}
	}
}