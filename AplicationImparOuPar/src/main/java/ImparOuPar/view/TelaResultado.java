package ImparOuPar.view;

import ImparOuPar.model.Jogada;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

    private JLabel titulo;
    private JLabel nome1;
    private JLabel nome2;
    private JLabel nome3; //resutlado
    private JLabel escolha1;
    private JLabel escolha2;
    private JLabel escolha3;// resultado
    private JLabel valor1;
    private JLabel valor2;
    private JLabel valor3;// resultado
    private JLabel valor4;// resultado soma
    private JLabel resultadoDoVencedor;
    private JButton voltar;

    public TelaResultado(Jogada jogadaLocal, Jogada jogadaRecebida, Jogada jogadaVencedora, int resultadoConta, String nomeDoModo, Socket servidorConexao, ObjectInputStream servidorEntrada, ObjectOutputStream servidorSaida, boolean suaVez) {
        setTitle("Sistema Principal");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        this.jogadaLocal = jogadaLocal;
        this.jogadaRecebida = jogadaRecebida;
        this.jogadaVencedora = jogadaVencedora;

        this.servidorConexao = servidorConexao;
        this.servidorEntrada = servidorEntrada;
        this.servidorSaida = servidorSaida;
        this.suaVez = suaVez;
        this.nomeDoModo = nomeDoModo;

        // Verifica se o vencedor é o jogadaRecebida (e não o local)
        // so para criar uma ordem na tabela pois isso afeta a logica do jogo
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
            {"Valor: ", valor1, valor2, resultadoConta},};

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
       
        JLabel titulo = new JLabel("Resultado da Partida", SwingConstants.CENTER);

        // Layout vertical
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(titulo);
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
                ex.printStackTrace(); // mostra o erro no console
            }
        });

    }

    public void salvarDados() {
        try {

            FileWriter fw = new FileWriter("src/ImparOuPar/data/historico.json", StandardCharsets.UTF_8, true);

            PrintWriter pw = new PrintWriter(fw);

            String json = "{"
                    + "\"Modo_Jogo\":\"" + nomeDoModo + "\","
                    + "\"nome\":\"" + jogadaVencedora.getNome() + "\","
                    + "\"escolha\":\"" + jogadaVencedora.getEscolha() + "\","
                    + "\"valor\":\"" + jogadaVencedora.getValor() + "\""
                    + "}";

            pw.println(json);
            pw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
