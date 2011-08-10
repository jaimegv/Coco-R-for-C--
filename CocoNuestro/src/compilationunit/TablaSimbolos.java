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
		return 0;
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