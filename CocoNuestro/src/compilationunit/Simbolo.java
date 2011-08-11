package compilationunit;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Queue;

public class Simbolo {
	
	// Declaracion de constantes para tipos
	final int undef=0, entera=1, bool=2, cadena=3, vacio=4;
	// Declaración de constantes de tipo de scopes
	final int var=0, funcion=1, clase=2, metodo=3;
	//Declaración de visibilidad
	final int privado=0, publico=1;
	
	
	private String nombre;	// Nombre del objeto
	private int type;		// tipo del objeto
	private int kind;		// varible, funcion, metodo...
	private int line;		// línea dónde se encontró el símbolo
	private int column;		// columna dónde se encontró el símbolo
	private int nparametros; // En caso de ser una función, indica cuántos parámetros tiene
							// Si no fuera una función siempre estará a 0.
	private Vector parametros; //Vector en el que se almacenarán los tipos de los parámetros
	private int visible;    // Público o privado.
	private int tiporetorno; //Si es una función esto indicará el tipo de valor devuelto
	


public Simbolo(String nombre, int type, int kind){
	   this.nombre = nombre;
	   System.out.println("Nuevo simbolo con nombre: " + this.nombre);
	   this.type = type;
	   this.kind = kind;
	   this.nparametros = 0;
	   if (this.kind == funcion)
	   		{
		    parametros = new Vector();
	   		}
	}

//*****************MODIFICADORES*******************///
public void SetNombre(String nombre){
	   this.nombre = nombre;
	}

public void SetType(int type){
	   this.type = type;
	}

public void SetKind(int kind){
	   this.kind = kind;
	}

public void SetLine(int line){
	   this.line = line;
	}

public void SetColumn(int column){
	   this.column = column;
	}

public void SetNParametros(int nparametros){
	   this.nparametros = nparametros;
	}

public void SetVisibilidad(int visible){
	   this.visible = visible;
	}

public void SetTipoRetorno(int tiporetorno){
	   this.tiporetorno = tiporetorno;
	}

//*****************MÉTODOS DE ACCESO*******************///
public String GetNombre(){
	   return this.nombre;
	}

public int GetType(){
	   return this.type;
	}

public int GetKind(){
	   return this.kind;
	}

public int GetLine(){
	   return this.line;
	}

public int GetColumn(){
	   return this.column;
	}

public int GetNParametros(){
	   return this.nparametros;
	}

public int GetVisibilidad(){
	   return this.visible;
	}

public int GetTipoRetorno()
	{
	return this.tiporetorno;
	}

public int GetTipoParametro (int numero)  //numero indica el parametro del que quieres saber su tipo
	{
	return (Integer) this.parametros.elementAt(numero);
	}


//************************************************//////

public void AnadirParametro (int tipo)
	{
	this.nparametros = this.nparametros + 1;
	this.parametros.addElement(tipo);
	}




}