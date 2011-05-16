// Definimos el paquete al que pertenece nuestro analizador
header {
	package analizador;
}

// ANALISIS SEMÁNTICO -- Instalación de símbolos

class CompTreeParser extends TreeParser;
 
options {
//	k=1; //No sé por qué, pero en el TreeParser de LELI no lo pone
	importVocab=CompParserVocab;
	buildAST = false; // Por defecto no construimos un AST nuevo
}

/// Permite recorrer el nodo principal, llamado "programa"
// Punto de entrada del codigo fuente, acabará SIEMPRE en un MAIN
programa : #(PROGRAMA (instDecVar)*
			( decMetodo
		  		| subprograma
		  			| decClase )*	// aqui irá la decClase, programas, metodos... 
			main)
			;

subprograma : #(SUBPROGRAMA tipo IDENT listaDecParams cuerpo_sp) ;


main: #(MAINN tipo MAIN listaDecParams cuerpo_sp);



// Declaracion de Clase
// ejemplo: class Persona {};
decClase : 	#(DEC_CLASE IDENT (decCabMet)? (PUBLIC decCabMet)? (PRIVATE decCabMet)?);


decCabMet : #(DEC_CAB_MET(tipo IDENT 
				( (IDENT)+
					| (tipo (tipo)*) 
				)?)+);

/** Permite recorrer la declaración de una variable local */
instDecVar : #(INST_DEC_VAR tipo IDENT (expresion|listaExpresiones)? ) ;
/**
 * Lista de expresiones. Sirve para representar parámetros pasados a 
 * un método o un constructor
 */
listaExpresiones : #(LISTA_EXPRESIONES (expresion)* ) ;


/**
 * Declaración de un método
 * 
 * Declaracion de metodos de una clase ejemplo: int Fecha::daDia (void)*/
decMetodo : #(DEC_METODO tipo IDENT IDENT listaDecParams
					cuerpo_sp) 
			;
			
listaDecParams 
		:	#(LISTA_DEC_PARAMS (tipo (IDENT (tipo IDENT)*)?)?)
		;
		
cuerpo_sp : #(CUERPO_SP (instruccion )*)  
		;

instruccion : #(INSTRUCCION (
			  instDecVar	// declaracion de var no global! (de ahi el false)
			| expresion					// asig, suma...
			| instCond
			| instCondSimple
			| instCout
			| instCin
			| instReturn))
			;
			

			
instReturn : #(RETURN (expresion)?);

instCondSimple : #(INST_COND_SIMPLEacceso (OP_IGUAL | OP_DISTINTO | OP_MAYOR
										OP_MENOR | OP_MENOR_IGUAL | OP_MAYOR_IGUAL)
							acceso
				 INTER acceso DOSPUNTOS acceso)
		;

instDecMet :	// declarar metodo
		#(INSTDECMET IDENT IDENT lista_valores)
		;

lista_valores : #(LISTA_VALORES (q_argumento)+);

instCout : #(COUT (MENOR_MENOR (IDENT | LIT_CADENA))+); 
// cin >> num;	// se lee un nmero del teclado
instCin : #(CIN MAYOR_MAYOR (IDENT | LIT_CADENA));
// argumentos que pueden recibir las expresiones

instCond : 	#(IF expresion 
			(cuerpo_sp
				| instruccion)
			sino)
;

// if (pascual)... 
sino : #(SINO (sinosi sino	//else if...
		| sinofin	//else...
		| /*nada*/))
;

// else if (lalala) 
// 	{hagase mi voluntad}		
sinosi : #(SINOSI ELSE IF expresion
			(cuerpo_sp | instruccion ))
;

// else {hagase mi voluntad}
sinofin: #(ELSE (cuerpo_sp | instruccion))
;


/**
 * Regla que permite reconocer todas las instrucciones
 */
expresion
	: #(OP_MAS          expresion expresion)
	| #(OP_MENOS        expresion expresion)
	| #(OP_ASIG         expresion expresion)
	| #(OP_OR           expresion expresion)
	| #(OP_AND          expresion expresion)
	| #(OP_IGUAL        expresion expresion)
	| #(OP_DISTINTO     expresion expresion)
	| #(OP_MAYOR        expresion expresion)
	| #(OP_MENOR        expresion expresion)
	| #(OP_MAYOR_IGUAL  expresion expresion)
	| #(OP_MENOR_IGUAL  expresion expresion)
	| #(OP_PRODUCTO     expresion expresion)
	| #(OP_DIVISION     expresion expresion)
	| #(OP_MENOS_UNARIO expresion)
	| #(OP_MASMAS       expresion)
	| #(OP_MENOSMENOS   expresion)
	| #(OP_NOT          expresion)
	| acceso
	;


acceso 	: IDENT (llamada)+
		| literal
		| llamada
		| IDENT
		;
llamada : #(LLAMADA IDENT listaExpresiones);

		
q_argumento : LIT_CADENA
			| LIT_ENTERO_OCTAL
			| LIT_ENTERO_DECIMAL
			| IDENT q_argumento
			| IDENT
			| CTE_LOGTRUE
			| CTE_LOGFALSE
			| /* nada */ 
;

literal : LIT_ENTERO_OCTAL
		| LIT_ENTERO_DECIMAL
		| LIT_CADENA
		| CTE_LOGTRUE
		| CTE_LOGFALSE
		;

tipo :
	INT						// errores con declaracion de globales
	| BOOL
	| VOID
	| CHAR OP_PRODUCTO	// cadena de caracteres
	|  IDENT; 