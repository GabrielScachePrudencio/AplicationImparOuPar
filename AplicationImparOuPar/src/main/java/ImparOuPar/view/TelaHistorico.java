package ImparOuPar.view;

import ImparOuPar.model.Jogada;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TelaHistorico extends JFrame {
    private JLabel titulo;
    private JTextArea historico;
    private JButton voltar;
    private JScrollPane scrollPane;

    private Socket servidorConexao;
    private ObjectInputStream servidorEntrada;
    private ObjectOutputStream servidorSaida;
    private boolean suaVez;

    
    public TelaHistorico(Socket socket, ObjectInputStream entrada, ObjectOutputStream saida, boolean suaVez) {
        this.servidorConexao = socket;
        this.servidorEntrada = entrada;
        this.servidorSaida = saida;
        this.suaVez = suaVez;
        
        carregar();
    }

    
    public TelaHistorico() {
        carregar();
    }

    private void carregar(){
        setTitle("Histórico de Jogadas");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null); // Centraliza a janela

        titulo = new JLabel("📜 Histórico de Campeões");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setBounds(110, 10, 250, 30);
        add(titulo);

        historico = new JTextArea();
        historico.setEditable(false);
        historico.setFont(new Font("Consolas", Font.PLAIN, 14));
        historico.setLineWrap(true);
        historico.setWrapStyleWord(true);

        scrollPane = new JScrollPane(historico);
        scrollPane.setBounds(30, 50, 380, 250);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);

        voltar = new JButton("🔙 Voltar");
        voltar.setBounds(150, 310, 130, 30);
        add(voltar);

        // Carrega os dados
        historico.setText(getDados());

        voltar.addActionListener((ActionEvent e) -> {
            dispose();
            TelaInicial tela = new TelaInicial(servidorConexao, servidorEntrada, servidorSaida, suaVez);
            tela.setVisible(true);
        });

        setVisible(true);
    }
    
    public String getDados() {
        File f = new File("src/main/java/ImparOuPar/data/historico.json");
        StringBuilder sb = new StringBuilder();

        if (!f.exists()) {
            try {
                File pasta = f.getParentFile();
                if (!pasta.exists()) {
                    pasta.mkdirs();
                }
                f.createNewFile();
                return "Nenhum histórico encontrado.";
            } catch (Exception e) {
                e.printStackTrace();
                return "Erro ao criar arquivo de histórico.";
            }
        }

        try (Scanner sc = new Scanner(f, StandardCharsets.UTF_8.name())) {
            while (sc.hasNextLine()) {
                String linha = sc.nextLine().trim();
                if (!linha.isEmpty()) {
                    String modo = extrairValor(linha, "Modo_Jogo");
                    String nome = extrairValor(linha, "nome vencedor");
                    String escolha = extrairValor(linha, "escolha vencedor");
                    String valor = extrairValor(linha, "valor");
                    int valor2 = Integer.parseInt(valor);

                    sb.append("Modo: ").append(modo).append("\n");
                    sb.append("Jogador: ").append(nome).append("\n");
                    sb.append("Escolha: ").append(escolha).append("\n");
                    sb.append("Valor: ").append(valor2).append("\n");
                    sb.append("──────────────────────────────\n\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao ler histórico.";
        }

        return sb.length() == 0 ? "Nenhum histórico encontrado." : sb.toString();
    }

    private String extrairValor(String linha, String chave) {
        String procura = "\"" + chave + "\":\"";
        int start = linha.indexOf(procura);
        if (start == -1) return "";
        start += procura.length();
        int end = linha.indexOf("\"", start);
        if (end == -1) return "";
        return linha.substring(start, end);
    }
}