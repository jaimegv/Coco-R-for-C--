package analizador;
import java.io.*;
//import org.antlr.runtime.CommonTokenStream;
import antlr.collections.AST;
import antlr.ANTLRException;
import antlr.Token;
import antlr.TokenStreamException;

public class Compilador {
	
	public static void main (String args[]) throws TokenStreamException {
		try {
	        System.out.println("Empieza a dar tokens:");
	        String ext =args[0].substring(args[0].indexOf("."));
	        if(ext.equalsIgnoreCase(".cpp")) {
				FileInputStream fis = new FileInputStream(args[0]);
				analizador Analizador = new analizador(fis);
				Token token = Analizador.nextToken();
				while(token.getType() != Token.EOF_TYPE) {
					System.out.println(token);
					token = Analizador.nextToken();
				}
	        } else {
				System.err.println("No es un fichero con extensión correcta .cpp");
	        }
		}catch(FileNotFoundException fnfe) {
			System.err.println("No se encontró el fichero");
		}
	}
}
