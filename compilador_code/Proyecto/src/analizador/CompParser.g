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
	
	// Tokens imaginarios para enraizar listas
	PROGRAMA;
	LISTA_MIEMBROS;
	LISTA_EXPRESIONES;
	LISTA_INSTRUCCIONES;
	// Tokens imaginarios que se utilizan cuando no hay raíces adecuadas
	OP_MENOS_UNARIO;
	INST_EXPRESION;
	INST_DEC_VAR;
	LLAMADA;
	ATRIBUTO;
	// Otros
	TIPO_VACIO;
	
}

/* Reglas de generación*/

// Zona de reglas

/* Instrucción nula */
instNula : PUNTO_COMA! ;