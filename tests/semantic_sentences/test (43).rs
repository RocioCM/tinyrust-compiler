class Derivada : Base {
	pub Array Bool: x;
	fn m2(Str: a) -> void {
	}
}

class Base {
}

fn main() {
	Base a;
	a = new Derivada();
	a.x = new Bool[3]; // Error: No existe el atributo x en la clase Base, solo en la clase Derivada y no es accesible debido a que el tipo estatico de a es Base,
}
