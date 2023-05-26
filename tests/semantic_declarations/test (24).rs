class Hola {
	fn m1(I32: name) -> void {
		Str: name; // Error: el nombre de la variable colisiona con el nombre de un argumento.
	}
}

fn main() {
}

/* ERROR: SEMANTICO - DECLARACIONES 
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 3 | COLUMNA 8 | UNA DECLARACION DE UN ARGUMENTO CON EL NOMBRE "name" YA EXISTE EN ESTE AMBITO. | */
