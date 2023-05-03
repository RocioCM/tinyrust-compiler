class Derivada : Base {
	I32: x;
	fn m2(Str: a) -> void {
	}
}

class Base: Base2 {
	pub Str: s;
	I32: a;
	fn m1() -> void {
	}
}

class Base2: Base3 {
	pub Str: s;
	I32: a;
	fn m1() -> void {
	}
}

class Base3: Base {
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
| LINEA 21 | COLUMNA 14 | HERENCIA CIRCULAR: LA CLASE Base3 HEREDA DE LA CLASE Base QUE HEREDA DIRECTA O INDIRECTAMENTE DE Base3 | */
