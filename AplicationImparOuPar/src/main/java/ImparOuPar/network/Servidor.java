package ImparOuPar.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Servidor {
    public static void main(String[] args) throws Exception{
        System.out.println("IP carregado: " + ImparOuPar.network.Config.getIp());
        System.out.println("Porta carregada: " + ImparOuPar.network.Config.getPorta());
    
        ServerSocket servidor;
        servidor = new ServerSocket( ImparOuPar.network.Config.getPorta(), 2, InetAddress.getByName( ImparOuPar.network.Config.getIp() ) );
        System.out.println("Servidor JogoDaVelha Inicializado ( " + servidor + " ).\n");
    
        
        Socket jogador1;
        System.out.println("Esperando por conexao (Jogador 1)");
        jogador1 = servidor.accept();
        System.out.println( "Conexão Recebida: " + jogador1.toString() + ":" + jogador1.getPort() + "\n" );
        
        ObjectOutputStream entradaJogador1;
        entradaJogador1 = new ObjectOutputStream(jogador1.getOutputStream());
        entradaJogador1.flush();
        entradaJogador1.writeObject("jogador1:true");
        
        ObjectInputStream saidaJogador1;
        saidaJogador1 = new ObjectInputStream(jogador1.getInputStream());
        
        
        
        Socket jogador2;
        System.out.println( "Esperando por Conexão (jogador2)." );
        jogador2 =  servidor.accept();
        System.out.println( "Conexão Recebida: " + jogador2.toString() + ":" + jogador2.getPort() + "\n" );
        
        ObjectOutputStream entradaJogador2;
        entradaJogador2 = new ObjectOutputStream( jogador2.getOutputStream() );
        entradaJogador2.flush();        
        entradaJogador2.writeObject("jogador2:false");
        
        ObjectInputStream saidaJogador2;
        saidaJogador2 = new ObjectInputStream( jogador2.getInputStream() );
        
        
        
        int modoJogo = new Random().nextInt(4);
        
        
        entradaJogador1.writeObject("MODO:" + modoJogo);
        entradaJogador2.writeObject("MODO:" + modoJogo);

        
        String msg1 = saidaJogador1.readObject().toString();
        String msg2 = saidaJogador2.readObject().toString();
        System.out.println("o que era par ser a msg jogar novamente msg1: "+ msg1);
        System.out.println("o que era par ser a msg jogar novamente msg2: "+ msg2);
        
        if(msg1.equals("NOVO_JOGO") && msg2.equals("NOVO_JOGO")){
            modoJogo = new Random().nextInt(4);
                
            entradaJogador1.writeObject("MODO:" + modoJogo);
            entradaJogador2.writeObject("MODO:" + modoJogo);
        }    
        

        Thread thJo1 = new Thread(new GerenciadorJogadas(saidaJogador1, entradaJogador2));
        Thread thJo2 = new Thread(new GerenciadorJogadas(saidaJogador2, entradaJogador1));
        
        
        thJo1.start();
        thJo2.start();
        
        
        
    
    }
}