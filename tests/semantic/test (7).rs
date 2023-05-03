class Derivada : Base {
	I32: self;
	fn m2(Str: a) -> void {
	}
}

class Base {
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
| LINEA 2 | COLUMNA 7 | NO ESTA PERMITIDO ASIGNAR EL IDENTIFICADOR "self" A ATRIBUTOS, VARIABLES O METODOS. | */
