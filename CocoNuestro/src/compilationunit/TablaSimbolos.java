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
	final int undef=0, entera=1, bool=2, cadena=3, vacio=4;
	// Declaración de constantes de tipo de scopes
	final int var=0, funcion=1, clase=2, metodo=3;
	//Declaración de visibilidad
	final int privado=0, publico=1;
	
	private TablaSimbolos ambito_padre;
	private Vector tabla;
	
	public TablaSimbolos()
		{
		this.tabla = new Vector();
		this.ambito_padre = null;
		}
	
	public TablaSimbolos(TablaSimbolos ambito_padre)
		{
		this.tabla = new Vector();
		this.ambito_padre = ambito_padre;
		}
	
	public int InsertarSimbolo (Simbolo simbolo)
		{
		//Inserta un nuevo simbolo en la tabla
		//Si se encuentra un simbolo con el mismo nombre devuelve -1 y e.o.c.
		Simbolo simbolito;
		simbolito = null;
		for(int i=0; i< tabla.size(); i++)
			{
			
			simbolito = (Simbolo) tabla.elementAt(i);			
            if ((this.tabla.elementAt(i) instanceof Simbolo) && 
            		(simbolo.GetNombre() == simbolito.GetNombre()))
            	{
					System.out.println("YA existe este simbolo:"+simbolo.GetNombre());
            		return -1;
            	}
			}
		tabla.addElement(simbolo);
		return 0;
		}
	
	public boolean Esta(String nombre)
		//Devuelve cierto si hay un símbolo almacenado con el mismo nombre en este objeto
		{
		Simbolo simbolito = null;
		for(int i=0; i< this.tabla.size(); i++)
			{
			simbolito = (Simbolo) this.tabla.elementAt(i);
			//System.out.println("Un simbolo a comparar con "+nombre+" es "+simbolito.GetNombre()+" kind:"+simbolito.GetKind());
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

	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	
	public Simbolo Objeto;
	public Simbolo topScope;	// Ambito Actual
	
	Parser parser;
	
	// Constructor
	public void TablaSimbolos (Parser parser) {
		this.parser=parser;
		Objeto = new Simbolo("undef", 0 , 0);

	}
	
	// Abrir un nuevo ambito para hacerlo el actual
	public void AbrirAmbito() {
		Objeto = new Simbolo("undef", undef , var);
	//
	}

	// Crear un nuevo objteo en el actual SCOPE-ambito
	public void NuevoAmbito(String nombre, int kind, int type) {
		Objeto = new Simbolo("undef", undef , var);

	}
	
	
	
    public void DestruirTSG () {
    	System.out.println("Hola mundo! esto es la Tabla de simbolo");
    }
}
*/