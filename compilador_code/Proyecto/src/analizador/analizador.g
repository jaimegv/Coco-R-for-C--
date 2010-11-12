header {
	package analizador;
}

// Este será uno de los tres tipos de analizadores que tendremos
// que hacer, este caso es el Lexico.
class analizador extends Lexer ;


options{
	charVocabulary = '\3'..'\377';	// Caracteres que podemos leer
	exportVocab = CompLexerVocab;
	testLiterals=false;
	k=3;	 // Tamano del lexema
}

tokens {
	LIT_REAL; 
	LIT_ENTERO;
}

//{ // Comienza la zona de código
//	protected Token makeToken(int type)	{
//		// Usamos la implementación de la superclase...
//		Token result = super.makeToken(type);
//		// ...y añadimos información del nombre de fichero
//		result.setFilename(inputState.filename);
//		// y devolvemos el token
//		return result;
//	}
//}

/** Esta regla permite ignorar los blancos.*/
protected BLANCO : ( ' '
					| '\t'
					| NL
			) { $setType(Token.SKIP); }; // La accion del blanco: ignorar

/**Los tres tipos de retorno de carro.*/
protected NL : (("\r\n") => "\r\n" // MS-DOS
				| '\r'	// MACINTOSH
				| '\n'	// UNIX
			)	{ newline();};
			
/** Letras españolas. */
protected LETRA: 'a'..'z'
				| 'A'..'Z'
				| 'ñ' | 'Ñ'
				| 'á' | 'é' | 'í' | 'ó' | 'ú'
				| 'Á' | 'É' | 'Í' | 'Ó' | 'Ú'
				| 'ü' | 'Ü';
				
/** Dígitos usuales */
protected DIGITO : '0'..'9';

/** Regla que permite reconocer los literales (y palabras reservadas).*/
IDENT options {testLiterals=true;} // Comprobar palabras reservadas
			: (LETRA|'_') (LETRA|DIGITO|'_')*; 	// Empieza por Letra o _
												// _a3 a_3_4 qweWEQ...
												
// CONSTANTES
LIT_NUMERO : (( DIGITO )+ '.' ) =>
				( DIGITO )+ '.' ( DIGITO )* { $setType (LIT_REAL); }
				| ( DIGITO )+ { $setType (LIT_ENTERO); }
;
												
// OPERADORES
// OP_Artimeticos
OP_MAS : '+' ;
OP_MENOS : '-' ;
OP_PRODUCTO : '*' ;
OP_DIVISION : '/' ;
OP_MODULO : '%' ;
// OP_Relacion
OP_IGUAL : "==" ;
OP_DISTINTO : "!=" ;
OP_MENOR : '<' ;
OP_MAYOR : '>' ;
OP_MENOR_IGUAL : "<=" ;
OP_MAYOR_IGUAL : ">=" ;
// OP_LOGICOS
OP_AND : "&&" ;
OP_OR : "||" ;
OP_NOT : '!' ;
// OP_AUTO_[INCR|DECR]emento
// tanto delanto como detrás
OP_MASMAS : "++" ;
OP_MENOSMENOS : "--" ;
// OP_condicional
DOSPUNTOS : ':' ;
INTER : '?' ;
// OP_asignacion
OP_ASIG : '=' ;
// OP_asig+op
OP_ASIG_MAS : "+=" ;
OP_ASIG_MENOS : "-=" ;
OP_ASIG_PRODUCTO : "*=" ;
OP_ASIG_DIVISION : "/=" ;
OP_ASIG_MODULO : "%=" ;
// Separadores
PUNTO_COMA : ';' ;
//COMA : ',' ;
CORCHETE_AB : '[' ;
CORCHETE_CE : ']' ;
LLAVE_AB : '{' ;
LLAVE_CE : '}' ;
PUNTO : '.' ;
PARENT_AB : '(' ;
PARENT_CE : ')' ;
BARRA_VERT : '|';



