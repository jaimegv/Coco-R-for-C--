package compilationunit;
import java.util.Vector;

public class Tablas {
	
	private TablaSimbolos Ambito_Global;
	private TablaSimbolos Ambito_Actual;

	
	public Tablas()
	{
		Ambito_Global = New TablaSimbolos();
		Ambito_Actual = Ambito_Global;
	}
}
