package compilationunit;
//import java.util.Vector;

public class Tablas {
	
	private TablaSimbolos Ambito_Global;
	private TablaSimbolos Ambito_Actual;

	// Constructor
	public Tablas()	{
		Ambito_Global = new TablaSimbolos();
		Ambito_Actual = Ambito_Global;
	}
	
	// Abrir un nuevo ámbito
	public void NuevoAmbito(TablaSimbolos Actual) {
		// Nuevo ambito a desarrollar que será el actual
		// su padre es el actual ambito
		TablaSimbolos Ambito = new TablaSimbolos(Ambito_Actual);
		Ambito_Actual = Ambito;
	}
	
}
