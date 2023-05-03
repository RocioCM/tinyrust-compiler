class Derivada : Base {
	I32: x;
	fn m2(Str: a) -> void {
	}
}

class Base: Derivada {
	pub Str: s;
	I32: a;
	fn m1() -> void {
	}
}

fn main() {
	Base: b1;
	b1 = new Derivada();
	(b1.m1());
}
/* ERROR: SEMANTICO - DECLARACIONES
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 7 | COLUMNA 13 | HERENCIA CIRCULAR: LA CLASE Base HEREDA DE LA CLASE Derivada QUE HEREDA DIRECTA O INDIRECTAMENTE DE Base | */
