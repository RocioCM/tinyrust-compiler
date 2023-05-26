class Derivada {
	I32: x;
}

fn main() {
	(new Derivada().x); // Atributo privado.
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 6 | COLUMNA 18 | EL CAMPO x DE LA CLASE Derivada NO ES VISIBLE EN ESTE CONTEXTO PORQUE ES PRIVADO. | */
