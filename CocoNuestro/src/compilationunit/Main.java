package compilationunit;

public class Main {
	public static void main(String[] args) {
		String inputName = null;
		// En un string todos los argumentos pasados.
        for (int i = 0; i < args.length; i++) {
                inputName = args[i];
        }
        // si vacio -> no hay fichero de codigo
        if (inputName == null) {
            System.err.println("No hay archivo de entrada seleccionado");
            System.exit(1);
        }
    
        // Creamos el objeto Scanner y le pasamos el fichero de codigo
		Scanner scanner = new Scanner(inputName);
		// Creamos el objeto Parser y pasamos los tokens del lexico
		Parser parser = new Parser(scanner);
		parser.Parse();
		System.out.println(parser.errors.count + " errores detectados");
	}
}


