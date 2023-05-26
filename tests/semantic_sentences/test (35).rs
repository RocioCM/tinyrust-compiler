class Ejemplo {
	fn m1() -> I32 {
		while(true) {
			return 1;
		}
	}
	fn m2()-> I32 {
		while(true) {
			if (true) {
				return 1;
			}
			// Falta return aca.
		}
		// o aca.
	}
}

fn main () {
	return nil;
} 
	