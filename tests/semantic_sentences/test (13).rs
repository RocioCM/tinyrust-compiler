class Clase {
	fn metodo() -> I32 {
		{
			{
				while (true) {
					return; // mal, espera i32.
				}
			}
		}
	}
}

fn main() {
}
