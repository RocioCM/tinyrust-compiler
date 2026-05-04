class Fibonacci {
	I32: suma;
	I32: i,j;
	fn sucesion_fib(I32: n) -> void {
		i=0; j=0; suma=0;
		while (i<= n){
			if (i==0){
				(imprimo_numero(i));
				(imprimo_sucesion(suma));
			}
			else if(i==1){
				(imprimo_numero(i));
				suma=suma+i;
				(imprimo_sucesion(suma));
			}
			else{
				(imprimo_numero(i));
				suma=suma+j;
				j=suma;
				(imprimo_sucesion(suma));
			}
			i = i + 1;
		}
	}
	create(I32: n){
		i=0;
		j=0;
		suma=0;
		(sucesion_fib(n));
	}
	fn imprimo_numero(I32: num) -> void {
		(IO.out_str("f_"));
		(IO.out_i32(num));
		(IO.out_str("="));
	}
	fn imprimo_sucesion(I32: s) -> void {
		//"el valor es: ";
		(IO.out_i32(s));
		(IO.out_str("\n"));
	}
}
fn main(){
	Fibonacci: fib;
	I32: n;
	n = IO.in_i32();
	fib = new Fibonacci(n);
}

