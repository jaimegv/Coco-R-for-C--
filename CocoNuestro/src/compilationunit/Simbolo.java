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
	private String etiqueta; //Esto servira, para, en caso de ser una funcion o un metodo, identificar la etiqueta a donde saltara
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
	
	private int desplazamiento; //Esto indicara que posicion de la tabla de simbolos ocupa.
								//Sera necesario para localizar las variables locales en la GCF
	private Vector atributos;   //Si es una clase, tendra dentro los atributos asociados
	private Simbolo objeto_perteneciente;
	


public Simbolo(String nombre, int type, int kind){
	   this.nombre = nombre;
	   //Parser.salidadep("Nuevo simbolo con nombre: " + this.nombre);
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
	   this.desplazamiento = -1;
	   this.objeto_perteneciente = null;

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
	   Parser.salidadep("Situando la visibilidad del atributo " + this.nombre + " a ");
	   Parser.salidadep(visible);
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
	if ((this.GetType() == identificador) && (this.GetKind() == var))
		{
		Parser.salidadep("Creando los simbolos atributos:");
		Simbolo simbolito;
		this.atributos = new Vector();
		for(int i=0; i< this.clase_perteneciente.GetAmbitoAsociado().GetVectorSimbolos().size(); i++)
			{
			simbolito = (Simbolo) this.clase_perteneciente.GetAmbitoAsociado().GetVectorSimbolos().elementAt(i);
			if (simbolito.GetKind() == var)
				{
				Simbolo simbolo_atributo = new Simbolo(this.nombre + "."+ simbolito.GetNombre(),simbolito.GetType(),var);
				simbolo_atributo.SetVisibilidad(simbolito.GetVisibilidad());
				simbolo_atributo.SetDesplazamiento(simbolito.GetDesplazamiento());
				Parser.salidadep(simbolo_atributo.GetNombre() + "desplazamiento: " + simbolo_atributo.GetDesplazamiento());
				simbolo_atributo.objeto_perteneciente = this;
				this.atributos.addElement(simbolo_atributo);
				}
			}
		}
}

public void SetAmbitoAsociado(TablaSimbolos ambito_asociado)
	{
	this.ambito_asociado = ambito_asociado;
	}

public void SetClaseDevuelta (Simbolo simbolo_clase)
	{
	this.clase_devuelta = simbolo_clase;
	}

public void SetDesplazamiento (int desplazamiento)
	{
	this.desplazamiento = desplazamiento;
	}

public void SetEtiqueta (String etiqueta)
	{
	this.etiqueta = new String(etiqueta);
	}
//*****************MÉTODOS DE ACCESO*******************///
public String GetNombre(){
	   if (this != null)
		   return this.nombre;
	   else
		   return null;
	}

public int GetType(){
		if (this != null)
			return this.type;
		else
			return -1;
	}

public int GetKind(){
	if (this != null)
		return this.kind;
	else
		return -1;
	}

public int GetLine(){
	if (this != null)
		return this.line;
	else
		return -1;
	}

public int GetColumn(){
	if (this != null)
		return this.column;
	else
		return -1;
	}

public int GetNParametros(){
	if (this != null)
		return this.nparametros;
	else
		return -1;
	}

public int GetVisibilidad(){
	if (this != null)
		return this.visible;
	else
		return -1;
	}

public int GetTipoRetorno()
	{
	if (this != null)
		return this.tiporetorno;
	else
		return -1;
	}

public int GetTipoParametro (int numero)  //numero indica el parametro del que quieres saber su tipo
	{
	return (Integer) this.parametros.elementAt(numero);
	}

public boolean Es_Vector ()
	{
	if (this != null)
		return this.es_vector;
	else
		return false;
	}

public Object GetValor()
	{
	 if (this.inicializada)
		return this.valor.elementAt(0);
	else
		{
		Parser.salidadep("Se ha intentado leer una variable no inicializada");
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
	if (this != null)
		return this.parametros;
	else
		return null;
	
	}

public Simbolo GetParametros(int pos)
	{
	if (this != null)
		try
			{
			return (Simbolo)this.parametros.elementAt(pos);
			}
		catch (Exception e)
			{
			Parser.salidadep("Consulta de paramtro inexistente!!!");
			return null;
			}
		
	else
		return null;
	}

public Vector GetPosicionesVector()
	{
	return this.valor;
	}

public Simbolo GetClase()
	{
	if (this != null)
		return this.clase_perteneciente;
	else
		return null;
	
	}

public TablaSimbolos GetAmbitoAsociado()
{
	if (this != null)
		return this.ambito_asociado;
	else
		return null;

}

public Simbolo GetClaseDevuelta ()
	{
	if (this != null)
		return this.clase_devuelta;
	else
		return null;
	
	}

public int GetDesplazamiento ()
	{
	if (this != null)
		return this.desplazamiento;
	else
		return -1;
	}

public String GetEtiqueta ()
	{
	return this.etiqueta;
	}

public Simbolo GetObjetoPerteneciente()
	{
	return this.objeto_perteneciente;
	}

public Simbolo GetAtributo(String nombre)
	{
	if (this.GetType() != identificador)
		return null;
	else
		{
		Simbolo simbolito;
		for(int i=0; i< this.atributos.size(); i++)
			{
			simbolito = (Simbolo) this.atributos.elementAt(i);
			Parser.salidadep(simbolito.GetNombre());
			if (simbolito.GetNombre().contentEquals(nombre))
				return simbolito;
			}
		Parser.salidadep("NO ENCONTRADO");
		return null;
		}
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
			if (this.clase_perteneciente != null)
				{
				this.tamano = this.clase_perteneciente.GetAmbitoAsociado().GetDesplazamiento();
				return this.tamano;
				}
			else
				{
				return this.tamano;
				}
			}
		else if (type == cadena)
			{
			this.tamano = 1;
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