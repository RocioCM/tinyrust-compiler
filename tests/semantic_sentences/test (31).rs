class Ejemplo {
	I32: attr;
	static fn metodoEstatico() -> void {
		(metodoDinamico()); // ERROR. No se puede acceder a un atributo de instancia desde un metodo estatico.
	}
	fn metodoDinamico() -> void {}
}

fn main () {}
	/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 4 | COLUMNA 4 | SE INTENTO ACCEDER AL METODO NO ESTATICO metodoDinamico DENTRO DEL METODO ESTATICO metodoEstatico. NO SE PERMITE ACCEDER A METODOS DINAMICOS DENTRO DE UN CONTEXTO ESTATICO. | */
