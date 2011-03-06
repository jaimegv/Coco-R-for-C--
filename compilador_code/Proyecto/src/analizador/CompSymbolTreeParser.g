header
{
	package analizador;
	
	//| ANALISIS SEMÁNTICO -- Instalación de símbolos |
	
	import antlraux.context.ContextException;
	import antlraux.context.Scope;
	import antlraux.context.asts.*;
	import antlraux.context.types.Type;
	import antlraux.context.types.AttributeType;
	import leli.types.*;
	import antlraux.util.LexInfoAST;
	import antlraux.util.Logger;
	import antlr.TokenStreamException;
	import java.io.FileInputStream;
	import java.io.FileNotFoundException;
}

class CompSymbolTreeParser extends CompTreeParser;

options {
	importVocab=CompParserVocab;
	exportVocab=CompSymbolTreeParserVocab;
	buildAST = true;
	ASTLabelType = antlraux.util.LexInfoAST;
}

tokens
{
TIPO_ERROR = "error";
METACLASE = "holap";
}

{
//	La clase {link LeLiContext} es una fachada que facilita la manipulación
//	contextual
private CompContext contexto;

// El logger de la clase
private Logger logger;


}


programaBasico
: #( PROGRAMA main )
{ ## = new ScopeAST(##, contexto.getCurrentScope()); }
;
exception catch [RecognitionException ce] { reportError(ce); }

