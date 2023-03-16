import lexic_analyzer.LexicAnalyzer;

class TinyRustCompiler {
	public static void main(String[] args) {
		LexicAnalyzer lexic = new LexicAnalyzer("./tests/test1.rs");
		lexic.nextToken();
		lexic.nextToken();
	}
}