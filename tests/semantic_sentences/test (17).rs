class Clase {
	fn metodo() -> I32 {
		if (false) {
			if (false) {
				return 3;
			} else {while(true)(nil);}
			// Deberia incluir un return dentro del else o en esta linea o afuera del if mas externo.
		} else {
			{if (false){
				return 2;
			}};
		}
	}
}

fn main() {
}
