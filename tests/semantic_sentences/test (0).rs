class Fibonacci {
	I32: suma;
	I32: i,j;
	fn sucesion_fib(I32: n)-> void {
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
		}
	}
	create(){
		i=0;
		j=0;
		suma=0;
	}
	fn imprimo_numero(I32: num) -> void{
		(IO.out_string("f_"));
		(IO.out_i32(num));
		(IO.out_string("="));
	}
	fn imprimo_sucesion(I32: s) -> void{
		//"el valor es: ";
		(IO.out_i32(s));
		(IO.out_string("\n"));
	}
}
fn main(){
	Fibonacci: fib;
	I32: n;
	fib = new Fibonacci();
	n = IO.in_i32();
	(fib.sucesion_fib(n));
}

