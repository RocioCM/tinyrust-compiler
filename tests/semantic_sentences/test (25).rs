class Derivada  {
	I32: x;
	Array Bool: y;
}

fn main() {
	(new Derivada().y[0]); // Atributo privado.
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 7 | COLUMNA 18 | EL CAMPO y DE LA CLASE Derivada NO ES VISIBLE EN ESTE CONTEXTO PORQUE ES PRIVADO. | */
