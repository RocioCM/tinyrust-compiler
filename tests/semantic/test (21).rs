class Hola {
	fn m1() -> void {}
	fn m2() -> ClaseInexistente {}
}

fn main() {
}

/* ERROR: SEMANTICO - DECLARACIONES 
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 3 | COLUMNA 5 | LA CLASE DE RETORNO DEL METODO m2 ES DEL TIPO NO DECLARADO ClaseInexistente | */
