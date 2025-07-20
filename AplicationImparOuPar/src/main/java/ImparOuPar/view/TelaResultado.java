package ImparOuPar.view;

import ImparOuPar.model.Jogada;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TelaResultado extends JFrame {

    private String nomeDoModo;
    private Jogada jogadaLocal;
    private Jogada jogadaRecebida;
    private Jogada jogadaVencedora;

    private Socket servidorConexao;
    private ObjectInputStream servidorEntrada;
    private ObjectOutputStream servidorSaida;
    private boolean suaVez;

    private JLabel resultadoDoVencedor;
    private JButton voltar;
    private Sons sons;

    public TelaResultado(
            Jogada jogadaLocal, Jogada jogadaRecebida, Jogada jogadaVencedora,
            int resultadoConta, String nomeDoModo,
            Socket servidorConexao, ObjectInputStream servidorEntrada, ObjectOutputStream servidorSaida,
            boolean suaVez
    ) {
        sons = new Sons("./sounds/comecar.wav", "./sounds/ganhar.wav", "./sounds/perder.wav");

        this.jogadaLocal = jogadaLocal;
        this.jogadaRecebida = jogadaRecebida;
        this.jogadaVencedora = jogadaVencedora;
        this.servidorConexao = servidorConexao;
        this.servidorEntrada = servidorEntrada;
        this.servidorSaida = servidorSaida;
        this.suaVez = suaVez;
        this.nomeDoModo = nomeDoModo;

        setTitle("Resultado da Partida");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        boolean inverter = !jogadaVencedora.getNome().equals(jogadaLocal.getNome());

        String nome1 = inverter ? jogadaRecebida.getNome() : jogadaLocal.getNome();
        String nome2 = inverter ? jogadaLocal.getNome() : jogadaRecebida.getNome();

        String escolha1 = inverter ? jogadaRecebida.getEscolha() : jogadaLocal.getEscolha();
        String escolha2 = inverter ? jogadaLocal.getEscolha() : jogadaRecebida.getEscolha();

        int valor1 = inverter ? jogadaRecebida.getValor() : jogadaLocal.getValor();
        int valor2 = inverter ? jogadaLocal.getValor() : jogadaRecebida.getValor();

        String[] colunas = {"-", nome1, nome2, "Resultado"};
        Object[][] dados = {
            {"Escolha: ", escolha1, escolha2, jogadaVencedora.getEscolha()},
            {"Valor: ", valor1, valor2, resultadoConta}
        };

        JTable tabela = new JTable(dados, colunas);
        tabela.setFont(new Font("Verdana", Font.BOLD, 20));
        tabela.setRowHeight(30);
        tabela.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 20));
        tabela.setGridColor(Color.LIGHT_GRAY);
        tabela.setShowVerticalLines(false);
        tabela.setShowHorizontalLines(true);
        tabela.setEnabled(false);
        JScrollPane scroll = new JScrollPane(tabela);

        resultadoDoVencedor = new JLabel("Modo de Jogo: " + nomeDoModo + " | Vencedor: " + jogadaVencedora.getNome());
        resultadoDoVencedor.setFont(new Font("Verdana", Font.BOLD, 20));
        resultadoDoVencedor.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultadoDoVencedor.setBorder(new EmptyBorder(10, 10, 20, 10));

        voltar = new JButton("Jogar novamente");
        voltar.setFont(new Font("Verdana", Font.BOLD, 20));
        voltar.setBackground(new Color(33, 150, 243));
        voltar.setForeground(Color.WHITE);
        voltar.setFocusPainted(false);
        voltar.setAlignmentX(Component.CENTER_ALIGNMENT);

        boolean venceu = jogadaLocal.getNome().equals(jogadaVencedora.getNome());

        try {
            if (venceu) {
                sons.ganhar();
            } else {
                sons.perder();
            }
        } catch (Exception ex) {
            Logger.getLogger(TelaResultado.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Adiciona mensagem de vitória ou derrota
        mostrarResultadoPersonalizado(venceu);

        add(scroll);
        add(resultadoDoVencedor);
        add(voltar);

        salvarDados();

        voltar.addActionListener(e -> {
            try {
                dispose();
                TelaDefinicao teld = new TelaDefinicao(servidorConexao, servidorEntrada, servidorSaida, suaVez);
                teld.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void mostrarResultadoPersonalizado(boolean venceu) {
        String mensagem = venceu ? "VOCÊ GANHOU!" : "VOCÊ PERDEU!";
        Color cor = venceu ? new Color(0, 128, 0) : new Color(200, 0, 0);
        JLabel msg = new JLabel(mensagem, SwingConstants.CENTER);
        msg.setFont(new Font("Arial", Font.BOLD, 30));
        msg.setForeground(cor);
        JOptionPane.showMessageDialog(this, msg, "Resultado Final", JOptionPane.PLAIN_MESSAGE);
    }

    public void salvarDados() {
        // Imprimir no log quando metodo é chamado
        System.out.println(">>> Metodo salvarDados() chamado");

        String caminho = "C:\\Users\\Windows\\Desktop\\trabalho java imparoupar\\AplicationImparOuPares\\AplicationImparOuPar\\src\\main\\java\\ImparOuPar\\data\\historico.json";

        // Criar pasta se não existir
        File pasta = new File("C:\\Users\\Windows\\Desktop\\trabalho java imparoupar\\AplicationImparOuPares\\AplicationImparOuPar\\src\\main\\java\\ImparOuPar\\data");
        if (!pasta.exists()) {
            boolean criada = pasta.mkdirs();
            if (!criada) {
                System.out.println("Não foi possível criar a pasta!");
                return;
            }
        }

        String json = "{"
                + "\"Modo_Jogo\":\"" + nomeDoModo + "\","
                + "\"nome vencedor\":\"" + jogadaVencedora.getNome() + "\","
                + "\"escolha vencedor\":\"" + jogadaVencedora.getEscolha() + "\","
                + "\"valor\":\"" + jogadaVencedora.getValor() + "\""
                + "}";

        try (OutputStreamWriter osw = new OutputStreamWriter(
                new FileOutputStream(caminho, true), StandardCharsets.UTF_8); BufferedWriter bw = new BufferedWriter(osw); PrintWriter pw = new PrintWriter(bw)) {

            pw.println(json);
            System.out.println("Dados salvos com sucesso no arquivo: " + caminho);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
