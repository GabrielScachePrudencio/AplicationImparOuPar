package ImparOuPar.view;

import ImparOuPar.controller.Regras;
import ImparOuPar.model.Jogada;
import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TelaDefinicao extends JFrame {

    private JLabel titulo, nome, escolha, valor, modoDeJogo;
    private JTextField campoNome, campoValor;
    private JComboBox<String> comboEscolha, comboModoDeJogo;
    private JButton botaoDisputar;
    private JButton voltar;

    private Jogada jogadaLocal, jogadaRecebida, jogadaVencedora;
    private String nomeDoModo = "";
    private int resultadoConta = 0;
    private Regras regras = new Regras();

    private Socket servidorConexao;
    private ObjectInputStream servidorEntrada;
    private ObjectOutputStream servidorSaida;
    private boolean suaVez;

    public TelaDefinicao(Socket socket, ObjectInputStream entrada, ObjectOutputStream saida, boolean suaVez) {
        this.servidorConexao = socket;
        this.servidorEntrada = entrada;
        this.servidorSaida = saida;
        this.suaVez = suaVez;
        setupUI();
    }

    private void setupUI() {
        setTitle("Definição de Jogada");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // Cores e fontes padrão
        Color azul = new Color(33, 150, 243);
        Color textoEscuro = new Color(33, 33, 33);
        Font fonteTitulo = new Font("Verdana", Font.BOLD, 20);
        Font fonteTexto = new Font("SansSerif", Font.PLAIN, 16);
        Font fonteBotao = new Font("Verdana", Font.BOLD, 20);

        // Título
        titulo = new JLabel("Defina sua jogada", SwingConstants.CENTER);
        titulo.setFont(fonteTitulo);
        titulo.setForeground(textoEscuro);
        titulo.setBounds(50, 10, 300, 25);

        // Nome
        nome = new JLabel("Nome:");
        nome.setFont(fonteTexto);
        nome.setForeground(textoEscuro);
        nome.setBounds(50, 50, 300, 25);

        campoNome = new JTextField();
        campoNome.setBounds(50, 75, 300, 25);

        // Escolha
        escolha = new JLabel("Escolha (Impar ou Par):");
        escolha.setFont(fonteTexto);
        escolha.setForeground(textoEscuro);
        escolha.setBounds(50, 110, 300, 25);

        comboEscolha = new JComboBox<>(new String[]{"Impar", "Par"});
        comboEscolha.setBounds(50, 135, 300, 25);

        // Modo de jogo
        modoDeJogo = new JLabel("Escolha seu modo de Jogo:");
        modoDeJogo.setFont(fonteTexto);
        modoDeJogo.setForeground(textoEscuro);
        modoDeJogo.setBounds(50, 170, 300, 25);

        comboModoDeJogo = new JComboBox<>(new String[]{"Multiplicacao", "Subtracao", "Divisao", "Soma"});
        comboModoDeJogo.setBounds(50, 195, 300, 25);

        // Valor
        valor = new JLabel("Número a jogar:");
        valor.setFont(fonteTexto);
        valor.setForeground(textoEscuro);
        valor.setBounds(50, 230, 300, 25);

        campoValor = new JTextField();
        campoValor.setBounds(50, 255, 300, 25);

        // Botão Disputar
        botaoDisputar = new JButton("Disputar");
        botaoDisputar.setFont(fonteBotao);
        botaoDisputar.setForeground(Color.WHITE);
        botaoDisputar.setBackground(azul);
        botaoDisputar.setBounds(100, 300, 200, 40);

        // Botão Voltar
        voltar = new JButton("Voltar");
        voltar.setFont(fonteBotao);
        voltar.setForeground(Color.WHITE);
        voltar.setBackground(azul);
        voltar.setBounds(100, 350, 200, 40);
        
        add(titulo);
        add(nome);
        add(campoNome);
        add(escolha);
        add(comboEscolha);
        add(modoDeJogo);
        add(comboModoDeJogo);
        add(valor);
        add(campoValor);
        add(botaoDisputar);
        add(voltar);

        botaoDisputar.addActionListener(e -> processarJogada());
        voltar.addActionListener((ActionEvent e) -> {
            dispose();
            TelaInicial tela = new TelaInicial(servidorConexao, servidorEntrada, servidorSaida, suaVez);
            tela.setVisible(true);
        });
    }

    private void processarJogada() {
        System.out.println(">>> processarJogada chamado"); // DEBUG
        String nome = campoNome.getText().trim();
        String escolha = comboEscolha.getSelectedItem().toString();
        String valorStr = campoValor.getText().trim();
        String modoDeJogo = comboModoDeJogo.getSelectedItem().toString();

        if (nome.isEmpty() || valorStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        int valor;
        try {
            valor = Integer.parseInt(valorStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um número válido.");
            return;
        }

        try {
            jogadaLocal = new Jogada(nome, valor, escolha, modoDeJogo);
            System.out.println(">>> Jogada Local criada: " + jogadaLocal); // DEBUG

            servidorSaida.writeObject("NOVO_JOGO");
            servidorSaida.flush();

            if (suaVez) {
                servidorSaida.writeObject(jogadaLocal);
                servidorSaida.flush();
                Object recebido = aguardarJogada();
                if (recebido == null) {
                    JOptionPane.showMessageDialog(this, "Erro ao receber jogada do outro jogador.");
                    return;
                }
                jogadaRecebida = (Jogada) recebido;
            } else {
                Object recebido = aguardarJogada();
                if (recebido == null) {
                    JOptionPane.showMessageDialog(this, "Erro ao receber jogada do outro jogador.");
                    return;
                }
                jogadaRecebida = (Jogada) recebido;
                servidorSaida.writeObject(jogadaLocal);
                servidorSaida.flush();
            }

            if (jogadaRecebida.getEscolha().equalsIgnoreCase(jogadaLocal.getEscolha())) {
                JOptionPane.showMessageDialog(this, "Ambos escolheram " + jogadaRecebida.getEscolha() + ". Refazendo jogada.");
                return;
            }

            if (!jogadaRecebida.getModoJogo().equalsIgnoreCase(jogadaLocal.getModoJogo())) {
                JOptionPane.showMessageDialog(this, "Modos de jogo diferentes escolhidos. Ambos devem escolher o mesmo modo. Refaça a jogada.");
                return;
            }

            verificarVencedor();

            dispose();
            TelaResultado resultado = new TelaResultado(jogadaLocal, jogadaRecebida, jogadaVencedora, resultadoConta, nomeDoModo, servidorConexao, servidorEntrada, servidorSaida, suaVez);
            resultado.setVisible(true);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro na comunicação com o servidor: " + ex.getMessage());
            dispose();
            new TelaInicial().setVisible(true);
        }
    }

    private Object aguardarJogada() {
        Object recebido = null;
        boolean recebimentoOk = false;

        while (!recebimentoOk) {
            try {
                recebido = servidorEntrada.readObject();
                if (recebido instanceof Jogada) {
                    recebimentoOk = true;
                } else {
                    Thread.sleep(500);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        return recebido;
    }

    private void verificarVencedor() {
        String modo = jogadaLocal.getModoJogo().toLowerCase();
        Jogada jogador1 = jogadaLocal.getNome().compareTo(jogadaRecebida.getNome()) < 0 ? jogadaLocal : jogadaRecebida;
        Jogada jogador2 = jogador1 == jogadaLocal ? jogadaRecebida : jogadaLocal;

        switch (modo) {
            case "soma":
                nomeDoModo = "Soma";
                jogadaVencedora = regras.DisputaPorSoma(jogadaLocal, jogadaRecebida);
                resultadoConta = jogador1.getValor() + jogador2.getValor();
                break;
            case "subtracao":
                nomeDoModo = "Subtração";
                jogadaVencedora = regras.DisputaPorSubtracao(jogadaLocal, jogadaRecebida);
                resultadoConta = jogador1.getValor() - jogador2.getValor();
                break;
            case "divisao":
                nomeDoModo = "Divisão";
                jogadaVencedora = regras.DisputaPorDivisao(jogadaLocal, jogadaRecebida);
                int maior = Math.max(jogador1.getValor(), jogador2.getValor());
                int menor = Math.min(jogador1.getValor(), jogador2.getValor());
                resultadoConta = menor != 0 ? maior / menor : 0;
                if(resultadoConta == 0){
                    jogadaVencedora.setEscolha("Par");
                }
                break;
            case "multiplicacao":
                nomeDoModo = "Multiplicação";
                jogadaVencedora = regras.DisputaPorMultiplicacao(jogadaLocal, jogadaRecebida);
                resultadoConta = jogador1.getValor() * jogador2.getValor();
                break;
            default:
                nomeDoModo = "Soma";
                jogadaVencedora = regras.DisputaPorSoma(jogadaLocal, jogadaRecebida);
                resultadoConta = jogador1.getValor() + jogador2.getValor();
                break;
        }
    }
}
