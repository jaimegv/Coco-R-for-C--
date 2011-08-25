package compilationunit;

public class Main {
	
	public static void main(String[] arg) {

		if (arg.length < 1) {	// Tenemos que recibir el fichero de codigo de entrada
			System.err.println("Error: Se esperaba fichero de codigo de entrada.");
		} else if (arg.length < 2) {	// Tenemos que recibir el fichero de salida
			System.err.println("Error: Se esperaba fichero de salida.");			
		} else {
			try {
				// Se recibe fichero de codigo de entrada!
				Scanner scanner = new Scanner(arg[0]);	// lexico
				Parser parser = new Parser(scanner);	// sintactico-seman
				parser.fichero=arg[1];					// fichero de salida
				parser.Parse();
				if (parser.errors.count == 0) {	// todo ok!
					System.out.println(parser.errors.count + " errores detectados.");
					System.out.println("Fichero Codigo Objeto almacenado en \""+parser.fichero+"\"");				
				} else {	// Errores detectados
					System.err.println(parser.errors.count + " errores detectados.");	
				}
			} catch( Exception e ) {
				// Captura de cualquier excepcion en ultima instancia
				System.err.println("¡Tranquilo vaquero!.Main");
			}
		}		
	}
}


