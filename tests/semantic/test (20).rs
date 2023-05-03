class Base {
	I32: x;
	fn m1() -> void {
	}
}

class Hola: Base {
	pub Str: s;
	I32: a;
	static fn m1() -> void {
	}
}

fn main() {
}

/* ERROR: SEMANTICO - DECLARACIONES
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 10 | COLUMNA 12 | NO ESTA PERMITIDO REDEFINIR METODOS ESTATICOS DE UNA SUPERCLASE. SE ESTA REDEFINIENDO EL METODO ESTATICO m1 |
 */
