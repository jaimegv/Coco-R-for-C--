//Prueba consistente en la modificacion de una variable global

int variable_global;

void restar_global ()
	{
	variable_global = variable_global - 1;
	}

void main()
	{
	variable_global = 5;
	cout << "La variable vale " << variable_global;
	restar_global();
	cout << "Ahora la variable vale " << variable_global;
	
	}
