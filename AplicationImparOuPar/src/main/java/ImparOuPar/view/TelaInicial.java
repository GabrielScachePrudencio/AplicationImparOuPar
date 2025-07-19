// erro o jogo nao funciona de novo pq ele tenta pegar um aleatorio falha e colocar como nulo o modo de jogo 
//* nao funciona o jogar novamente acho  q modo nao gera um aleatorio
//* fazer com q o historico esteja so com um unico pq ta repetindo
// ver se esta o jeito certo de implementar os butoes de cada classe do view ver se nao tem q estar em um arquivo separado 
// ver se esta certo a parte impletar o run
// ver se ele pode ser entregue como um pacote ou se para entregar tem q estar dendro de um aplicação java
package ImparOuPar.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class TelaInicial extends JFrame {

    private JLabel titulo;
    private JButton botaoJogar;
    private JButton botaoConectar;
    private JLabel imprimindoServidor;
    private JButton botaoHistorico;
    private JButton botaoAvancar;
    private Sons sons;

    private Socket servidorConexao;
    private ObjectInputStream servidorEntrada;
    private ObjectOutputStream servidorSaida;
    private boolean suaVez;

    private static boolean jaConectou = false;

    public boolean isSuaVez() {
        return suaVez;
    }

    public void setSuaVez(boolean suaVez) {
        this.suaVez = suaVez;
    }

    public static boolean isJaConectou() {
        return jaConectou;
    }

    public static void setJaConectou(boolean valor) {
        jaConectou = valor;
    }

    public TelaInicial() {

        try {
            sons = new Sons("./sounds/comecar.wav", "./sounds/mover.wav", "./sounds/errar.wav", "./sounds/ganhar.wav", "./sounds/perder.wav");
            
            System.out.println("IP carregado: " + ImparOuPar.network.Config.getIp());
            System.out.println("Porta carregada: " + ImparOuPar.network.Config.getPorta());
            setTitle("Bem vindo ao jogo Par ou Impar Multiplayer");
            setSize(400, 600); // tamanho aumentado
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(null);
            
            sons.comecar();
            titulo = new JLabel("Par ou Impar");
            botaoJogar = new JButton("JOGAR");
            botaoConectar = new JButton("Conectar");
            botaoHistorico = new JButton("Histórico");
            imprimindoServidor = new JLabel("");
            botaoAvancar = new JButton("Avançar");
            
            // Titulo
            titulo.setFont(new Font("Verdana", Font.BOLD, 20));
            titulo.setForeground(new Color(33, 33, 33));
            titulo.setBounds(125, 1, 150, 40);
            
            // boatao jogar
            botaoJogar.setFont(new Font("Verdana", Font.BOLD, 20));
            botaoJogar.setForeground(Color.WHITE);
            botaoJogar.setBackground(new Color(33, 150, 243));
            
            botaoJogar.setBounds(125, 180, 150, 40);
            // boatao conectar
            botaoConectar.setFont(new Font("Verdana", Font.BOLD, 20));
            botaoConectar.setBackground(new Color(33, 150, 243));
            botaoConectar.setForeground(Color.WHITE);
            botaoConectar.setBounds(125, 40 + 60, 150, 40);
            
            // boatao historico
            botaoHistorico.setFont(new Font("Verdana", Font.BOLD, 20));
            botaoHistorico.setBackground(new Color(33, 150, 243));
            botaoHistorico.setForeground(Color.WHITE);
            botaoHistorico.setBounds(125, 40 + 60 * 2, 150, 40);
            
            //imprimindoServidor
            imprimindoServidor.setFont(new Font("SansSerif", Font.PLAIN, 16));
            imprimindoServidor.setForeground(new Color(33, 33, 33));
            imprimindoServidor.setBounds(125 - 20, 40 + 60 * 3, 250, 40);
            
            // boatao avancar
            botaoAvancar.setFont(new Font("Verdana", Font.BOLD, 20));
            botaoAvancar.setBackground(new Color(33, 150, 243));
            botaoAvancar.setForeground(Color.WHITE);
            botaoAvancar.setBounds(125, 40 + 60 * 4, 150, 40);
            
            add(titulo);
            add(botaoJogar);
            
            botaoJogar.addActionListener(e -> {
                remove(botaoJogar);
                add(botaoConectar);
                add(imprimindoServidor);
                add(botaoHistorico);
                
                if (isJaConectou()) {
                    botaoConectar.setText("Já conectado");
                    botaoConectar.setEnabled(false);
                    add(botaoAvancar);
                } else {
                    add(botaoConectar);
                }
                
                repaint();
            });
            
            botaoConectar.addActionListener(e -> {
                
                new Thread(() -> {
                    try {
                        conectar();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        imprimindoServidor.setText("Erro na conexão");
                    }
                }).start();
                remove(botaoConectar);
                remove(botaoHistorico);
            });
            
            botaoHistorico.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        dispose(); // Fecha tela inicial
                        TelaHistorico tela = new TelaHistorico();
                        tela.setVisible(true);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Erro ao iniciar o jogo: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                
            });
            
            botaoAvancar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        dispose(); // Fecha tela inicial
                        TelaDefinicao tela = new TelaDefinicao(servidorConexao, servidorEntrada, servidorSaida, suaVez);
                        tela.setVisible(true);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Erro ao iniciar o jogo: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(TelaInicial.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void conectar() throws Exception {
        imprimindoServidor.setText("Buscando Conexao...");
        Thread.sleep(1000);

        servidorConexao = new Socket(InetAddress.getByName(ImparOuPar.network.Config.getIp()), ImparOuPar.network.Config.getPorta());

        imprimindoServidor.setText("Enviando dados ao outro jogador...");

        servidorSaida = new ObjectOutputStream(servidorConexao.getOutputStream());
        servidorSaida.flush();
        Thread.sleep(1000);

        imprimindoServidor.setText("Recebendo dados do outro jogador...");

        servidorEntrada = new ObjectInputStream(servidorConexao.getInputStream());
        Thread.sleep(1000);

        imprimindoServidor.setText("Conexao Estabelecida!");

        String msg = (String) servidorEntrada.readObject();
        String[] info = msg.split(":");
        setSuaVez(info[1].equals("true"));

        setJaConectou(true);

        Thread.sleep(1000);
        add(botaoAvancar);
        repaint(); // Atualiza tela pra mostrar botão
    }
}
