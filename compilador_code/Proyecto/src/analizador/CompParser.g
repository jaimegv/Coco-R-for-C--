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
	INST_EXPRESION ;
	EXPRESION ;
	OP_MENOS_UNARIO ;
	ACCESO ;
	LISTA_EXPRESIONES ;
	LLAMADA ;
	CLASE ;			// declracion de una clase
	DEC_METODO ; 	// declaracion de metodo de una clase
	CALL_METODO ; 	// llamada a un metodo
	LISTA_INSTRUCCIONES ; // lista de intrucciones que pertenecen al cuerpo
	ARGUMENTOS_ENTRADA ; 	// LISTA DE ARGUMENTOS DE ENTRADA
	SUBPROGRAMA ;	// declaracion de una funcion
	PROGRAMA ; // El punto de entrada del programa
	INST_RETURN ; // instruccion de RETURN
	CONDSIMPLE ; // Condicional simple
	LITERAL ; // numero enteros, cadenas...
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
programa : (instDecVar)*
			( decMetodo
		  		| subprograma
		  			| decClase )*	// aqui irá la decClase, programas, metodos... 
			main
				{ ## = #( #[PROGRAMA, "PROGRAMA"], ##);}
			;


// aqui se entra al main->raiz del arbol
main: ttipo[false] MAIN^ PARENT_AB! listaDecParams PARENT_CE!
		LLAVE_AB! 
			cuerpo_sp 
		LLAVE_CE!
		;


/* Reglas de generación*/
// FUNCIONES-SUBPROGRAMAS C++
subprograma : ttipo[false] IDENT^ PARENT_AB! listaDecParams PARENT_CE!
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
decMetodo : ttipo[false] IDENT^ DOSPUNTOS_DOS! IDENT PARENT_AB! listaDecParams PARENT_CE!
				LLAVE_AB! 
					cuerpo_sp 
				LLAVE_CE!
			{ ## = #( #[DEC_METODO, "decMetodo"], ##);}
			;

// Argumentos de entrada de las funciones
// por ejemplo: int s, char s, int w ó vacio (solo paso por valor)
listaDecParams { final AST raiz = #[RES_PARAMETRO, "parametro"]; } 
		:	ttipo[true]
			| (listaDeclaraciones[raiz, false] 
				(COMA! listaDeclaraciones[raiz, false])*
			)?
		;


// Cuerpo de un subprograma o programa general
// lista declaracion de instrucciones
cuerpo_sp : ( instruccion )* (instReturn)?
			{ ## = #( #[LISTA_INSTRUCCIONES, "LISTA_INSTRUCCIONES"], ##);}  
		;

// Instruccion return
// puede o no estar, y devolver un valor o una var...
instReturn : RETURN! (instExpresion 
						| PUNTO_COMA! /*nada*/)	// return; se puede omitir
			{ ## = #( #[INST_RETURN, "INST_RETURN"], ##);}
		;

// Instruccion de declaracion de variables
instDecVar { final AST raiz = #[INTS_DEC_VAR, "VARIABLE"]; } 
		:	listaDeclaraciones[raiz, true] PUNTO_COMA!
		;
listaDeclaraciones [AST raiz, boolean inicializacion] : 
		t: ttipo[true]! declaracion[raiz, #t, inicializacion, false]
				(COMA! declaracion[raiz, #t, inicializacion, false])*
		;

declaracion !	// desactivamos el AST contructor por defecto
		[AST r, AST t, boolean inicializacion, boolean decmetodo]	// parametros de la gen gramtrica
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
		  i2: IDENT OP_ASIG arg:q_argumento	// CASO: int var=23
		  { 
				raiz.addChild(#i2);
				raiz.addChild(#arg);
				## = raiz;
		  }
/*		| { decmetodo }? // pred semantico, caso: hola.hola =234
		  i5: IDENT PUNTO! IDENT OP_ASIG q_argumento
		  { 
				raiz.addChild(#i5);
				## = raiz;
		  }

		| { decmetodo }? // pred semantico, caso saludo.hola(); ó saludo.hola(23);
			// si tiene un true se puede asignar cosas
		  i3: IDENT PUNTO! IDENT PARENT_AB! arg:q_argumento PARENT_CE! (OP_ASIG asignado:q_argumento)? PUNTO_COMA! 
		  { raiz.addChild(#i3);
		  	raiz.addChild(#arg);
		  	raiz.addChild(#asignado);
		  	## = raiz;
		  }
*/
		| { inicializacion }? // pred semantico, caso saludo.hola(); ó saludo.hola(23);
			// si tiene un true se puede asignar cosas
		  i6: IDENT PARENT_AB! valor:q_argumento PARENT_CE! 
		  { raiz.addChild(#i6);
		  	raiz.addChild(#valor);
		  	## = raiz;
		  }
		| // si tiene un true se puede asignar cosas
		  i4: IDENT CORCHETE_AB (LIT_ENTERO_OCTAL|LIT_ENTERO_DECIMAL)? CORCHETE_CE	// CASO: int jarl[23]
		  { raiz.addChild(#i4);
		  	//raiz.addChild(#li);
		  	## = raiz;
		  }
;
				
// Diferentes tipos de sentencias
// El llamante ya ha puesto el instruccion*
instruccion : (ttipo[true] IDENT)=> instDecVar	// declaracion de var
//			| instDecMet
			| instExpresion					// asig, suma...
			| instCond
			//| sentencia_llam_func
			| instNula						// Simplemente: -> ; <-
			; // No se añade la insReturn aqui!

// instExpresion
// lleva el peso de todo: cond(simple|comleja), asig...
instExpresion : expresion PUNTO_COMA!
			{ ## = #( #[INST_EXPRESION, "INST_EXPRESION"], ##); };

// EMPIEZA LAS DIFERENTES EXPRESION
expresion : expAsignacion;

expAsignacion : expOLogico (OP_ASIG^ (/* instCondSimple | */expOLogico ) )?;

// instConSimple
// del tipo (v[2] < v[3]) ? v[2]: v[3]
//sentCondSimple
instCondSimple : PARENT_AB! acceso (OP_IGUAL^ | OP_DISTINTO^ | OP_MAYOR^
										OP_MENOR^ | OP_MENOR_IGUAL^ | OP_MAYOR_IGUAL^)
							acceso
				 PARENT_CE! INTER acceso DOSPUNTOS acceso
				 { ## = #( #[CONDSIMPLE, "CONDSIMPLE"], ##); }
		;	// tambien la meto en la regla: expresion o una mas adelante, jejeje

expOLogico: expYLogico (OP_OR^ expYLogico)?;

expYLogico : expComparacion (OP_AND^ expComparacion)?;

expComparacion : expAritmetica 
		(
			(OP_IGUAL^ | OP_DISTINTO^ | OP_MAYOR^
				OP_MENOR^ | OP_MENOR_IGUAL^ | OP_MAYOR_IGUAL^
			)
			expAritmetica 
		)*;

expAritmetica : expProducto ((OP_MAS^|OP_MENOS^) expProducto)*;

expProducto : expCambioSigno
		((OP_PRODUCTO^ | OP_DIVISION^) expCambioSigno)*;

expCambioSigno : 
			( OP_MENOS! expPostIncremento
				{ ## = #(#[OP_MENOS_UNARIO,"OP_MENOS_UNARIO"], ##); }
			) | (OP_MAS!)? expPostIncremento;

expPostIncremento : expNegacion (OP_MASMAS | OP_MENOSMENOS)?;

// seria esto: expEsUn; pero no gastamos esta expresion
expNegacion : (OP_NOT^)* acceso;

//expEsUn : acceso (RES_ESUN^ tipo ); no tenemos de esto

acceso 	: r1: raizAcceso { ## = #(#[ACCESO, "ACCESO"], #r1);}
			( PUNTO! sub1:subAcceso! { ##.addChild(#sub1); } )*
/*		| r2: raizAccesoConSubAccesos! {  ## = #(#[ACCESO, "ACCESO"], #r2);}
			( PUNTO! sub2:subAcceso! { ##.addChild(#sub2); } )+
*/
		//| r4: literal
//		| r3: raizAccesoSinAccesos! {  ## = #(#[ACCESO, "ACCESO"], #r3);}
//			( PUNTO! sub3:subAcceso! { ##.addChild(#sub3); } )*
		;
/* Raiz de los accesos que no son llamadas a un metodos de la clase*/
raizAcceso : IDENT 
			| llamada
			| l1:literal
			//| conversion	// conversion de tipos, NO!
			| PARENT_AB! e1:expresion PARENT_CE! { 	## = #(#[ACCESO, "ACasdCESO"], #e1);
													##.addChild(#e1); }
			;

raizAccesoConSubAccesos : OP_MAS;

raizAccesoSinAccesos: llamada 
//			| PARENT_AB! expresion PARENT_CE!;
;

subAcceso : IDENT
			| llamada
;
 
// Representa:
// llamada a un metodo
// subacceso en forma de llamada
// llamada a un constructor
// subacceso en forma de constructor
llamada : IDENT PARENT_AB! listaExpresiones PARENT_CE!
		{ ## = #(#[LLAMADA, "LLAMADA"], ##);}
		;
listaExpresiones : ((IDENT | literal) (COMA! (IDENT | literal))*)?
		{ ## = #(#[LISTA_EXPRESIONES, "LISTA_EXPRESIONES"], ##);}
		;
literal : LIT_ENTERO_OCTAL { ## = #(#[LITERAL, "ent_OCTAL"], ##);}
		| LIT_ENTERO_DECIMAL { ## = #(#[LITERAL, "ENT_DECIMAL"], ##);}
		| LIT_CADENA { ## = #(#[LITERAL, "LIT_CADENA"], ##);}
		| CTE_LOGTRUE { ## = #(#[LITERAL, "LIT_TRUE"], ##);}
		| CTE_LOGFALSE { ## = #(#[LITERAL, "LIT_FALSE"], ##);}
		// Creo que es todo
		// añadido tras ver el condicional simple
		;

// instruccion NULA
instNula : PUNTO_COMA! ;	// se omite por que no vale para nada

// Por ejemplo: hola.saludo(1,2,43,4) ó hola.jarl() ó hola.jar=234;
instDecMet :	// declarar metodo
		d : IDENT PUNTO! IDENT PARENT_AB! lista_valores PARENT_CE! PUNTO_COMA!
		{	## = #(#[CALL_METODO, "CALL_METODO"], ##); };

// Intruccion Condicional grande!
// if (juanito=9) {} ...
instCond : 	IF^ PARENT_AB! expresion PARENT_CE!
			LLAVE_AB! cuerpo_sp LLAVE_CE!
			sino 
;

// if (pascual)... 
sino : sinosi sino
		| sinofin
		| /*nada*/
;

// else if (lalala) 
// 	{hagase mi voluntad}		
sinosi : ELSE^ IF PARENT_AB! expresion PARENT_CE!
			LLAVE_AB! cuerpo_sp LLAVE_CE!
;

// else {hagase mi voluntad}
sinofin: ELSE^
			LLAVE_AB! cuerpo_sp LLAVE_CE!
;

// la lista: 2,34,5__ NOTA: cuidado con el vacio de q_argumento
lista_valores : q_argumento (COMA! q_argumento)*
						{ ## = #( #[ARGUMENTOS_ENTRADA, "ARGUMENTOS_ENTRADA"], ##);};


detIgual [AST raiz] : 
		t: OP_ASIG 	;
	
// Tipo de argumento que se asignan a variables y demás
// serán: 34, "hola", funcionJARL(23), algo.algo()
q_argumento : LIT_CADENA
			| LIT_ENTERO_OCTAL
			| LIT_ENTERO_DECIMAL
			| IDENT PARENT_AB! q_argumento PARENT_CE!
			| IDENT
			| CTE_LOGTRUE
			| CTE_LOGFALSE
			| /* nada */ 
;

// declaracion de un elem de un vector (elem_vector)
// afirmamos que solo puede ir un entero dentro del corchete
e_vector : IDENT^ CORCHETE_AB! LIT_ENTERO_DECIMAL CORCHETE_CE! ;

// tipos genericos que reconoce nuestro lenguaje
ttipo [boolean variable] : 
	INT
	| BOOL
	| VOID
	| CHAR
//	| CHAR OP_PRODUCTO // en caso de puntero a cadena, SOLO TENEMOS PARAM POR VALOR
	| { variable }? // si es un ttipo de variable puede ser IDENT, e.o.c. NO
		IDENT; 

cadena : vector
	| /* NADA (para el -> int IDENT*/;

vector : CORCHETE_AB LIT_ENTERO_DECIMAL CORCHETE_CE; // int e[20]

