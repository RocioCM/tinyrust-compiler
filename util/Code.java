package util;

public class Code {
	private String code;

	public Code() {
		this.code = "";
	}

	public Code(String code) {
		this.code = code;
	}

	public void addLine(String line) {
		this.code = code + line + "\n";
	}

	public String getCode() {
		return code;
	}
}
