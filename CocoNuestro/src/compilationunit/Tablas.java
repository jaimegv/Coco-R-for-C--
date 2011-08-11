package compilationunit;
//import java.util.Vector;

public class Tablas {
	
	private TablaSimbolos ambito_global;
	private TablaSimbolos ambito_actual;

	// Constructor
	public Tablas()	{
		ambito_global = new TablaSimbolos();
		ambito_actual = ambito_global;
	}
	
	public TablaSimbolos GetAmbitoActual ()
		{
		return ambito_actual;
		}
	
	public TablaSimbolos GetAmbitoGlobal ()
	{
	return ambito_global;
	}
	
	// Abrir un nuevo ámbito
	public void NuevoAmbito() {
		// Nuevo ambito a desarrollar que será el actual
		// su padre es el actual ambito
		TablaSimbolos ambito_nuevo = new TablaSimbolos(ambito_actual);
		ambito_actual = ambito_nuevo;
	}
	
	public void CerrarAmbito()
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
	
	public Simbolo GetSimboloRecur (String nombre)
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
	
}
