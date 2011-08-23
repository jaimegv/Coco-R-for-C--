package compilationunit;

import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Queue;
/*import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;
*/

public class TablaSimbolos {
	
	// Declaracion de constantes para tipos
	final int undef=0, entera=1, bool=2, cadena=3, vacio=4, identificador=5;
	// Declaración de constantes de tipo de scopes
	final int var=0, funcion=1, clase=2, metodo=3;
	//Declaración de visibilidad
	final int privado=0, publico=1;
	
	private Simbolo simbolo_asociado;
	private TablaSimbolos ambito_padre;
	private Vector tabla;
	private int desplazamiento;
	
	public TablaSimbolos()
		{
		this.simbolo_asociado = null;
		this.tabla = new Vector();
		this.ambito_padre = null;
		}
	
	public TablaSimbolos(TablaSimbolos ambito_padre, Simbolo simbolo_asociado)
		{
		this.simbolo_asociado = simbolo_asociado;
		this.tabla = new Vector();
		this.ambito_padre = ambito_padre;
		}
	
	public int InsertarSimbolo (Simbolo simbolo)
		{
		//Inserta un nuevo simbolo en la tabla
		//Si se encuentra un simbolo con el mismo nombre devuelve -1 y e.o.c. 0
		Simbolo simbolito;
		simbolito = null;
		for(int i=0; i< tabla.size(); i++)
			{
			
			simbolito = (Simbolo) tabla.elementAt(i);			
            if ((this.tabla.elementAt(i) instanceof Simbolo) && 
            		(simbolo.GetNombre() == simbolito.GetNombre()))
            	{
            		return -1;
            	}
			}
		tabla.addElement(simbolo);
		System.out.println(simbolo.GetNombre());
		this.desplazamiento = this.desplazamiento + simbolo.Actualiza_Tamano();
		return 0;
		}
	
	public boolean Esta(String nombre)
		//Devuelve cierto si hay un símbolo almacenado con el mismo nombre en este objeto
		{
		Simbolo simbolito = null;
		for(int i=0; i< this.tabla.size(); i++)
			{
			simbolito = (Simbolo) this.tabla.elementAt(i);
			//System.out.println("Un simbolo a comparar con "+nombre+" es "+simbolito.GetNombre()+" kind:"+simbolito.GetKind()+" type:"+simbolito.GetType());
			if ((this.tabla.elementAt(i) instanceof Simbolo) && (nombre.contentEquals(simbolito.GetNombre())))
				{
				return true;
				}
		
			}
		return false;
		}
	
	public Simbolo GetSimbolo (String nombre)
		//Devuelve el simbolo almacenado con este nombre
		//Si el simbolo no esta se devuelve null (es preferible preguntar antes si está)
		{
		Simbolo simbolito;
		simbolito = null;
		
	
		for(int i=0; i< tabla.size(); i++)
			{
			simbolito = (Simbolo) tabla.elementAt(i);
			if ((this.tabla.elementAt(i) instanceof Simbolo) && 
            		(nombre.contentEquals(simbolito.GetNombre())))
				{
				return simbolito;
				}
			
			}
		return null;
		}
	
	public TablaSimbolos Ambito_Padre ()
		{
		return this.ambito_padre;
		}
	
	public int GetDesplazamiento()
		{
		return this.desplazamiento;
		}
	
	public void SetDesplazamiento(int desplazamiento)
		{
		this.desplazamiento = desplazamiento;
		}
	
	public int ActualizarDesplazamiento ()
		{
		this.desplazamiento = 0;
		Simbolo simbolito = null;
		for(int i=0; i< tabla.size(); i++)
			{
			simbolito = (Simbolo) tabla.elementAt(i);
			if (simbolito.Actualiza_Tamano() != 0)
				simbolito.SetDesplazamiento(desplazamiento);
			System.out.println("El tamano del simbolo " + simbolito.GetNombre() + " es: " + simbolito.Actualiza_Tamano() + " y su desplazamiento es " + simbolito.GetDesplazamiento());
			this.desplazamiento = this.desplazamiento + simbolito.Actualiza_Tamano();
			}
		return this.desplazamiento;
		}

	}
