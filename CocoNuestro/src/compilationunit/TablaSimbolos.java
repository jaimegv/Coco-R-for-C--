package compilationunit;

/*import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;
*/

public class TablaSimbolos {
	
	// Declaracion de constantes para tipos
	final int undef=0, entera=1, bool=2, cadena=3;
	// Declaración de constantes de tipo de scopes
	final int var=0, funcion=1, clase=2, metodo=3;
	
	public Simbolo Objeto;
	public Simbolo topScope;	// Ambito Actual
	
	Parser parser;
	
	// Constructor
	public void TablaSimbolos (Parser parser) {
		this.parser=parser;
		Objeto = new Simbolo();
		Objeto.nombre  =  "undef"; 
		Objeto.type = undef;	// tipo del objeto
		Objeto.kind=var;		// tipo de inicio
	}
	
	// Abrir un nuevo ambito para hacerlo el actual
	public void OpenAmbito() {
		Objeto = new Simbolo();
		Objeto.nombre  =  ""; 
		Objeto.type = undef;	// tipo del objeto
		Objeto.kind=var;		//
	}

	// Crear un nuevo objteo en el actual SCOPE-ambito
	public void NuevoAmbito(String nombre, int kind, int type) {
		Objeto = new Simbolo();
		Objeto.nombre  =  ""; 
		Objeto.type = undef;	// tipo del objeto
		Objeto.kind=var;		//
	}
	
	
	
    public void DestruirTSG () {
    	System.out.println("Hola mundo! esto es la Tabla de simbolo");
    }
}
