
package ImparOuPar.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Servidor {
    public static void main(String[] args) throws Exception {
        System.out.println("IP carregado: " + Config.getIp());
        System.out.println("Porta carregada: " + Config.getPorta());

        ServerSocket servidor = new ServerSocket(Config.getPorta(), 2, InetAddress.getByName(Config.getIp()));
        System.out.println("Servidor JogoDaVelha Inicializado (" + servidor + ").\n");

        // JOGADOR 1
        System.out.println("Esperando por conexão (Jogador 1)...");
        Socket jogador1 = servidor.accept();
        System.out.println("Conexão recebida: " + jogador1.toString() + ":" + jogador1.getPort() + "\n");

        // Ordem correta: primeiro o OUTPUT, depois o INPUT
        ObjectOutputStream saidaJogador1 = new ObjectOutputStream(jogador1.getOutputStream());
        saidaJogador1.flush();
        ObjectInputStream entradaJogador1 = new ObjectInputStream(jogador1.getInputStream());

        saidaJogador1.writeObject("jogador1:true");

        // JOGADOR 2
        System.out.println("Esperando por conexão (Jogador 2)...");
        Socket jogador2 = servidor.accept();
        System.out.println("Conexão recebida: " + jogador2.toString() + ":" + jogador2.getPort() + "\n");

        ObjectOutputStream saidaJogador2 = new ObjectOutputStream(jogador2.getOutputStream());
        saidaJogador2.flush();
        ObjectInputStream entradaJogador2 = new ObjectInputStream(jogador2.getInputStream());

        saidaJogador2.writeObject("jogador2:false");

        // SORTEIA MODO
        int modoJogo = new Random().nextInt(4);
        saidaJogador1.writeObject("MODO:" + modoJogo);
        saidaJogador2.writeObject("MODO:" + modoJogo);

        // LÊ RESPOSTAS DOS JOGADORES
        String msg1 = entradaJogador1.readObject().toString();
        String msg2 = entradaJogador2.readObject().toString();

        System.out.println("Mensagem do jogador 1: " + msg1);
        System.out.println("Mensagem do jogador 2: " + msg2);

        if (msg1.equals("NOVO_JOGO") && msg2.equals("NOVO_JOGO")) {
            modoJogo = new Random().nextInt(4);
            saidaJogador1.writeObject("MODO:" + modoJogo);
            saidaJogador2.writeObject("MODO:" + modoJogo);
        }

        // INICIA THREADS
        Thread thJo1 = new Thread(new GerenciadorJogadas(entradaJogador1, saidaJogador2));
        Thread thJo2 = new Thread(new GerenciadorJogadas(entradaJogador2, saidaJogador1));

        thJo1.start();
        thJo2.start();
    }
}