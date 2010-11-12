header {
	package analizador;
}

// Este será uno de los tres tipos de analizadores que tendremos
// que hacer, este caso es el Lexico.
class analizador extends Lexer ;


options{
	charVocabulary = '\3'..'\377';	// Caracteres que podemos leer
	exportVocab=LeLiLexerVocab;
	testLiterals=false;
	k=2;	// Tamano del lexema
}