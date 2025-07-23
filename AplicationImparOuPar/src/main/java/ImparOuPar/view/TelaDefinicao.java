// tem que estilizar
/*
package ImparOuPar.view;

import ImparOuPar.controller.Regras;
import ImparOuPar.model.Jogada;
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
    private int numDaEscolhaModoJogo = -1;
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

        titulo = new JLabel("Defina sua jogada", SwingConstants.CENTER);
        titulo.setBounds(50, 10, 300, 25);

        nome = new JLabel("Nome:");
        nome.setBounds(50, 50, 300, 25);
        campoNome = new JTextField();
        campoNome.setBounds(50, 75, 300, 25);

        escolha = new JLabel("Escolha (Impar ou Par):");
        escolha.setBounds(50, 110, 300, 25);
        comboEscolha = new JComboBox<>(new String[]{"Impar", "Par"});
        comboEscolha.setBounds(50, 135, 300, 25);

        modoDeJogo = new JLabel("Escolha seu modo de Jogo:");
        modoDeJogo.setBounds(50, 170, 300, 25);
        comboModoDeJogo = new JComboBox<>(new String[]{"Multiplicacao", "Subtracao", "Divisao", "Soma"});
        comboModoDeJogo.setBounds(50, 195, 300, 25);

        valor = new JLabel("Número a jogar:");
        valor.setBounds(50, 230, 300, 25);
        campoValor = new JTextField();
        campoValor.setBounds(50, 255, 300, 25);

        botaoDisputar = new JButton("Disputar");
        botaoDisputar.setBounds(100, 300, 200, 30);

        voltar = new JButton("Voltar");
        voltar.setBounds(100, 350, 200, 30);

        // Adiciona os componentes na tela
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

        // Eventos
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
            jogadaLocal = new Jogada(nome, valor, escolha,modoDeJogo);
            System.out.println(">>> Jogada Local criada: " + jogadaLocal); // DEBUG

            System.out.println(">>> Enviando mensagem 'NOVO_JOGO'"); // DEBUG
            servidorSaida.writeObject("NOVO_JOGO");
            servidorSaida.flush();

            if (suaVez) {
                System.out.println(">>> Sua vez - enviando jogadaLocal"); // DEBUG
                servidorSaida.writeObject(jogadaLocal);
                servidorSaida.flush();

                System.out.println(">>> Aguardando jogada do outro jogador..."); // DEBUG
                Object recebido = aguardarJogada();

                if (recebido == null) {
                    System.out.println(">>> ERRO: Recebido null na sua vez"); // DEBUG
                    JOptionPane.showMessageDialog(this, "Erro ao receber jogada do outro jogador.");
                    return;
                }

                jogadaRecebida = (Jogada) recebido;
                System.out.println(">>> Jogada Recebida (sua vez): " + jogadaRecebida); // DEBUG

            } else {
                System.out.println(">>> Não é sua vez - aguardando jogada do outro jogador..."); // DEBUG
                Object recebido = aguardarJogada();

                if (recebido == null) {
                    System.out.println(">>> ERRO: Recebido null (não sua vez)"); // DEBUG
                    JOptionPane.showMessageDialog(this, "Erro ao receber jogada do outro jogador.");
                    return;
                }

                jogadaRecebida = (Jogada) recebido;
                System.out.println(">>> Jogada Recebida (não sua vez): " + jogadaRecebida); // DEBUG

                System.out.println(">>> Enviando jogadaLocal depois de receber a jogada do outro jogador"); // DEBUG
                servidorSaida.writeObject(jogadaLocal);
                servidorSaida.flush();
            }

            if (jogadaRecebida.getEscolha().equalsIgnoreCase(jogadaLocal.getEscolha())) {
                System.out.println(">>> Empate de escolha detectado: " + jogadaRecebida.getEscolha()); // DEBUG
                JOptionPane.showMessageDialog(this, "Ambos escolheram " + jogadaRecebida.getEscolha() + ". Refazendo jogada.");
                return;
            }
            
            
            if (jogadaRecebida.getModoJogo().equalsIgnoreCase(jogadaLocal.getModoJogo())) {
                JOptionPane.showMessageDialog(this, "Ambos escolheram " + jogadaRecebida.getModoJogo() + ". Refazendo jogada.");
                return;
            }
            
            verificarVencedor();

            dispose();
            System.out.println(">>> Abrindo TelaResultado..."); // DEBUG
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
                System.out.println(">>> Tentando ler do servidorEntrada..."); // DEBUG
                recebido = servidorEntrada.readObject();
                System.out.println(">>> Objeto recebido: " + recebido); // DEBUG

                if (recebido instanceof String) {
                    String msg = (String) recebido;
                    System.out.println(">>> Mensagem recebida (String): " + msg); // DEBUG
                    if (msg.startsWith("MODO:") || msg.startsWith("NOVO_JOGO")) {
                        String[] partes = msg.split(":");
                        if (partes.length > 1) {
                            numDaEscolhaModoJogo = Integer.parseInt(partes[1]);
                            System.out.println(">>> Modo de jogo setado para: " + numDaEscolhaModoJogo); // DEBUG
                        }
                        recebido = servidorEntrada.readObject(); // pega jogada de verdade
                        System.out.println(">>> Segunda leitura (esperada Jogada): " + recebido); // DEBUG
                    }
                }

                if (recebido instanceof Jogada) {
                    recebimentoOk = true;
                    System.out.println(">>> Jogada recebida com sucesso."); // DEBUG
                } else {
                    System.out.println(">>> Aguardando jogada válida..."); // DEBUG
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
        switch (numDaEscolhaModoJogo) {
            case 0:
                nomeDoModo = "Soma";
                jogadaVencedora = regras.DisputaPorSoma(jogadaLocal, jogadaRecebida);
                resultadoConta = jogadaLocal.getValor() + jogadaRecebida.getValor();
                break;
            case 1:
                nomeDoModo = "Subtração";
                jogadaVencedora = regras.DisputaPorSubtracao(jogadaLocal, jogadaRecebida);
                resultadoConta = jogadaLocal.getValor() - jogadaRecebida.getValor();
                break;
            case 2:
                nomeDoModo = "Divisão";
                jogadaVencedora = regras.DisputaPorDivisao(jogadaLocal, jogadaRecebida);
                resultadoConta = jogadaLocal.getValor() / jogadaRecebida.getValor();
                break;
            case 3:
                nomeDoModo = "Multiplicação";
                jogadaVencedora = regras.DisputaPorMultiplicacao(jogadaLocal, jogadaRecebida);
                resultadoConta = jogadaLocal.getValor() * jogadaRecebida.getValor();
                break;
            default:
                nomeDoModo = "Soma";
                jogadaVencedora = regras.DisputaPorSoma(jogadaLocal, jogadaRecebida);
                resultadoConta = jogadaLocal.getValor() + jogadaRecebida.getValor();
        }
    }
}
*/

