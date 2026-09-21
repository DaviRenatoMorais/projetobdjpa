package com.teatro.view;

import com.teatro.model.Artista;
import com.teatro.model.Contrato;
import com.teatro.model.Peca;
import com.teatro.model.StatusContrato;
import com.teatro.service.ArtistaService;
import com.teatro.service.ContratoService;
import com.teatro.service.PecaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ContratoView extends JFrame {

    private final ContratoService contratoService;
    private final ArtistaService artistaService;
    private final PecaService pecaService;
    private final MenuPrincipalView menuPrincipalView;

    private JComboBox<Artista> comboArtista;
    private JComboBox<Peca> comboPeca;
    private JTextField campoDataInicio;
    private JTextField campoDataFim;
    private JComboBox<StatusContrato> comboStatus;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private Contrato contratoSelecionado;

    public ContratoView(MenuPrincipalView menuPrincipalView) {

        this.menuPrincipalView = menuPrincipalView;

        contratoService = new ContratoService();
        artistaService = new ArtistaService();
        pecaService = new PecaService();

        setTitle("Teatro Sapeense - Contratos");
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                voltarAoMenu();
            }
        });

        criarInterface();
        carregarArtistas();
        carregarPecas();
        carregarContratos();
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
                        "Gerenciamento de Contratos",
                        SwingConstants.CENTER
                );

        titulo.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        painelPrincipal.add(
                titulo,
                BorderLayout.NORTH
        );

        JPanel painelFormulario =
                new JPanel(new GridBagLayout());

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(5, 5, 5, 5);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1;

        comboArtista = new JComboBox<>();
        comboPeca = new JComboBox<>();

        campoDataInicio = new JTextField();
        campoDataFim = new JTextField();

        comboStatus =
                new JComboBox<>(
                        StatusContrato.values()
                );

        adicionarCampo(
                painelFormulario,
                gbc,
                0,
                "Artista:",
                comboArtista
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                1,
                "Peça:",
                comboPeca
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                2,
                "Data inicial:",
                campoDataInicio
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                3,
                "Data final:",
                campoDataFim
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                4,
                "Status:",
                comboStatus
        );

        JButton botaoSalvar =
                new JButton("Salvar");

        JButton botaoAtualizar =
                new JButton("Atualizar");

        JButton botaoExcluir =
                new JButton("Excluir");

        JButton botaoEncerrar =
                new JButton("Encerrar");

        JButton botaoProrrogar =
                new JButton("Prorrogar");

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

        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoAtualizar);
        painelBotoes.add(botaoExcluir);
        painelBotoes.add(botaoEncerrar);
        painelBotoes.add(botaoProrrogar);
        painelBotoes.add(botaoLimpar);
        painelBotoes.add(botaoVoltar);

        gbc.gridx = 0;
        gbc.gridy = 5;
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
                                "Artista",
                                "Peça",
                                "Início",
                                "Fim",
                                "Valor Aluguel",
                                "Status"
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

        botaoSalvar.addActionListener(
                e -> salvar()
        );

        botaoAtualizar.addActionListener(
                e -> atualizar()
        );

        botaoExcluir.addActionListener(
                e -> excluir()
        );

        botaoEncerrar.addActionListener(
                e -> encerrar()
        );

        botaoProrrogar.addActionListener(
                e -> prorrogar()
        );

        botaoLimpar.addActionListener(
                e -> limparCampos()
        );

        botaoVoltar.addActionListener(
                e -> voltarAoMenu()
        );

        tabela.getSelectionModel()
                .addListSelectionListener(
                        e -> selecionarContrato()
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

    private void carregarArtistas() {

        comboArtista.removeAllItems();

        List<Artista> artistas =
                artistaService.listarTodos();

        for (Artista artista : artistas) {
            comboArtista.addItem(artista);
        }
    }

    private void carregarPecas() {

        comboPeca.removeAllItems();

        List<Peca> pecas =
                pecaService.listarTodos();

        for (Peca peca : pecas) {
            comboPeca.addItem(peca);
        }
    }

    private void salvar() {

        try {

            Contrato contrato =
                    criarContratoComDados();

            contratoService.salvar(contrato);

            JOptionPane.showMessageDialog(
                    this,
                    "Contrato cadastrado com sucesso!\n"
                            + "Valor do aluguel: R$ "
                            + contrato.getValorAluguel()
            );

            limparCampos();
            carregarContratos();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void atualizar() {

        if (contratoSelecionado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um contrato."
            );

            return;
        }

        try {

            preencherContrato(
                    contratoSelecionado
            );

            contratoService.atualizar(
                    contratoSelecionado
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Contrato atualizado com sucesso!"
            );

            limparCampos();
            carregarContratos();

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

        if (contratoSelecionado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um contrato."
            );

            return;
        }

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente excluir este contrato?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION
                );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            contratoService.remover(
                    contratoSelecionado
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Contrato excluído com sucesso!"
            );

            limparCampos();
            carregarContratos();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível excluir o contrato:\n"
                            + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void encerrar() {

        if (contratoSelecionado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um contrato."
            );

            return;
        }

        String dataFim =
                JOptionPane.showInputDialog(
                        this,
                        "Informe a nova data de encerramento:\n"
                                + "Formato: AAAA-MM-DD"
                );

        if (dataFim == null || dataFim.trim().isEmpty()) {
            return;
        }

        try {

            LocalDate data =
                    LocalDate.parse(
                            dataFim.trim()
                    );

            contratoService.encerrarContrato(
                    contratoSelecionado.getId(),
                    data
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Contrato encerrado com sucesso!"
            );

            limparCampos();
            carregarContratos();

        } catch (DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Data inválida. Use AAAA-MM-DD.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void prorrogar() {

        if (contratoSelecionado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um contrato."
            );

            return;
        }

        String dataFim =
                JOptionPane.showInputDialog(
                        this,
                        "Informe a nova data final:\n"
                                + "Formato: AAAA-MM-DD"
                );

        if (dataFim == null || dataFim.trim().isEmpty()) {
            return;
        }

        try {

            LocalDate novaData =
                    LocalDate.parse(
                            dataFim.trim()
                    );

            contratoService.prorrogarContrato(
                    contratoSelecionado.getId(),
                    novaData
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Contrato prorrogado com sucesso!"
            );

            limparCampos();
            carregarContratos();

        } catch (DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Data inválida. Use AAAA-MM-DD.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Contrato criarContratoComDados() {

        Contrato contrato =
                new Contrato();

        preencherContrato(contrato);

        return contrato;
    }

    private void preencherContrato(
            Contrato contrato
    ) {

        Artista artista =
                (Artista) comboArtista.getSelectedItem();

        Peca peca =
                (Peca) comboPeca.getSelectedItem();

        if (artista == null) {

            throw new IllegalArgumentException(
                    "Selecione um artista."
            );
        }

        if (peca == null) {

            throw new IllegalArgumentException(
                    "Selecione uma peça."
            );
        }

        String inicioTexto =
                campoDataInicio.getText().trim();

        String fimTexto =
                campoDataFim.getText().trim();

        if (inicioTexto.isEmpty()
                || fimTexto.isEmpty()) {

            throw new IllegalArgumentException(
                    "Informe as datas do contrato."
            );
        }

        LocalDate dataInicio;
        LocalDate dataFim;

        try {

            dataInicio =
                    LocalDate.parse(inicioTexto);

            dataFim =
                    LocalDate.parse(fimTexto);

        } catch (DateTimeParseException e) {

            throw new IllegalArgumentException(
                    "As datas devem estar no formato AAAA-MM-DD."
            );
        }

        contrato.setArtista(artista);
        contrato.setPeca(peca);
        contrato.setDataInicio(dataInicio);
        contrato.setDataFim(dataFim);

        contrato.setStatus(
                (StatusContrato)
                        comboStatus.getSelectedItem()
        );
    }

    private void selecionarContrato() {

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

        contratoSelecionado =
                contratoService.buscarPorId(id);

        if (contratoSelecionado == null) {
            return;
        }

        selecionarArtista(
                contratoSelecionado.getArtista()
        );

        selecionarPeca(
                contratoSelecionado.getPeca()
        );

        campoDataInicio.setText(
                contratoSelecionado
                        .getDataInicio()
                        .toString()
        );

        campoDataFim.setText(
                contratoSelecionado
                        .getDataFim()
                        .toString()
        );

        comboStatus.setSelectedItem(
                contratoSelecionado.getStatus()
        );
    }

    private void selecionarArtista(
            Artista artista
    ) {

        if (artista == null) {
            return;
        }

        for (int i = 0;
             i < comboArtista.getItemCount();
             i++) {

            Artista item =
                    comboArtista.getItemAt(i);

            if (item.getId()
                    .equals(artista.getId())) {

                comboArtista.setSelectedIndex(i);
                break;
            }
        }
    }

    private void selecionarPeca(
            Peca peca
    ) {

        if (peca == null) {
            return;
        }

        for (int i = 0;
             i < comboPeca.getItemCount();
             i++) {

            Peca item =
                    comboPeca.getItemAt(i);

            if (item.getId()
                    .equals(peca.getId())) {

                comboPeca.setSelectedIndex(i);
                break;
            }
        }
    }

    private void carregarContratos() {

        modeloTabela.setRowCount(0);

        List<Contrato> contratos =
                contratoService.listarTodos();

        for (Contrato contrato : contratos) {

            modeloTabela.addRow(
                    new Object[]{
                            contrato.getId(),
                            contrato.getArtista()
                                    .getNomeCompleto(),
                            contrato.getPeca()
                                    .getTitulo(),
                            contrato.getDataInicio(),
                            contrato.getDataFim(),
                            contrato.getValorAluguel(),
                            contrato.getStatus()
                    }
            );
        }
    }

    private void limparCampos() {

        if (comboArtista.getItemCount() > 0) {
            comboArtista.setSelectedIndex(0);
        }

        if (comboPeca.getItemCount() > 0) {
            comboPeca.setSelectedIndex(0);
        }

        campoDataInicio.setText("");
        campoDataFim.setText("");

        comboStatus.setSelectedItem(
                StatusContrato.ATIVO
        );

        contratoSelecionado = null;

        tabela.clearSelection();

        campoDataInicio.requestFocus();
    }

    private void voltarAoMenu() {

        dispose();

        menuPrincipalView.setVisible(true);
    }
}