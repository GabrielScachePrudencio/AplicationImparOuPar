package ImparOuPar.model;

import java.io.Serializable;


public class Jogada implements Serializable{
    private String nome;
    private int valor;
    private String escolha;
    private String modoJogo;
    
    public Jogada(String nome, int valor, String escolha) {
        setNome(nome);
        setValor(valor);
        setEscolha(escolha);
    }

    public Jogada(String nome, int valor, String escolha, String modoJogo) {
        setNome(nome);
        setValor(valor);
        setEscolha(escolha);
        setModoJogo(modoJogo);
    }

    
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome){
        if (nome != null) {
            this.nome = nome;
        }else{
            throw new IllegalArgumentException("Nome nao pode ser nulo");
        }
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor){
         if (valor > 0) {
            this.valor = valor;
        }else{
            throw new IllegalArgumentException("Numero deve ser positivo!");
        }
    }

    public String getEscolha() {
        return escolha;
    }

    public void setEscolha(String escolha) {
        this.escolha = escolha;
    }

    public String getModoJogo() {
        return modoJogo;
    }

    public void setModoJogo(String modoJogo) {
        this.modoJogo = modoJogo;
    }
    
    public String toString() {
        return "Modo_Jogo: " + modoJogo + ", Nome: " + nome + ", Escolha: " + escolha + ", Valor: " + valor;
    }

    
}
