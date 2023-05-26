class Hola {
	fn m1() -> void {}
	fn m2() -> Hola {
		ClaseInexistente: hola, chau;
	}
}

fn main() {
}

/* ERROR: SEMANTICO - DECLARACIONES 
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 4 | COLUMNA 27 | LA VARIABLE chau EN EL BLOQUE DEL METODO m2 ES DEL TIPO NO DECLARADO ClaseInexistente | */
