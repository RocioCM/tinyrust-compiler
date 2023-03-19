import java.io.File;

import lexic_analyzer.Executor;

class TinyRustCompiler {
	public static void main(String[] args) {
		String inputPath = args[0];
		String outputPath = args[1];

		try{
			File outputFile = new File(outputPath);
			//Si el archivo ya existe, lo sobreescribimos
			if(outputFile.createNewFile()){
				System.out.println("Archivo de salida creado");
			}else{
				System.out.println("Sobreescribiendo archivo de salida");
			}
			
			new Executor().run(inputPath, outputPath);
		}catch(Exception e){
			System.out.println("Error: No se ha ingresado un archivo de entrada");
			System.exit(0);
		}
	}
}