package ImparOuPar.view;

import ImparOuPar.controller.Regras;
import ImparOuPar.model.Jogada;
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

        titulo = new JLabel("Defina sua jogada", SwingConstants.CENTER);
        titulo.setBounds(50, 10, 300, 25);

        nome = new JLabel("Nome:");
        nome.setBounds(50, 50, 300, 25);
        campoNome = new JTextField();
        campoNome.setBounds(50, 75, 300, 25);

        escolha = new JLabel("Escolha (Impar ou Par):");
        escolha.setBounds(50, 110, 300, 25);
        comboEscolha = new JComboBox<>(new String[]{"Impar", "Par"});
        comboEscolha.setBounds(50, 135, 300, 25);

        modoDeJogo = new JLabel("Escolha seu modo de Jogo:");
        modoDeJogo.setBounds(50, 170, 300, 25);
        comboModoDeJogo = new JComboBox<>(new String[]{"Multiplicacao", "Subtracao", "Divisao", "Soma"});
        comboModoDeJogo.setBounds(50, 195, 300, 25);

        valor = new JLabel("Número a jogar:");
        valor.setBounds(50, 230, 300, 25);
        campoValor = new JTextField();
        campoValor.setBounds(50, 255, 300, 25);

        botaoDisputar = new JButton("Disputar");
        botaoDisputar.setBounds(100, 300, 200, 30);

        voltar = new JButton("Voltar");
        voltar.setBounds(100, 350, 200, 30);

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

        switch (modo) {
            case "soma":
                nomeDoModo = "Soma";
                jogadaVencedora = regras.DisputaPorSoma(jogadaLocal, jogadaRecebida);
                resultadoConta = jogadaLocal.getValor() + jogadaRecebida.getValor();
                break;
            case "subtracao":
                nomeDoModo = "Subtração";
                jogadaVencedora = regras.DisputaPorSubtracao(jogadaLocal, jogadaRecebida);
                resultadoConta = jogadaLocal.getValor() - jogadaRecebida.getValor();
                break;
            case "divisao":
                nomeDoModo = "Divisão";
                jogadaVencedora = regras.DisputaPorDivisao(jogadaLocal, jogadaRecebida);
                resultadoConta = jogadaLocal.getValor() / jogadaRecebida.getValor();
                break;
            case "multiplicacao":
                nomeDoModo = "Multiplicação";
                jogadaVencedora = regras.DisputaPorMultiplicacao(jogadaLocal, jogadaRecebida);
                resultadoConta = jogadaLocal.getValor() * jogadaRecebida.getValor();
                break;
            default:
                nomeDoModo = "Soma";
                jogadaVencedora = regras.DisputaPorSoma(jogadaLocal, jogadaRecebida);
                resultadoConta = jogadaLocal.getValor() + jogadaRecebida.getValor();
                break;
        }
    }
}