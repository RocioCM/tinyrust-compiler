class A {
}

class B: A {
}

class C {
}

class Clase: B {
	fn metodo() -> Clase {
		return new C();
	}
}

fn main() {
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 12 | COLUMNA 14 | SE ESPERABA UNA EXPRESION DE TIPO "Clase" PERO SE ENCONTRO UNA EXPRESION DE TIPO "C" | */
