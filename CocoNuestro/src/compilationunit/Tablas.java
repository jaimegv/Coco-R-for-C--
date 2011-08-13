package compilationunit;
//import java.util.Vector;

public class Tablas {
	
	private TablaSimbolos ambito_global;
	private TablaSimbolos ambito_actual;

	// Constructor
	public Tablas()	
		{
		System.out.println("Ambito global creado");
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
	
	public void NuevoAmbito() 
		{
		// Nuevo ambito a desarrollar que será el actual
		// su padre es el actual ambito
		TablaSimbolos ambito_nuevo = new TablaSimbolos(ambito_actual);
		ambito_actual = ambito_nuevo;
		System.out.println("Nuevo ambito creado");
		}
	
	public void CerrarAmbito() 
	//¡Cerrar un ambito no es destruirlo!
		{
		TablaSimbolos ambitopadre = this.ambito_actual.Ambito_Padre();
		this.ambito_actual = ambitopadre;
		}
	
	public int InsertarEnActual (Simbolo simbolo)
		{
		if (this.ambito_actual.InsertarSimbolo(simbolo) == 0)
			{
			System.out.println("Exito insertando simbolo en el ambito actual");
			return 0;
			}
		else
			{
			System.out.println("JUR: El simbolo ya estaba definido en este ambito");
			return -1;
			}
		}
	
	public int InsertarEnAmbitoPadre (Simbolo simbolo)
	{
		TablaSimbolos ambito_padre = this.ambito_actual.Ambito_Padre();
		if (ambito_padre.InsertarSimbolo(simbolo) == 0)
			{
			System.out.println("Exito insertando simbolo en el ambito padre");
			return 0;
			}
		else
			{
			System.out.println("JUR: El simbolo ya estaba definido en este ambito");
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
	else
		return null;
	
	
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
			System.out.println("Se ha encontrado en el ámbito global");
			return true;
			}
		else
			{
			System.out.println("No se ha encontrado el simbolo en la jerarquia de ambitos");
			return false;
			}
		
		}

	public boolean EstaEnActual (String nombre)
		{
		return this.ambito_actual.Esta(nombre);
		}
	
}
