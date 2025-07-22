package ImparOuPar.network;

import ImparOuPar.model.Jogada;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GerenciadorJogadas implements Runnable {
    private ObjectInputStream entradaJogador1;    // onde se lê as jogadas do jogador 1
    private ObjectOutputStream saidaJogador2;     // onde se envia as jogadas para o jogador 2

    public GerenciadorJogadas(ObjectInputStream entradaJogador1, ObjectOutputStream saidaJogador2) {
        this.entradaJogador1 = entradaJogador1;
        this.saidaJogador2 = saidaJogador2;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Jogada jogada = (Jogada) entradaJogador1.readObject();
                if (jogada == null) {
                    break; // conexão fechada ou fim das jogadas
                }
                saidaJogador2.writeObject(jogada);
                saidaJogador2.flush();
            }
        } catch (Exception e) {
            System.out.println("Conexão perdida ou erro na comunicação: " + e.getMessage());
            // Aqui pode-se avisar o servidor para limpar recursos, fechar conexão, etc.
        } finally {
            try {
                if (entradaJogador1 != null) entradaJogador1.close();
                if (saidaJogador2 != null) saidaJogador2.close();
            } catch (Exception e) {
                // Ignore ou registre erro no fechamento
            }
        }
    }
}
