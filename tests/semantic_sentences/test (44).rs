class Base {
	pub Str: s;
	I32: a;
	fn m1() -> void {
		(m1().a); // m1 retorna void, no se puede acceder "a".
	}
}

fn main() {
}
/* CORRECTO: SEMANTICO - DECLARACIONES */
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 5 | COLUMNA 4 | EL METODO m1 NO RETORNA NINGUN VALOR, NO SE PUEDE ACCEDER A ENCADENADOS DE void. | */
