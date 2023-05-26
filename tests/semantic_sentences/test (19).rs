class Base {
	pub Array Bool: a;
	fn m1() -> void {
		self.a = new I32[5]; // Tipo incorrecto de array.
	}
}

fn main() {
}/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 4 | COLUMNA 19 | SE ESPERABA UNA EXPRESION DE TIPO "Array-Bool" PERO SE ENCONTRO UNA EXPRESION DE TIPO "Array-I32" | */
