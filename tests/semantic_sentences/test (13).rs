class Clase {
	fn metodo() -> I32 {
		{
			{
				while (12 < 3 * 2) {
					return; // mal, espera i32.
				}
			}
		}
	}
}

fn main() {
}
