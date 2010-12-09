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
	DEC_ENTERO ;
	
	FUNCION ;
	
}

/* Reglas de generación*/

// FUNCIONES C++
funcion : tipo IDENT PARENT_AB! arg_entrada PARENT_CE!
			LLAVE_AB!
				// Declaraciones de variables
				
				// declaracion de ops
			
			LLAVE_CE!;

// Argumentos de entrada de las funciones
// por ejemplo: (int s, char *s, int w)
arg_entrada : tipo_arg arg_sig_entrada
			| /* nada */ ;
			
arg_sig_entrada : COMA tipo_arg arg_sig_entrada 
			| /* nada */ ;

// tipos genericos que reconoce nuestro lenguaje
tipo : INT 
	| BOOL
	| VOID
	| CHAR
	| CHAR OP_PRODUCTO;
	
// Tipos de argumentos
tipo_arg : INT IDENT cadena	// int s ó int e[20]
	| BOOL IDENT
	| CHAR IDENT
	| CHAR OP_PRODUCTO IDENT;

cadena : vector
	| /* NADA (para el -> int IDENT*/;
	
vector : CORCHETE_AB LIT_ENTERO_DECIMAL CORCHETE_CE; // int e[20]

// DECLARACIONES DE VARIABLES
// sin asignacion.
dec_entero : INT int_var ;
int_var : IDENT asig 
		| IDENT vector dec_entero_cad;
dec_entero_cad : COMA int_var
		| OP_ASIG IDENT dec_entero_cad_fin
		| PUNTO_COMA;
dec_entero_cad_fin : COMA int_var
		| PUNTO_COMA;
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