// Definimos el paquete al que pertenece nuestro analizador
header {
	package analizador;
}

class CompParser extends Parser ;

options {
	/* opciones del analizador*/
	// LookAhead.
	// cuantos caracteres cogerá para concluir Y DETERMINAR la regla
	k = 3;	// ya que normalmente siempre empieza con: ttipo IDENT
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
	RES_PARAMETRO ;
	INTS_DEC_VAR ;
	EXPRESION ;
	CLASE ;			// declracion de una clase
	DEC_METODO ; 	// declaracion de metodo de una clase
	CALL_METODO ; 	// llamada a un metodo
	LISTA_INSTRUCCIONES ; // lista de intrucciones que pertenecen al cuerpo
	ARGUMENTOS_ENTRADA ; 	// LISTA DE ARGUMENTOS DE ENTRADA
	SUBPROGRAMA ;	// declaracion de una funcion
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
programa :  instDecVar
			( decMetodo
		  		| subprograma 
		  			| decClase /**/)*	// aqui irá la decClase, programas, metodos... 
			main 
				{ ## = #( #[PROGRAMA, "PROGRAMA"], ##);}
			;


// aqui se entra al main->raiz del arbol
main: ttipo MAIN^ PARENT_AB! listaDecParams PARENT_CE!
		LLAVE_AB! 
			cuerpo_sp 
		LLAVE_CE!
		;


/* Reglas de generación*/
// FUNCIONES-SUBPROGRAMAS C++
subprograma : ttipo IDENT^ PARENT_AB! listaDecParams PARENT_CE!
				LLAVE_AB! 
					cuerpo_sp 
				LLAVE_CE!
			{ ## = #( #[SUBPROGRAMA, "SUBPROGRAMA"], ##);}
			;

// Declaracion de Clase
// ejemplo: class Persona {};
decClase : CLASS! IDENT^ LLAVE_AB! 
					cuerpo_sp 
			LLAVE_CE!
			{ ## = #( #[CLASE, "CLASE"], ##);}
			;

// Declaracion de metodos de una clase
// ejemplo: int Fecha::daDia (void)
decMetodo : ttipo IDENT^ DOSPUNTOS_DOS! IDENT PARENT_AB! listaDecParams PARENT_CE!
				LLAVE_AB! 
					cuerpo_sp 
				LLAVE_CE!
			{ ## = #( #[DEC_METODO, "decMetodo"], ##);}
			;

// Argumentos de entrada de las funciones
// por ejemplo: int s, char s, int w ó vacio (solo paso por valor)
listaDecParams { final AST raiz = #[RES_PARAMETRO, "parametro"]; } 
		:	ttipo
			| (listaDeclaraciones[raiz, false] 
				(COMA! listaDeclaraciones[raiz, false])*
			)?
		;


// Cuerpo de un subprograma o programa general
// lista declaracion de instrucciones
cuerpo_sp : ( instruccion )*
			{ ## = #( #[LISTA_INSTRUCCIONES, "LISTA_INSTRUCCIONES"], ##);}  
		;

// Instruccion de declaracion de variables
instDecVar { final AST raiz = #[INTS_DEC_VAR, "variable"]; } 
		:	listaDeclaraciones[raiz, true] PUNTO_COMA!
		;
listaDeclaraciones [AST raiz, boolean inicializacion] : 
		t: ttipo! declaracion[raiz, #t, inicializacion]
				(COMA! declaracion[raiz, #t, inicializacion])*
		;
declaracion !	// desactivamos el AST contructor por defecto
		[AST r, AST t, boolean inicializacion]	// parametros de la gen gramtrica
		{	
			AST raiz = astFactory.dupTree(r);	// copiamos el arbol
			raiz.addChild(astFactory.dupTree(t));	// copia del arbol
		}
		: i1: IDENT	// CASO en que es: int var
			{
				raiz.addChild(#i1);
				## = raiz;
			}
		| { inicializacion }? // pred semantico, caso int var=23
			// si tiene un true se puede asignar cosas
		  i2: IDENT OP_ASIG q_argumento	// CASO: int var=23
		  { 
				raiz.addChild(#i2);
				//raiz.addChild(#valor);
				## = raiz;
		  }
/*		| { inicializacion }? // pred semantico, caso: hola.hola =234
		  i5: IDENT PUNTO! IDENT OP_ASIG q_argumento
		  { 
				raiz.addChild(#i5);
				## = raiz;
		  }
*/
		| { inicializacion }? // pred semantico, caso int var(algo)
			// si tiene un true se puede asignar cosas
		  i3: IDENT PARENT_AB! valor:q_argumento PARENT_CE! 
		  { raiz.addChild(#i3);
		  	raiz.addChild(#valor);
		  	## = raiz;
		  }
		| { inicializacion }? // pred semantico, caso int vector[30]
			// si tiene un true se puede asignar cosas
		  i4: IDENT CORCHETE_AB (LIT_ENTERO_OCTAL|LIT_ENTERO_DECIMAL) CORCHETE_CE	// CASO: int jarl[23]
		  { raiz.addChild(#i4);
		  	//raiz.addChild(#li);
		  	## = raiz;
		  }
;

expresion : r1:q_argumento { ## = #(#[EXPRESION], #r1); } ;

				
// Diferentes tipos de sentencias
// El llamante ya ha puesto el instruccion*
instruccion : (ttipo IDENT)=> instDecVar	// declaracion de var
			| sentencia_llam_met
			/*| sentencia_cond_simple
			| sentencia_llam_func*/
			| instNula
			;

// instruccion NULA
instNula : PUNTO_COMA! ;	// se omite por que no vale para nada
			
// Por ejemplo: hola.saludo(1,2,43,4) ó hola.jarl()
sentencia_llam_met : IDENT PUNTO! IDENT PARENT_AB! lista_valores PARENT_CE! PUNTO_COMA!
						{ ## = #( #[CALL_METODO, "CALL_METODO"], ##);};
// la lista: 2,34,5__ NOTA: cuidado con el vacio de q_argumento
lista_valores : q_argumento (COMA! q_argumento)*
						{ ## = #( #[ARGUMENTOS_ENTRADA, "ARGUMENTOS_ENTRADA"], ##);};


sentencia_asig : PUNTO_COMA!;
	
// Tipo de argumento que se asignan a variables y demás
// serán: 34, "hola", funcionJARL(23), algo.algo()
q_argumento : LIT_CADENA
			| LIT_ENTERO_OCTAL
			| LIT_ENTERO_DECIMAL
			| IDENT PARENT_AB! q_argumento PARENT_CE!
			| IDENT
			| CTE_LOGTRUE
			| CTE_LOGFALSE
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

