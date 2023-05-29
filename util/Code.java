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
		code.concat("\n" + line);
	}

	public String getCode() {
		return code;
	}
}
