class Ejemplo {
	I32: attr;
	static fn metodoEstatico() -> I32 {
		I32: var;
		Ejemplo: v;
		(var); // Es valido acceder a variables locales.
		(attr); // ERROR. No se puede acceder a un atributo de instancia desde un metodo estatico.
		return 1;
	}
}

fn main () {}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 7 | COLUMNA 4 | SE INTENTO ACCEDER AL ATRIBUTO attr DENTRO DEL METODO ESTATICO metodoEstatico. NO SE PERMITE ACCEDER A ATRIBUTOS DINAMICOS DENTRO DE UN CONTEXTO ESTATICO. | */
