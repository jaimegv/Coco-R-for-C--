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
	
	public Simbolo undefObj;
	
	
	public void SymbolTable(Parser parser) {
		undefObj = new Simbolo();
		undefObj.nombre  =  "undef"; 
		undefObj.type = undef; 
	}
	
	
    public void DestruirTSG () {
    	System.out.println("Hola mundo! esto es la Tabla de simbolo");
    }
}
