//Nombre del paquete
package compilationunit;
import java.io.Console;


public class Main {
	public static void main(String[] arg) {
		int error;
		
		TablaSimbolos tablaprueba = new TablaSimbolos();
		Simbolo simboloprueba = new Simbolo("hola",1,1);
		
		error = tablaprueba.InsertarSimbolo(simboloprueba);
		System.out.println(error);
		
		
		Simbolo simboloprueba2;
		
		simboloprueba2 = tablaprueba.GetSimbolo("hola");
		if (simboloprueba2 == null)
			System.out.println("No se ha encontrado");
		else
			System.out.println(simboloprueba2.GetNombre());
		
		
		/*
		Scanner scanner = new Scanner(arg[0]);
		Parser parser = new Parser(scanner);
		parser.Parse();
		System.out.println(parser.errors.count + " errores detectados");*/
	}
}


