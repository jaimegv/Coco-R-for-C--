// Definimos el paquete al que pertenece nuestro analizador
header {
	package analizador;
}

// ANALISIS SEMÁNTICO -- Instalación de símbolos

class CompTreeParser extends TreeParser;
 
options {
	importVocab=CompParserVocab;
	buildAST = false; // Por defecto no construimos un AST nuevo
}


/// Permite recorrer el nodo principal, llamado "programa"
programa : #(PROGRAMA main)
 		;
 		
// aqui se entra al main->raiz del arbol
main: #(DEC_MAIN VOID MAIN^ PARENT_AB! INT IDENT PARENT_CE!
		LLAVE_AB!
			RETURN PUNTO_COMA
		LLAVE_CE!)
		;

 
 