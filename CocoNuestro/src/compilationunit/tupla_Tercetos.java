package compilationunit;

class tupla_Tercetos {
    String terceto;
    TablaSimbolos ambito_actual;

    public tupla_Tercetos(TablaSimbolos ambito_actual, String terceto){
        this.ambito_actual = ambito_actual;
        this.terceto = terceto;
    }

    public TablaSimbolos GetAmbitoActual(){
        return this.ambito_actual;
    }


    public String GetTerceto (){
        return terceto;
        }


    public void SetAmbitoActual(TablaSimbolos ambito) {
        this.ambito_actual = ambito;
    }

    public void SetTerceto (String terceto) {
        this.terceto = terceto;

    }

}