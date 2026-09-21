package com.teatro.view;

import com.teatro.dto.PresencaDto;
import com.teatro.model.Peca;
import com.teatro.service.PecaService;
import com.teatro.service.RelatorioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RelatorioView extends JFrame {

    private final MenuPrincipalView menuPrincipalView;

    private final RelatorioService relatorioService;
    private final PecaService pecaService;

    private JComboBox<Peca> comboPeca;
    private JTextField campoData;

    private JTextField campoDataInicio;
    private JTextField campoDataFim;

    private JTable tabelaPresenca;
    private DefaultTableModel modeloTabela;

    private JLabel resultadoFinanceiro;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public RelatorioView(MenuPrincipalView menuPrincipalView) {

        this.menuPrincipalView = menuPrincipalView;

        relatorioService = new RelatorioService();
        pecaService = new PecaService();

        setTitle("Teatro Sapeense - Relatórios");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                voltarAoMenu();
            }
        });

        criarInterface();
        carregarPecas();
    }

    private void criarInterface() {

        JPanel painelPrincipal =
                new JPanel(new BorderLayout(10, 10));

        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );

        JLabel titulo =
                new JLabel(
                        "RELATÓRIOS",
                        SwingConstants.CENTER
                );

        titulo.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        painelPrincipal.add(
                titulo,
                BorderLayout.NORTH
        );

        JTabbedPane abas = new JTabbedPane();

        abas.addTab(
                "Lista de Presença",
                criarPainelPresenca()
        );

        abas.addTab(
                "Relatório Financeiro",
                criarPainelFinanceiro()
        );

        painelPrincipal.add(
                abas,
                BorderLayout.CENTER
        );

        JButton botaoVoltar =
                new JButton("Voltar ao Menu");

        botaoVoltar.addActionListener(
                e -> voltarAoMenu()
        );

        JPanel painelRodape =
                new JPanel(
                        new FlowLayout(FlowLayout.RIGHT)
                );

        painelRodape.add(botaoVoltar);

        painelPrincipal.add(
                painelRodape,
                BorderLayout.SOUTH
        );

        add(painelPrincipal);
    }

    // =========================================================
    // LISTA DE PRESENÇA
    // =========================================================

    private JPanel criarPainelPresenca() {

        JPanel painel =
                new JPanel(new BorderLayout(10, 10));

        JPanel filtros =
                new JPanel(new GridBagLayout());

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(5, 5, 5, 5);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;

        filtros.add(
                new JLabel("Peça:"),
                gbc
        );

        gbc.gridx = 1;

        comboPeca =
                new JComboBox<>();

        comboPeca.setPreferredSize(
                new Dimension(300, 25)
        );

        filtros.add(
                comboPeca,
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy = 1;

        filtros.add(
                new JLabel("Data (DD/MM/AAAA):"),
                gbc
        );

        gbc.gridx = 1;

        campoData =
                new JTextField();

        filtros.add(
                campoData,
                gbc
        );

        gbc.gridx = 1;
        gbc.gridy = 2;

        JButton botaoListar =
                new JButton("Listar Presença");

        filtros.add(
                botaoListar,
                gbc
        );

        painel.add(
                filtros,
                BorderLayout.NORTH
        );

        modeloTabela =
                new DefaultTableModel(
                        new Object[]{
                                "Cliente",
                                "Peça",
                                "Data",
                                "Horário",
                                "Quantidade",
                                "Valor Total"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {
                        return false;
                    }
                };

        tabelaPresenca =
                new JTable(modeloTabela);

        tabelaPresenca.setRowHeight(25);

        painel.add(
                new JScrollPane(tabelaPresenca),
                BorderLayout.CENTER
        );

        botaoListar.addActionListener(
                e -> listarPresenca()
        );

        return painel;
    }

    // =========================================================
    // RELATÓRIO FINANCEIRO
    // =========================================================

    private JPanel criarPainelFinanceiro() {

        JPanel painel =
                new JPanel(new GridBagLayout());

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(8, 8, 8, 8);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        // -----------------------------------------------------
        // PEÇA
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 0;

        painel.add(
                new JLabel("Peça:"),
                gbc
        );

        gbc.gridx = 1;

        JComboBox<Peca> comboPecaFinanceiro =
                new JComboBox<>();

        for (Peca peca : pecaService.listarTodos()) {
            comboPecaFinanceiro.addItem(peca);
        }

        comboPecaFinanceiro.setPreferredSize(
                new Dimension(300, 25)
        );

        painel.add(
                comboPecaFinanceiro,
                gbc
        );

        // -----------------------------------------------------
        // RELATÓRIO DA PEÇA
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 1;

        painel.add(
                new JLabel("Relatório da peça:"),
                gbc
        );

        gbc.gridx = 1;

        JButton botaoTotalPeca =
                new JButton("Calcular Relatório da Peça");

        painel.add(
                botaoTotalPeca,
                gbc
        );

        // -----------------------------------------------------
        // DATA INICIAL
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 2;

        painel.add(
                new JLabel("Data inicial (DD/MM/AAAA):"),
                gbc
        );

        gbc.gridx = 1;

        campoDataInicio =
                new JTextField();

        painel.add(
                campoDataInicio,
                gbc
        );

        // -----------------------------------------------------
        // DATA FINAL
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 3;

        painel.add(
                new JLabel("Data final (DD/MM/AAAA):"),
                gbc
        );

        gbc.gridx = 1;

        campoDataFim =
                new JTextField();

        painel.add(
                campoDataFim,
                gbc
        );

        // -----------------------------------------------------
        // RELATÓRIO DO TEATRO
        // -----------------------------------------------------

        gbc.gridx = 1;
        gbc.gridy = 4;

        JButton botaoTotalPeriodo =
                new JButton("Calcular Relatório do Período");

        painel.add(
                botaoTotalPeriodo,
                gbc
        );

        // -----------------------------------------------------
        // RESULTADO
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 5;

        gbc.gridwidth = 2;

        resultadoFinanceiro =
                new JLabel(
                        "<html><center>Resultado financeiro<br>R$ 0,00</center></html>",
                        SwingConstants.CENTER
                );

        resultadoFinanceiro.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        painel.add(
                resultadoFinanceiro,
                gbc
        );

        // =====================================================
        // BOTÃO - RELATÓRIO POR PEÇA
        // =====================================================

        botaoTotalPeca.addActionListener(e -> {

            try {

                Peca peca =
                        (Peca) comboPecaFinanceiro.getSelectedItem();

                if (peca == null) {
                    throw new IllegalArgumentException(
                            "Selecione uma peça."
                    );
                }

                BigDecimal receitaIngressos =
                        relatorioService
                                .calcularReceitaIngressosPorPeca(
                                        peca
                                );

                BigDecimal valorAluguel =
                        relatorioService
                                .calcularAluguelPorPeca(
                                        peca
                                );

                BigDecimal saldo =
                        relatorioService
                                .calcularSaldoPorPeca(
                                        peca
                                );

                mostrarRelatorioPeca(
                        peca,
                        receitaIngressos,
                        valorAluguel,
                        saldo
                );

            } catch (Exception ex) {

                mostrarErro(ex);
            }
        });

        // =====================================================
        // BOTÃO - RELATÓRIO POR PERÍODO
        // =====================================================

        botaoTotalPeriodo.addActionListener(e -> {

            try {

                LocalDate inicio =
                        LocalDate.parse(
                                campoDataInicio
                                        .getText()
                                        .trim(),
                                formatter
                        );

                LocalDate fim =
                        LocalDate.parse(
                                campoDataFim
                                        .getText()
                                        .trim(),
                                formatter
                        );

                BigDecimal receitaIngressos =
                        relatorioService
                                .calcularReceitaIngressosPorPeriodo(
                                        inicio,
                                        fim
                                );

                BigDecimal receitaContratos =
                        relatorioService
                                .calcularReceitaContratosPorPeriodo(
                                        inicio,
                                        fim
                                );

                BigDecimal total =
                        relatorioService
                                .calcularTotalPorPeriodo(
                                        inicio,
                                        fim
                                );

                mostrarRelatorioPeriodo(
                        inicio,
                        fim,
                        receitaIngressos,
                        receitaContratos,
                        total
                );

            } catch (Exception ex) {

                mostrarErro(ex);
            }
        });

        return painel;
    }

    // =========================================================
    // LISTAR PRESENÇA
    // =========================================================

    private void listarPresenca() {

        try {

            Peca peca =
                    (Peca) comboPeca.getSelectedItem();

            LocalDate data =
                    LocalDate.parse(
                            campoData.getText().trim(),
                            formatter
                    );

            List<PresencaDto> lista =
                    relatorioService.listarPresenca(
                            peca,
                            data
                    );

            modeloTabela.setRowCount(0);

            for (PresencaDto dto : lista) {

                modeloTabela.addRow(
                        new Object[]{
                                dto.getCliente(),
                                dto.getPeca(),
                                dto.getData().format(formatter),
                                dto.getHorario(),
                                dto.getQuantidade(),
                                "R$ " + dto.getValorTotal()
                        }
                );
            }

            if (lista.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Nenhum ingresso encontrado para a peça e data informadas."
                );
            }

        } catch (Exception ex) {

            mostrarErro(ex);
        }
    }

    // =========================================================
    // MOSTRAR RELATÓRIO FINANCEIRO DA PEÇA
    // =========================================================

    private void mostrarRelatorioPeca(
            Peca peca,
            BigDecimal receitaIngressos,
            BigDecimal valorAluguel,
            BigDecimal saldo) {

        String texto =
                "<html><center>" +
                        "<b>RELATÓRIO FINANCEIRO DA PEÇA</b><br><br>" +

                        "Peça: <b>" +
                        peca.getTitulo() +
                        "</b><br><br>" +

                        "Receita de ingressos: <b>R$ " +
                        formatarValor(receitaIngressos) +
                        "</b><br>" +

                        "Valor do aluguel: <b>R$ " +
                        formatarValor(valorAluguel) +
                        "</b><br>" +

                        "Saldo: <b>R$ " +
                        formatarValor(saldo) +
                        "</b>" +

                        "</center></html>";

        resultadoFinanceiro.setText(texto);
    }

    // =========================================================
    // MOSTRAR RELATÓRIO FINANCEIRO DO TEATRO
    // =========================================================

    private void mostrarRelatorioPeriodo(
            LocalDate inicio,
            LocalDate fim,
            BigDecimal receitaIngressos,
            BigDecimal receitaContratos,
            BigDecimal total) {

        String texto =
                "<html><center>" +
                        "<b>RELATÓRIO FINANCEIRO DO TEATRO</b><br><br>" +

                        "Período: " +
                        inicio.format(formatter) +
                        " até " +
                        fim.format(formatter) +
                        "<br><br>" +

                        "Receita de ingressos: <b>R$ " +
                        formatarValor(receitaIngressos) +
                        "</b><br>" +

                        "Receita de contratos: <b>R$ " +
                        formatarValor(receitaContratos) +
                        "</b><br><br>" +

                        "<b>TOTAL GERAL: R$ " +
                        formatarValor(total) +
                        "</b>" +

                        "</center></html>";

        resultadoFinanceiro.setText(texto);
    }

    // =========================================================
    // FORMATAR VALOR
    // =========================================================

    private String formatarValor(BigDecimal valor) {

        if (valor == null) {
            return "0,00";
        }

        return String.format(
                java.util.Locale.US,
                "%.2f",
                valor
        ).replace(".", ",");
    }

    // =========================================================
    // CARREGAR PEÇAS
    // =========================================================

    private void carregarPecas() {

        comboPeca.removeAllItems();

        for (Peca peca : pecaService.listarTodos()) {
            comboPeca.addItem(peca);
        }
    }

    // =========================================================
    // MOSTRAR ERRO
    // =========================================================

    private void mostrarErro(Exception ex) {

        String mensagem = ex.getMessage();

        if (mensagem == null || mensagem.isBlank()) {
            mensagem = "Ocorreu um erro inesperado.";
        }

        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // =========================================================
    // VOLTAR AO MENU
    // =========================================================

    private void voltarAoMenu() {

        dispose();

        menuPrincipalView.setVisible(true);
    }
}