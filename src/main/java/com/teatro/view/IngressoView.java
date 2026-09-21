package com.teatro.view;

import com.teatro.model.Cliente;
import com.teatro.model.Ingresso;
import com.teatro.model.Sessao;
import com.teatro.service.ClienteService;
import com.teatro.service.IngressoService;
import com.teatro.service.SessaoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class IngressoView extends JFrame {

    private final IngressoService ingressoService;
    private final ClienteService clienteService;
    private final SessaoService sessaoService;
    private final MenuPrincipalView menuPrincipalView;

    private JComboBox<Cliente> comboCliente;
    private JComboBox<Sessao> comboSessao;
    private JTextField campoQuantidade;
    private JTextField campoValorIngresso;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private Ingresso ingressoSelecionado;

    public IngressoView(
            MenuPrincipalView menuPrincipalView
    ) {

        this.menuPrincipalView = menuPrincipalView;

        ingressoService = new IngressoService();
        clienteService = new ClienteService();
        sessaoService = new SessaoService();

        setTitle("Teatro Sapeense - Ingressos");
        setSize(1050, 650);
        setDefaultCloseOperation(
                JFrame.DO_NOTHING_ON_CLOSE
        );
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(
                            WindowEvent e
                    ) {
                        voltarAoMenu();
                    }
                }
        );

        criarInterface();
        carregarClientes();
        carregarSessoes();
        carregarIngressos();
    }

    private void criarInterface() {

        JPanel painelPrincipal =
                new JPanel(
                        new BorderLayout(10, 10)
                );

        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );

        JLabel titulo =
                new JLabel(
                        "Venda de Ingressos",
                        SwingConstants.CENTER
                );

        titulo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        painelPrincipal.add(
                titulo,
                BorderLayout.NORTH
        );

        JPanel painelFormulario =
                new JPanel(
                        new GridBagLayout()
                );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(5, 5, 5, 5);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1;

        comboCliente =
                new JComboBox<>();

        comboSessao =
                new JComboBox<>();

        campoQuantidade =
                new JTextField();

        campoValorIngresso =
                new JTextField();

        adicionarCampo(
                painelFormulario,
                gbc,
                0,
                "Cliente:",
                comboCliente
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                1,
                "Sessão:",
                comboSessao
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                2,
                "Quantidade:",
                campoQuantidade
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                3,
                "Valor do ingresso:",
                campoValorIngresso
        );

        JButton botaoVender =
                new JButton("Vender");

        JButton botaoExcluir =
                new JButton("Excluir");

        JButton botaoLimpar =
                new JButton("Limpar");

        JButton botaoVoltar =
                new JButton("Voltar ao Menu");

        JPanel painelBotoes =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER
                        )
                );

        painelBotoes.add(botaoVender);
        painelBotoes.add(botaoExcluir);
        painelBotoes.add(botaoLimpar);
        painelBotoes.add(botaoVoltar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;

        painelFormulario.add(
                painelBotoes,
                gbc
        );

        painelPrincipal.add(
                painelFormulario,
                BorderLayout.NORTH
        );

        modeloTabela =
                new DefaultTableModel(
                        new Object[]{
                                "ID",
                                "Cliente",
                                "Peça",
                                "Data",
                                "Horário",
                                "Quantidade",
                                "Valor Total",
                                "Venda"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        tabela =
                new JTable(modeloTabela);

        tabela.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        painelPrincipal.add(
                new JScrollPane(tabela),
                BorderLayout.CENTER
        );

        add(painelPrincipal);

        botaoVender.addActionListener(
                e -> vender()
        );

        botaoExcluir.addActionListener(
                e -> excluir()
        );

        botaoLimpar.addActionListener(
                e -> limparCampos()
        );

        botaoVoltar.addActionListener(
                e -> voltarAoMenu()
        );

        tabela.getSelectionModel()
                .addListSelectionListener(
                        e -> selecionarIngresso()
                );
    }

    private void adicionarCampo(
            JPanel painel,
            GridBagConstraints gbc,
            int linha,
            String texto,
            JComponent componente
    ) {

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = linha;

        painel.add(
                new JLabel(texto),
                gbc
        );

        gbc.gridx = 1;

        painel.add(
                componente,
                gbc
        );
    }

    private void carregarClientes() {

        comboCliente.removeAllItems();

        List<Cliente> clientes =
                clienteService.listarTodos();

        for (Cliente cliente : clientes) {
            comboCliente.addItem(cliente);
        }
    }

    private void carregarSessoes() {

        comboSessao.removeAllItems();

        List<Sessao> sessoes =
                sessaoService.listarTodos();

        for (Sessao sessao : sessoes) {
            comboSessao.addItem(sessao);
        }
    }

    private void vender() {

        try {

            Cliente cliente =
                    (Cliente)
                            comboCliente
                                    .getSelectedItem();

            Sessao sessao =
                    (Sessao)
                            comboSessao
                                    .getSelectedItem();

            if (cliente == null) {

                throw new IllegalArgumentException(
                        "Selecione um cliente."
                );
            }

            if (sessao == null) {

                throw new IllegalArgumentException(
                        "Selecione uma sessão."
                );
            }

            int quantidade;

            try {

                quantidade =
                        Integer.parseInt(
                                campoQuantidade
                                        .getText()
                                        .trim()
                        );

            } catch (NumberFormatException e) {

                throw new IllegalArgumentException(
                        "A quantidade deve ser um número inteiro."
                );
            }

            BigDecimal valor;

            try {

                valor =
                        new BigDecimal(
                                campoValorIngresso
                                        .getText()
                                        .trim()
                                        .replace(",", ".")
                        );

            } catch (NumberFormatException e) {

                throw new IllegalArgumentException(
                        "Informe um valor de ingresso válido."
                );
            }

            ingressoService.vender(
                    cliente,
                    sessao,
                    quantidade,
                    valor
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Ingresso vendido com sucesso!"
            );

            limparCampos();
            carregarIngressos();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void excluir() {

        if (ingressoSelecionado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um ingresso."
            );

            return;
        }

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente excluir este ingresso?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION
                );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            ingressoService.remover(
                    ingressoSelecionado
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Ingresso excluído com sucesso!"
            );

            limparCampos();
            carregarIngressos();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível excluir o ingresso:\n"
                            + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void selecionarIngresso() {

        int linha =
                tabela.getSelectedRow();

        if (linha < 0) {
            return;
        }

        Long id =
                (Long) modeloTabela.getValueAt(
                        linha,
                        0
                );

        ingressoSelecionado =
                ingressoService.buscarPorId(id);
    }

    private void carregarIngressos() {

        modeloTabela.setRowCount(0);

        List<Ingresso> ingressos =
                ingressoService.listarTodos();

        DateTimeFormatter formatoHora =
                DateTimeFormatter.ofPattern(
                        "HH:mm"
                );

        DateTimeFormatter formatoDataHora =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                );

        for (Ingresso ingresso : ingressos) {

            modeloTabela.addRow(
                    new Object[]{
                            ingresso.getId(),
                            ingresso.getCliente()
                                    .getNomeCompleto(),
                            ingresso.getSessao()
                                    .getPeca()
                                    .getTitulo(),
                            ingresso.getSessao()
                                    .getData(),
                            ingresso.getSessao()
                                    .getHorarioInicio()
                                    .format(formatoHora),
                            ingresso.getQuantidade(),
                            ingresso.getValorTotal(),
                            ingresso.getDataVenda()
                                    .format(formatoDataHora)
                    }
            );
        }
    }

    private void limparCampos() {

        if (comboCliente.getItemCount() > 0) {
            comboCliente.setSelectedIndex(0);
        }

        if (comboSessao.getItemCount() > 0) {
            comboSessao.setSelectedIndex(0);
        }

        campoQuantidade.setText("");
        campoValorIngresso.setText("");

        ingressoSelecionado = null;

        tabela.clearSelection();

        campoQuantidade.requestFocus();
    }

    private void voltarAoMenu() {

        dispose();

        menuPrincipalView.setVisible(true);
    }
}