package ImparOuPar.dao;

import ImparOuPar.model.Jogada;

public class JogadaDao {

    public Jogada DisputaPorSoma(Jogada jogada1, Jogada jogada2) {
        int resultado = jogada1.getValor() + jogada2.getValor();
        return determinarVencedor(jogada1, jogada2, resultado);
    }

    public Jogada DisputaPorSubtracao(Jogada jogada1, Jogada jogada2) {
    int valor1 = jogada1.getValor();
    int valor2 = jogada2.getValor();
    int resultado = Math.abs(valor1 - valor2);
    return determinarVencedor(jogada1, jogada2, resultado);
    }
      
    public Jogada DisputaPorDivisao(Jogada jogada1, Jogada jogada2) {
        int resultado = jogada1.getValor() / jogada2.getValor();
        return determinarVencedor(jogada1, jogada2, resultado);
    }

    public Jogada DisputaPorMultiplicacao(Jogada jogada1, Jogada jogada2) {
        int resultado = jogada1.getValor() * jogada2.getValor();
        return determinarVencedor(jogada1, jogada2, resultado);
    }

    private Jogada determinarVencedor(Jogada jogada1, Jogada jogada2, int resultado) {
    boolean ehPar = resultado % 2 == 0;
    
    if (ehPar) {
        if (jogada1.getEscolha().equalsIgnoreCase("Par")) return jogada1;
        if (jogada2.getEscolha().equalsIgnoreCase("Par")) return jogada2;
    } else {
        if (jogada1.getEscolha().equalsIgnoreCase("Impar")) return jogada1;
        if (jogada2.getEscolha().equalsIgnoreCase("Impar")) return jogada2;
    }

    return new Jogada("Empate", resultado, "Nenhuma");
}

}
