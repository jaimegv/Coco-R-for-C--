package compilationunit;

class tupla_Tercetos {
    String clase, metodo, terceto;

    public tupla_Tercetos(String clase, String metodo, String terceto){
        this.clase = clase;
        this.metodo = metodo;
        this.terceto = terceto;
    }

    public String getClase(){
        return clase;
    }

    public String getMetodo(){
        return metodo;
    }

    public String getTerceto (){
        return terceto;
        }

    public void setClase(String clase){
        this.clase = clase;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public void setTerceto (String terceto) {
        this.terceto = terceto;

    }

}