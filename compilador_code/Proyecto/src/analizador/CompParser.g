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
	// Activando la construccion del build para el semántico
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
	
	SUBPROGRAMA ;
	PROGRAMA ; // El punto de entrada del programa
}
/* NOTAS:
* - Significado de las almohadillas en la pagina 64 del manual ANTLR
* - El símbolo ! detras de un elementos significa que dicho elemento se consume pero no 
* se envía al sig modulo
* - ^ significa que a partir de ahi se empezará a enraizar el arbol
* - en la zona de tokens la cosa va en MAYUS, en la declaracion un terminal esMAYUS y
* un no terminal es minus
**/


// Punto de entrada del codigo fuente, acabará SIEMPRE en un MAIN
programa :  (subprograma/*| decClase | metodos*/)*	// aqui irá la decClase, programas, metodos... 
			main 
				{ ## = #( #[PROGRAMA, "PROGRAMA"], ##);}
			;


// aqui se entra al main->raiz del arbol
main: ttipo MAIN^ PARENT_AB! lista_argumento PARENT_CE!
		LLAVE_AB! 
			cuerpo_sp 
		LLAVE_CE!;


/* Reglas de generación*/
// FUNCIONES-SUBPROGRAMAS C++
subprograma : ttipo IDENT^ PARENT_AB! lista_argumento PARENT_CE!
				LLAVE_AB! 
					cuerpo_sp 
				LLAVE_CE!;

// Argumentos de entrada de las funciones
// por ejemplo: int s, char *s, int w ó vacio
lista_argumento : ttipo IDENT^ siguiente_arg
				| ttipo	// puede ser una funcion del tipo: void hola (int)
				| /* nada */ ;
siguiente_arg : COMA sig_arg_tipo IDENT^ siguiente_arg
			| /* nada */ ;
sig_arg_tipo : ttipo
			| /*nada*/ ;


// Cuerpo de un subprograma o programa general
cuerpo_sp : declaracion_local cuerpo_sp
		| sentencia cuerpo_sp 
		| /* nada */ ;

// Declaracion de variables
// p.e. int qwe , jarl = 123 , HOLA , _asd , jarl = asd , ere [ 23 ] ;
declaracion_local : ttipo dec_var;

dec_var : IDENT^ dec_asig siguiente_dec
		| e_vector dec_asig siguiente_dec;
dec_arg : IDENT^ dec_asig
		| e_vector;
// quizas haya que poner otra regla				
siguiente_dec :	COMA dec_arg siguiente_dec
				| PUNTO_COMA ;
				
dec_asig : OP_ASIG q_argumento
			| /* nada */ ;
				
// Diferentes tipos de sentencias
sentencia :	sentencia_llam_met
			/*| sentencia_asig 
			| sentencia_cond_simple
			| sentencia_llam_func*/
			;
			
// Por ejemplo: hola.saludo(1,2,43,4) ó hola.jarl()
sentencia_llam_met : IDENT PUNTO IDENT PARENT_AB! lista_valores PARENT_CE! PUNTO_COMA;
// la lista: 2,34,5__ NOTA: cuidado con el vacio de q_argumento
lista_valores : q_argumento
			| COMA lista_valores;
				
// Tipo de argumento que se asignan a variables y demás
// serán: 34, "hola", funcionJARL(23), algo.algo()
q_argumento : LIT_CADENA
			| LIT_ENTERO_OCTAL
			| LIT_ENTERO_DECIMAL
			| IDENT PARENT_AB! q_argumento PARENT_CE!
			| IDENT
			| /* nada */ ;

// declaracion de un elem de un vector (elem_vector)
// afirmamos que solo puede ir un entero dentro del corchete
e_vector : IDENT^ CORCHETE_AB! LIT_ENTERO_DECIMAL CORCHETE_CE! ;

// tipos genericos que reconoce nuestro lenguaje
ttipo : INT 
	| BOOL
	| VOID
	| CHAR
//	| CHAR OP_PRODUCTO // en caso de puntero a cadena, SOLO TENEMOS PARAM POR VALOR
	| IDENT;	// puedo declarar una var del tipo de un objeto, p.e: Persona yo; 
	
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