package util;

import java.util.ArrayList;
import java.util.List;

import semantic_analyzer.symbol_table.TableElement;

public class Json {
	private ArrayList<String> attributes;

	public Json() {
		this.attributes = new ArrayList<String>();
	}

	static public String join(String... strings) {
		return String.join("", strings);
	}

	public void addAttr(String key, String value) {
		String json = "null";
		if (value != null) {
			json = '"' + value + '"';
		}
		attributes.add(Json.join("\"", key, "\": ", json));
	}

	public void addAttr(String key, Number value) {
		attributes.add(Json.join("\"", key, "\": ", String.valueOf(value)));
	}

	public void addAttr(String key, boolean value) {
		attributes.add(Json.join("\"", key, "\": ", String.valueOf(value)));
	}

	public void addAttr(String key, TableElement value) {
		String json = "null";
		if (value != null) {
			json = value.toJson();
		}
		attributes.add(Json.join("\"", key, "\": ", json));
	}

	public String toString() {
		if (attributes.isEmpty()) {
			return "{}";
		}
		return Json.join("{\n\t", String.join(",\n", attributes).replaceAll("\n", "\n\t"), "\n}");
	}

	static public String toStringArray(List<String> values) {
		if (values.isEmpty()) {
			return "[]";
		}
		return Json.join("[\n\t", String.join(",\n", values).replaceAll("\n", "\n\t"), "\n]");
	}
}
