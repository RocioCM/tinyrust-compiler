class Derivada : Base {
	I32: x;
	fn self(Str: a) -> void {
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
| LINEA 3 | COLUMNA 5 | NO ESTA PERMITIDO ASIGNAR EL IDENTIFICADOR "self" A ATRIBUTOS, VARIABLES O METODOS. | */
