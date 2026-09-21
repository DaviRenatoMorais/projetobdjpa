package com.teatro.view;

import com.teatro.model.Peca;
import com.teatro.service.PecaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class PecaView extends JFrame {

    private final PecaService pecaService;
    private final MenuPrincipalView menuPrincipalView;

    private JTextField campoTitulo;
    private JTextArea campoDescricao;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private Peca pecaSelecionada;

    public PecaView(MenuPrincipalView menuPrincipalView) {

        this.menuPrincipalView = menuPrincipalView;
        this.pecaService = new PecaService();

        setTitle("Teatro Sapeense - Peças");
        setSize(800, 550);
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
                        "Cadastro de Peças",
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

        campoTitulo = new JTextField();

        campoDescricao = new JTextArea(5, 20);
        campoDescricao.setLineWrap(true);
        campoDescricao.setWrapStyleWord(true);

        JScrollPane scrollDescricao =
                new JScrollPane(campoDescricao);

        adicionarCampo(
                painelFormulario,
                gbc,
                0,
                "Título:",
                campoTitulo
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                1,
                "Descrição:",
                scrollDescricao
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
        gbc.gridy = 2;
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
                                "Título",
                                "Descrição"
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

        tabela.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(50);

        tabela.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(200);

        tabela.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(450);

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
                        e -> selecionarPeca()
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

    private void salvar() {

        try {

            Peca peca =
                    criarPecaComDados();

            pecaService.salvar(peca);

            JOptionPane.showMessageDialog(
                    this,
                    "Peça cadastrada com sucesso!"
            );

            limparCampos();
            carregarPecas();

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

        if (pecaSelecionada == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma peça na tabela."
            );

            return;
        }

        try {

            preencherPeca(
                    pecaSelecionada
            );

            pecaService.atualizar(
                    pecaSelecionada
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Peça atualizada com sucesso!"
            );

            limparCampos();
            carregarPecas();

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

        if (pecaSelecionada == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma peça na tabela."
            );

            return;
        }

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente excluir esta peça?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION
                );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            pecaService.remover(
                    pecaSelecionada
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Peça excluída com sucesso!"
            );

            limparCampos();
            carregarPecas();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível excluir a peça:\n"
                            + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Peca criarPecaComDados() {

        Peca peca = new Peca();

        preencherPeca(peca);

        return peca;
    }

    private void preencherPeca(Peca peca) {

        String titulo =
                campoTitulo.getText().trim();

        String descricao =
                campoDescricao.getText().trim();

        if (titulo.isEmpty()) {

            throw new IllegalArgumentException(
                    "O título da peça é obrigatório."
            );
        }

        peca.setTitulo(titulo);
        peca.setDescricao(descricao);
    }

    private void selecionarPeca() {

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

        pecaSelecionada =
                pecaService.buscarPorId(id);

        if (pecaSelecionada == null) {
            return;
        }

        campoTitulo.setText(
                pecaSelecionada.getTitulo()
        );

        campoDescricao.setText(
                pecaSelecionada.getDescricao()
        );
    }

    private void carregarPecas() {

        modeloTabela.setRowCount(0);

        List<Peca> pecas =
                pecaService.listarTodos();

        for (Peca peca : pecas) {

            modeloTabela.addRow(
                    new Object[]{
                            peca.getId(),
                            peca.getTitulo(),
                            peca.getDescricao()
                    }
            );
        }
    }

    private void limparCampos() {

        campoTitulo.setText("");
        campoDescricao.setText("");

        pecaSelecionada = null;

        tabela.clearSelection();

        campoTitulo.requestFocus();
    }

    private void voltarAoMenu() {

        dispose();

        menuPrincipalView.setVisible(true);
    }
}