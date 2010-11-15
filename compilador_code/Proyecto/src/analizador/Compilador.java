package analizador;
import java.io.*;
//import org.antlr.runtime.CommonTokenStream;
import antlr.collections.AST;
import antlr.ANTLRException;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStreamException;

public class Compilador {
	
	public static void main (String args[])  {
		try {
	        String ext =args[0].substring(args[0].indexOf("."));
	        if(ext.equalsIgnoreCase(".cpp")) {
		        System.out.println("Escribiendo tokens fichero:");
				FileInputStream fis = new FileInputStream(args[0]);
				analizador Analizador = new analizador(fis);
				try {
					Token token = Analizador.nextToken();
					while(token.getType() != Token.EOF_TYPE) {
						System.out.println(token);
						token = Analizador.nextToken();
					}
				} catch (ANTLRException Ex) {				
						System.err.println("Error en token desconocido.");
						System.err.println(Ex.getMessage());
				}
	        } else {
				System.err.println("No es un fichero con extensión correcta .cpp");
	        }
		} catch(FileNotFoundException fnfe) {
			System.err.println("No se encontró el fichero especificado.");
		}
		

	}
}
