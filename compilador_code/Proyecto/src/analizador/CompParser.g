// Definimos el paquete al que pertenece nuestro analizador
header {
	package analizador;
}

class CompParser extends Parser ;

options {
	/* opciones del analizador*/
	// LookAhead.
	// cuantos caracteres cogerá para concluir
	k = 2;
	// Activando la construccion del build
	buildAST = true;
	
	importVocab = CompLexerVocab ;
	exportVocab = CompParserVocab ;
	
	
}

tokens {
	/* tokens */
	// En el caso del analizador sintactico los usaremos para
	// saber de que nos estan hablando (desde el lexico)
	
	
	EXPSUMA ;
	ALGO ;
}

/* Reglas de generación*/

// Zona de reglas


// ejemplo
// esto es una suma
expsuma : LIT_ENTERO_DECIMAL algo;

algo: OP_MAS LIT_ENTERO_DECIMAL;
