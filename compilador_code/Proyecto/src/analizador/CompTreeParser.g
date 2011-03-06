// Definimos el paquete al que pertenece nuestro analizador
header {
	package analizador;
}

// ANALISIS SEMÁNTICO -- Instalación de símbolos

class CompTreeParser extends TreeParser;
 
options {
	k=1;
	importVocab=CompParserVocab;
	buildAST = false; // Por defecto no construimos un AST nuevo
}


/// Permite recorrer el nodo principal, llamado "programa"
programa : #(PROGRAMA VARSGLOBAL 
				(SUBPROGRAMA
					| CLASE
						| DEC_METODO)* DEC_MAIN)
 		;

 