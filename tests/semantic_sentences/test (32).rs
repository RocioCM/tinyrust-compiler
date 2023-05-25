class Ejemplo {
	I32: attr;
	static fn metodoEstatico() -> void {
		(self.metodoDinamico()); // ERROR. No se puede acceder a un atributo de instancia desde un metodo estatico.
	}
	fn metodoDinamico() -> void {}
}

fn main () {}
	