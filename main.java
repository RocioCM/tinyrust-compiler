import lexic_analyzer.LexicAnalyzer;

class TinyRustCompiler {
	public static void main(String[] args) {
		System.out.println("Hello, World!");
		LexicAnalyzer lexic = new LexicAnalyzer("./tests/test1.rs");
		lexic.nextToken();
	}
}