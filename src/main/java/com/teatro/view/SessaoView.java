package com.teatro.view;

import com.teatro.model.Peca;
import com.teatro.model.Sessao;
import com.teatro.service.PecaService;
import com.teatro.service.SessaoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SessaoView extends JFrame {

    private final SessaoService sessaoService;
    private final PecaService pecaService;
    private final MenuPrincipalView menuPrincipalView;

    private JComboBox<Peca> comboPeca;
    private JTextField campoData;
    private JTextField campoHorarioInicio;
    private JTextField campoHorarioFim;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private Sessao sessaoSelecionada;

    public SessaoView(MenuPrincipalView menuPrincipalView) {

        this.menuPrincipalView = menuPrincipalView;

        this.sessaoService = new SessaoService();
        this.pecaService = new PecaService();

        setTitle("Teatro Sapeense - Sessões");
        setSize(850, 600);
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
        carregarPecas();
        carregarSessoes();
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
                        "Cadastro de Sessões",
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

        comboPeca = new JComboBox<>();

        campoData = new JTextField();
        campoHorarioInicio = new JTextField();
        campoHorarioFim = new JTextField();

        adicionarCampo(
                painelFormulario,
                gbc,
                0,
                "Peça:",
                comboPeca
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                1,
                "Data:",
                campoData
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                2,
                "Horário inicial:",
                campoHorarioInicio
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                3,
                "Horário final:",
                campoHorarioFim
        );

        JButton botaoSalvar =
                new JButton("Salvar");

        JButton botaoAtualizar =
                new JButton("Atualizar");

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

        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoAtualizar);
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
                                "Peça",
                                "Data",
                                "Início",
                                "Fim"
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

        JScrollPane scrollTabela =
                new JScrollPane(tabela);

        painelPrincipal.add(
                scrollTabela,
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

        botaoLimpar.addActionListener(
                e -> limparCampos()
        );

        botaoVoltar.addActionListener(
                e -> voltarAoMenu()
        );

        tabela.getSelectionModel()
                .addListSelectionListener(
                        e -> selecionarSessao()
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

            Sessao sessao =
                    criarSessaoComDados();

            sessaoService.salvar(sessao);

            JOptionPane.showMessageDialog(
                    this,
                    "Sessão cadastrada com sucesso!"
            );

            limparCampos();
            carregarSessoes();

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void atualizar() {

        if (sessaoSelecionada == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma sessão na tabela."
            );

            return;
        }

        try {

            preencherSessao(
                    sessaoSelecionada
            );

            sessaoService.atualizar(
                    sessaoSelecionada
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Sessão atualizada com sucesso!"
            );

            limparCampos();
            carregarSessoes();

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void excluir() {

        if (sessaoSelecionada == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma sessão na tabela."
            );

            return;
        }

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente excluir esta sessão?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION
                );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            sessaoService.remover(
                    sessaoSelecionada
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Sessão excluída com sucesso!"
            );

            limparCampos();
            carregarSessoes();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível excluir a sessão:\n"
                            + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Sessao criarSessaoComDados() {

        Sessao sessao =
                new Sessao();

        preencherSessao(sessao);

        return sessao;
    }

    private void preencherSessao(
            Sessao sessao
    ) {

        Peca peca =
                (Peca) comboPeca.getSelectedItem();

        if (peca == null) {

            throw new IllegalArgumentException(
                    "Selecione uma peça."
            );
        }

        String dataTexto =
                campoData.getText().trim();

        String inicioTexto =
                campoHorarioInicio.getText().trim();

        String fimTexto =
                campoHorarioFim.getText().trim();

        if (dataTexto.isEmpty()
                || inicioTexto.isEmpty()
                || fimTexto.isEmpty()) {

            throw new IllegalArgumentException(
                    "Preencha todos os campos obrigatórios."
            );
        }

        LocalDate data;

        LocalTime horarioInicio;
        LocalTime horarioFim;

        try {

            data =
                    LocalDate.parse(dataTexto);

            horarioInicio =
                    LocalTime.parse(inicioTexto);

            horarioFim =
                    LocalTime.parse(fimTexto);

        } catch (DateTimeParseException e) {

            throw new IllegalArgumentException(
                    "Data: AAAA-MM-DD | Horários: HH:MM."
            );
        }

        sessao.setPeca(peca);
        sessao.setData(data);
        sessao.setHorarioInicio(horarioInicio);
        sessao.setHorarioFim(horarioFim);
    }

    private void selecionarSessao() {

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

        sessaoSelecionada =
                sessaoService.buscarPorId(id);

        if (sessaoSelecionada == null) {
            return;
        }

        selecionarPeca(
                sessaoSelecionada.getPeca()
        );

        campoData.setText(
                sessaoSelecionada
                        .getData()
                        .toString()
        );

        campoHorarioInicio.setText(
                sessaoSelecionada
                        .getHorarioInicio()
                        .toString()
        );

        campoHorarioFim.setText(
                sessaoSelecionada
                        .getHorarioFim()
                        .toString()
        );
    }

    private void selecionarPeca(Peca peca) {

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

    private void carregarSessoes() {

        modeloTabela.setRowCount(0);

        List<Sessao> sessoes =
                sessaoService.listarTodos();

        for (Sessao sessao : sessoes) {

            modeloTabela.addRow(
                    new Object[]{
                            sessao.getId(),
                            sessao.getPeca().getTitulo(),
                            sessao.getData(),
                            sessao.getHorarioInicio(),
                            sessao.getHorarioFim()
                    }
            );
        }
    }

    private void limparCampos() {

        campoData.setText("");
        campoHorarioInicio.setText("");
        campoHorarioFim.setText("");

        if (comboPeca.getItemCount() > 0) {
            comboPeca.setSelectedIndex(0);
        }

        sessaoSelecionada = null;

        tabela.clearSelection();

        campoData.requestFocus();
    }

    private void voltarAoMenu() {

        dispose();

        menuPrincipalView.setVisible(true);
    }
}