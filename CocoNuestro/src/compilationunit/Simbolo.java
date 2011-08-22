package compilationunit;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Queue;

public class Simbolo {
	
	// Declaracion de constantes para tipos (type)
	final int undef=0, entera=1, bool=2, cadena=3, vacio=4, identificador=5, vector=6;
	// Declaración de constantes de tipo de scopes (kind)
	final int var=0, funcion=1, clase=2, metodo=3, parametro=4;
	//Declaración de visibilidad
	final int privado=0, publico=1;
	
	
	private String nombre;	// Nombre del objeto
	private int type;		// tipo del objeto
	private int kind;		// varible, funcion, metodo...
	private boolean inicializada;
	private boolean es_vector; // Indica si la variable es de tipo vector
	private int tamano;   	//Tamaño de la variable
	private Vector valor;   //Aquí se guardará el valor de la variable. En caso de ser un vector, sus valores.
	private int line;		// línea dónde se encontró el símbolo
	private int column;		// columna dónde se encontró el símbolo
	private int nparametros; // En caso de ser una función, indica cuántos parámetros tiene
							// Si no fuera una función siempre estará a 0.
	private Vector parametros; //Vector en el que se almacenarán los nombres de los parámetros
							   //Los parametros se almacenarán como nuevos símbolos
	private int visible;    // Público o privado.
	private int tiporetorno; //Si es una función esto indicará el tipo de valor devuelto
	private Simbolo clase_perteneciente; //Si es una variable, indica la clase a la que pertenece y type será "identificador"
										 //Si es un método, indica la clase perteneciente.
	
	private Simbolo clase_devuelta;		//Si es una función, indica la clase del objeto devuelto, y tiporetorno será igual a identificador.
	private TablaSimbolos ambito_asociado; //Si es una función o un método, indicará cuál es el ámbito asociado
	


public Simbolo(String nombre, int type, int kind){
	   this.nombre = nombre;
	   System.out.println("Nuevo simbolo con nombre: " + this.nombre);
	   this.type = type;
	   this.kind = kind;
	   this.nparametros = 0;
	   this.es_vector = false;
	   this.tamano = 1;
	   this.valor = new Vector();
	   this.parametros = new Vector();
	   this.clase_perteneciente = null;
	   this.clase_devuelta = null;
	   this.ambito_asociado = null;

	}

//*****************MODIFICADORES*******************///
public void SetNombre(String nombre){
		//System.out.println("Cambiando nombre simbolo a:"+nombre);
		this.nombre = new String(nombre);
		//this.nombre = nombre;
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

public void SetToVector(int tamano)
	{
	this.es_vector = true;
	this.type = vector;
	this.tamano = tamano;
	}
/*
public void SetToVector()
	{
	this.type = vector;
	es_vector = true;
	tamano = 1;
	}
*/
public void SetValor(Object valor)
{
	this.valor.insertElementAt(valor, 0);
	this.inicializada = true;
}

public void SetClase(Simbolo simbolo)
{
	clase_perteneciente = simbolo;
}

public void SetAmbitoAsociado(TablaSimbolos ambito_asociado)
	{
	this.ambito_asociado = ambito_asociado;
	}

public void SetClaseDevuelta (Simbolo simbolo_clase)
	{
	this.clase_devuelta = simbolo_clase;
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

public boolean Es_Vector ()
	{
	return this.es_vector;
	}

/*public int GetTamano()
	{
	return this.tamano;
	}
*/
public Object GetValor()
	{
	 if (this.inicializada)
		return this.valor.elementAt(0);
	else
		{
		System.out.println("Se ha intentado leer una variable no inicializada");
		return null;
		}
		
	}

public Object GetValor(int posicion)
	{
	if (posicion >= tamano)
		return null;
	else
		return this.valor.elementAt(posicion);

	}

public Vector GetParametros()
	{
	return this.parametros;
	}

public Simbolo GetParametros(int pos)
	{
	return (Simbolo)this.parametros.elementAt(pos);
	}

public Vector GetPosicionesVector()
	{
	return this.valor;
	}

public Simbolo GetClase()
	{
	return this.clase_perteneciente;
	}

public TablaSimbolos GetAmbitoAsociado()
{
return this.ambito_asociado;
}

public Simbolo GetClaseDevuelta ()
	{
	return this.clase_devuelta;
	}
//************************************************//////

public void AnadirParametro (Simbolo simbolo)
	{
	this.nparametros = this.nparametros + 1;
	this.parametros.addElement(simbolo);
	}

public int Actualiza_Tamano ()
	{
	if ((this.kind == var) || (this.kind == parametro))
		{
		if ((type == entera) || (type == bool))
			{
			this.tamano = 1;
			return tamano;
			}
		else if (type == vector)
			return tamano;
		else if (type == identificador)
			{
			this.tamano = this.clase_perteneciente.GetAmbitoAsociado().GetDesplazamiento();
			return this.tamano;
			}
		else if (type == cadena)
			{
			this.tamano = 20;
			return this.tamano;
			}
		else
			{
			this.tamano = 0;
			return this.tamano;
			}
		}
	else
		{
		this.tamano = 0;
		return this.tamano;
		}
	}

}