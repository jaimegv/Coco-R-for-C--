package compilationunit;
//import java.util.Vector;

public class Tablas {
	
	private TablaSimbolos ambito_global;
	private TablaSimbolos ambito_actual;

	// Constructor
	public Tablas()	
		{
		ambito_global = new TablaSimbolos();
		ambito_actual = ambito_global;
		}
	public void Destruir()	
		{
		ambito_global = null;
		ambito_actual = null;
		}
	
	public TablaSimbolos GetAmbitoActual ()
		{
		return ambito_actual;
		}
	
	public TablaSimbolos GetAmbitoGlobal ()
	{
	return ambito_global;
	}
	
	public void NuevoAmbito(Simbolo simbolo) 
		{
		// Nuevo ambito a desarrollar que será el actual
		// su padre es el actual ambito
		TablaSimbolos ambito_nuevo = new TablaSimbolos(ambito_actual, simbolo);	// pasamos como arg la tabla padre
		simbolo.SetAmbitoAsociado(ambito_nuevo);
		ambito_actual = ambito_nuevo;
		}
	
	public void CerrarAmbito() 
	//¡Cerrar un ambito no es destruirlo!
		{
		this.ambito_actual.ActualizarDesplazamiento();
		TablaSimbolos ambitopadre = this.ambito_actual.Ambito_Padre();
		this.ambito_actual = ambitopadre;
		}
	
	public int InsertarEnActual (Simbolo simbolo)
		{
		if (this.ambito_actual.InsertarSimbolo(simbolo) == 0)
			{
			return 0;
			}
		else
			{
			return -1;
			}
		}
	
	public int InsertarEnAmbitoPadre (Simbolo simbolo)
	{
		TablaSimbolos ambito_padre = this.ambito_actual.Ambito_Padre();
		if (ambito_padre.InsertarSimbolo(simbolo) == 0)
			{
			return 0;
			}
		else
			{
			return -1;
			}
		}
	
	public Simbolo GetSimboloRecur (String nombre)
		//Devuelve el primer simbolo que se encuentre con ese nombre en la jerarquia de ambitos
		{
		TablaSimbolos currentbusqueda = this.ambito_actual;
		
		while (currentbusqueda.Ambito_Padre() != null)
		{
		if (currentbusqueda.Esta(nombre))
			return currentbusqueda.GetSimbolo(nombre);
		else
			currentbusqueda = currentbusqueda.Ambito_Padre();
		}
			if (this.ambito_global.Esta(nombre))
				return this.ambito_global.GetSimbolo(nombre);
			else {
//				System.out.println("Cuidado! devolviendo Null.object");
				return null;
			}
		}
	
	public boolean EstaRecur (String nombre)
	//Esta funciona devuelve cierto cuando se encuentra un simbolo con el non el mismo nombre
	//dentro del ámbito o actual o cualquiera de sus padres.
	
		{
		TablaSimbolos currentbusqueda = this.ambito_actual;
		
		while (currentbusqueda.Ambito_Padre() != null)
			{
			if (currentbusqueda.Esta(nombre))
				return true;
			else
				currentbusqueda = currentbusqueda.Ambito_Padre();
			}
		if (this.ambito_global.Esta(nombre))
			{
			return true;
			}
		else
			{
			return false;
			}
		
		}

	public boolean EstaEnActual (String nombre)
		{
		return this.ambito_actual.Esta(nombre);
		}
	
	public Simbolo GetSimboloActual (String nombre)
		{
		return this.ambito_actual.GetSimbolo(nombre);
		}
	
	public void AbrirAmbito(TablaSimbolos ambito_a_abrir)
		{
		ambito_actual = ambito_a_abrir;
		}
}
