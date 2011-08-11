//Nombre del paquete
package compilationunit;
import java.io.Console;


public class Main {
	public static void main(String[] arg) {
		int error;
		
		Tablas tablon = new Tablas();
		
		TablaSimbolos tablaprueba = tablon.GetAmbitoActual();
		
		Simbolo simboloprueba = new Simbolo("hola",1,1);
		Simbolo simboloprueba2 = new Simbolo("caracola",1,1);
		
		error = tablon.InsertarEnActual(simboloprueba);
		tablon.NuevoAmbito();
		error = tablon.InsertarEnActual(simboloprueba2);
		
		//En teoria simboloprueba está en el ambito global y
		//simboloprueba2 está en el actual. Probemos...
		
		System.out.println(error);
//		tablon.CerrarAmbito();
		
		if (tablon.EstaRecur("caracola"))
			{
			simboloprueba2 = tablon.GetSimboloRecur("hola");
			System.out.println(simboloprueba2.GetNombre());
			}
		else
			System.out.println("No esta!");
		
		
		
		
		
		/*
		Scanner scanner = new Scanner(arg[0]);
		Parser parser = new Parser(scanner);
		parser.Parse();
		System.out.println(parser.errors.count + " errores detectados");*/
	}
}


