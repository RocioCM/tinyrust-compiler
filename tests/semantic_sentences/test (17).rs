class Clase {
	fn metodo() -> I32 {
		if (false) {
			return 2;
		} else {
			return 1;
		}

		if (false) {
			return 3;
		}

		if (false) {
			// Deberia incluir un return dentro del if.
		} else {
			return 1;
		}
	}
}

fn main() {
}
