package ImparOuPar.model;

import java.io.Serializable;


public class Jogada implements Serializable{
    private String nome;
    private int valor;
    private String escolha;
    private String modoJogo;
    
    public Jogada(String nome, int valor, String escolha) {
        this.nome = nome;
        this.valor = valor;
        this.escolha = escolha;
    }

    public Jogada(String nome, int valor, String escolha, String modoJogo) {
        this.nome = nome;
        this.valor = valor;
        this.escolha = escolha;
        this.modoJogo = modoJogo;
    }

    
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getEscolha() {
        return escolha;
    }

    public void setEscolha(String escolha) {
        this.escolha = escolha;
    }
    
    public String toString() {
        return "Modo_Jogo: " + modoJogo + ", Nome: " + nome + ", Escolha: " + escolha + ", Valor: " + valor;
    }
}
