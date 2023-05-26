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
