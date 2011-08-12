//Nombre del paquete
package compilationunit;
import java.io.Console;


public class Main {
	public static void main(String[] arg) {
		int error;
		
/*		Tablas tabla = new Tablas();
		Simbolo simbolo = new Simbolo("hola", 0, 0);
		
		tabla.InsertarEnActual(simbolo);
//		Simbolo simbolo_nuevo = new Simbolo("hola",0,0);
		if (tabla.EstaEnActual("hola"))
			System.out.println("El simbolo ya estaba");
		else
			System.out.println("uyuyuy");*/
		
		Scanner scanner = new Scanner(arg[0]);
		Parser parser = new Parser(scanner);
		parser.Parse();
		System.out.println(parser.errors.count + " errores detectados");
	}
}


