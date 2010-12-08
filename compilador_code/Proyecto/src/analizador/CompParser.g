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
	// traigo los token generados por el lexico
	importVocab = CompLexerVocab ;
	exportVocab = CompParserVocab ;
}

tokens {
	/* tokens */
	// En el caso del analizador sintactico los usaremos para
	// saber de que nos estan hablando (desde el lexico)
	
	// DECLARACION DE VARS
	ENTERO ;
	
	EXPSUMA ;
	ALGO ;
}

/* Reglas de generación*/


// DECLARACIONES DE VARIABLES
// sin asignacion.
entero : INT int_var ;
int_var : IDENT asig ;
asig : OP_ASIG numero
		| OP_ASIG asig_iden
		| COMA int_var
		| PUNTO_COMA ;
		exception catch [RecognitionException ex]
				{	 }
asig_iden : IDENT COMA int_var 
		| IDENT PUNTO_COMA ;
numero : LIT_ENTERO_DECIMAL COMA int_var
		| LIT_ENTERO_OCTAL COMA int_var
		| LIT_ENTERO_DECIMAL PUNTO_COMA
		| LIT_ENTERO_OCTAL PUNTO_COMA ;



// ejemplo
// esto es una suma
expsuma : LIT_ENTERO_DECIMAL algo;

algo: OP_MAS LIT_ENTERO_DECIMAL;
