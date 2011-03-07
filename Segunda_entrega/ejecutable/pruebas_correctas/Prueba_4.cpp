// Prueba Correcta

// main
bool main (int n, int m) 
{
	/* Declaracion de variables
	 - algunas inicializadas - */
	bool variable = true;

	// Condicional Simple
	variable =(n < m) ? true : false;	 
	
	// Condicional Complejo
	if variable	// cierto
		variable = false;
	else
		varible = true;

	// Devolvemos le valor calculado	
	return variable;
}
