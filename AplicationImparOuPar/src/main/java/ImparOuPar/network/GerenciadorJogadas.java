package ImparOuPar.network;

import ImparOuPar.model.Jogada;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GerenciadorJogadas implements Runnable{
    private ObjectInputStream saidaJogador1;
    private ObjectOutputStream entradaJogador2;

    public GerenciadorJogadas(ObjectInputStream saidaJogador1, ObjectOutputStream entradaJogador2) {
        this.saidaJogador1 = saidaJogador1;
        this.entradaJogador2 = entradaJogador2;
    }
    
    public void run(){
        while(true){
            try{
                Jogada msg = (Jogada) saidaJogador1.readObject();
                entradaJogador2.writeObject(msg);
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }   
}