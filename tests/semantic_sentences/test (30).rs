class Ejemplo {
	I32: attr;
	static fn metodoEstatico() -> void {
		(self.attr); // ERROR. No se puede acceder a un atributo de instancia desde un metodo estatico.
	}
	fn metodoDinamico() -> void {}
}

fn main () {}
	/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 4 | COLUMNA 4 | NO SE PERMITE ACCEDER A LA REFERENCIA "self" DENTRO DE UN METODO ESTATICO. | */
