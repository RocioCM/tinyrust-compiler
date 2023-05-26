class Derivada {
	I32: x;
	fn m2(Str: a) -> void {
		self.x = x;
	}
}

class Base {

}


fn main() {
	Derivada: a;
	a = new Base();
}
/* ERROR: SEMANTICO - SENTENCIAS
| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
| LINEA 15 | COLUMNA 10 | SE ESPERABA UNA EXPRESION DE TIPO "Derivada" PERO SE ENCONTRO UNA EXPRESION DE TIPO "Base" | */
