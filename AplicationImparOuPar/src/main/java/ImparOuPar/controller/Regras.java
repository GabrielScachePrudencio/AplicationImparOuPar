package ImparOuPar.controller;

import ImparOuPar.dao.JogadaDao;
import ImparOuPar.model.Jogada;

public class Regras {
    private JogadaDao dao = new JogadaDao();

    public Jogada DisputaPorSoma(Jogada jogada1, Jogada jogada2) {
        return dao.DisputaPorSoma(jogada1, jogada2);
    }

    public Jogada DisputaPorSubtracao(Jogada jogada1, Jogada jogada2) {
        return dao.DisputaPorSubtracao(jogada1, jogada2);
    }
    
    public Jogada DisputaPorDivisao(Jogada jogada1, Jogada jogada2) {
        return dao.DisputaPorDivisao(jogada1, jogada2);
    }

    public Jogada DisputaPorMultiplicacao(Jogada jogada1, Jogada jogada2) {
        return dao.DisputaPorMultiplicacao(jogada1, jogada2);
    }
}
