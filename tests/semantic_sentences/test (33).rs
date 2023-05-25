class Ejemplo {
	I32: attr;
	fn metodoEstatico() -> I32 {
		I32: var;
		Ejemplo: v;
		(var); // Es valido acceder a variables locales.
		(attr); // ERROR. No se puede acceder a un atributo de instancia desde un metodo estatico.
	}
}

fn main () {} // TODO check this test outputs the right error.
	