class Base {
	pub I32: a;
}

class Derivada: Base {
	fn m1(Str: a) -> void {
		a = 12; // Falla. El argumento "a" de tipo Str pisa el atributo "a" heredado de tipo I32.
	}
}

fn main() {
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 7 | COLUMNA 7 | SE ESPERABA UNA EXPRESION DE TIPO "Str" PERO SE ENCONTRO UNA EXPRESION DE TIPO "I32" | */
