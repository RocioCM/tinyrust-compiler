class Derivada : Base {
	I32: x;
}

class Base {
	I32: x;
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
| LINEA 6 | COLUMNA 7 | NO ESTA PERMITIDO REDEFINIR ATRIBUTOS DE UNA SUPERCLASE. SE ESTA REDEFINIENDO EL ATRIBUTO x | */
