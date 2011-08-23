package compilationunit;

public class Main {
	
	public static void main(String[] arg) {

		if (arg.length != 1) {	// Tenemos que recibir el fichero de codigo
			System.err.println("Error: Se espera un fichero de código de entrada.");
		} else {
			// Se recibe fichero de codigo!
			Scanner scanner = new Scanner(arg[0]);
			Parser parser = new Parser(scanner);
			parser.Parse();
			if (parser.errors.count == 0) {	// todo ok!
				System.out.println(parser.errors.count + " errores detectados.");
				System.out.println("Fichero Codigo Objeto almacenado en \""+parser.fichero+"\"");				
			} else {	// Errores detectados
				System.err.println(parser.errors.count + " errores detectados.");	
			}
		}		
	}
}


