package com.teatro.view;

import com.teatro.model.Artista;
import com.teatro.model.TipoGenero;
import com.teatro.service.ArtistaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ArtistaView extends JFrame {

    private final MenuPrincipalView menuPrincipalView;
    private final ArtistaService artistaService;


    private JTextField campoCpf;
    private JTextField campoNome;
    private JTextField campoTelefone;
    private JTextField campoEmail;
    private JComboBox<TipoGenero> comboGenero;
    private JTextField campoDataNascimento;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private Artista artistaSelecionado;

    public ArtistaView(MenuPrincipalView menuPrincipalView) {
        this.menuPrincipalView = menuPrincipalView;
        artistaService = new ArtistaService();

        setTitle("Teatro Sapeense - Artistas");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                voltarAoMenu();
            }
        });

        criarInterface();
        carregarArtistas();
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
                        "Cadastro de Artistas",
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
                new JPanel(new GridBagLayout());

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(5, 5, 5, 5);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1;

        campoCpf = new JTextField();
        campoNome = new JTextField();
        campoTelefone = new JTextField();
        campoEmail = new JTextField();
        comboGenero =
                new JComboBox<>(TipoGenero.values());
        campoDataNascimento = new JTextField();

        adicionarCampo(
                painelFormulario,
                gbc,
                0,
                "CPF:",
                campoCpf
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                1,
                "Nome completo:",
                campoNome
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                2,
                "Telefone:",
                campoTelefone
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                3,
                "E-mail:",
                campoEmail
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                4,
                "Gênero:",
                comboGenero
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                5,
                "Data nascimento:",
                campoDataNascimento
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
        gbc.gridy = 6;
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
                                "CPF",
                                "Nome",
                                "Telefone",
                                "E-mail",
                                "Gênero",
                                "Nascimento"
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

        tabela =
                new JTable(modeloTabela);

        tabela.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane =
                new JScrollPane(tabela);

        painelPrincipal.add(
                scrollPane,
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
                e -> voltarAoMenu());

        tabela.getSelectionModel()
                .addListSelectionListener(
                        e -> selecionarArtista()
                );
    }

    private void adicionarCampo(
            JPanel painel,
            GridBagConstraints gbc,
            int linha,
            String texto,
            JComponent componente) {

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

            Artista artista =
                    criarArtistaComDados();

            artistaService.salvar(artista);

            JOptionPane.showMessageDialog(
                    this,
                    "Artista cadastrado com sucesso!"
            );

            limparCampos();
            carregarArtistas();

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

        if (artistaSelecionado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um artista na tabela."
            );

            return;
        }

        try {

            preencherArtista(
                    artistaSelecionado
            );

            artistaService.atualizar(
                    artistaSelecionado
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Artista atualizado com sucesso!"
            );

            limparCampos();
            carregarArtistas();

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

        if (artistaSelecionado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um artista na tabela."
            );

            return;
        }

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente excluir este artista?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION
                );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            artistaService.remover(
                    artistaSelecionado
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Artista excluído com sucesso!"
            );

            limparCampos();
            carregarArtistas();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível excluir o artista:\n"
                            + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void voltarAoMenu() {
        dispose();
        menuPrincipalView.setVisible(true);
    }

    private Artista criarArtistaComDados() {

        Artista artista = new Artista();

        preencherArtista(artista);

        return artista;
    }

    private void preencherArtista(
            Artista artista) {

        String cpf =
                campoCpf.getText().trim();

        String nome =
                campoNome.getText().trim();

        String telefone =
                campoTelefone.getText().trim();

        String email =
                campoEmail.getText().trim();

        String dataTexto =
                campoDataNascimento.getText().trim();

        if (cpf.isEmpty()
                || nome.isEmpty()
                || telefone.isEmpty()
                || email.isEmpty()
                || dataTexto.isEmpty()) {

            throw new IllegalArgumentException(
                    "Preencha todos os campos obrigatórios."
            );
        }

        LocalDate dataNascimento;

        try {

            dataNascimento =
                    LocalDate.parse(dataTexto);

        } catch (DateTimeParseException e) {

            throw new IllegalArgumentException(
                    "A data deve estar no formato AAAA-MM-DD."
            );
        }

        artista.setCpf(cpf);
        artista.setNomeCompleto(nome);
        artista.setTelefone(telefone);
        artista.setEmail(email);
        artista.setGenero(
                (TipoGenero) comboGenero.getSelectedItem()
        );
        artista.setDataNascimento(
                dataNascimento
        );
    }

    private void selecionarArtista() {

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

        artistaSelecionado =
                artistaService.buscarPorId(id);

        if (artistaSelecionado == null) {
            return;
        }

        campoCpf.setText(
                artistaSelecionado.getCpf()
        );

        campoNome.setText(
                artistaSelecionado.getNomeCompleto()
        );

        campoTelefone.setText(
                artistaSelecionado.getTelefone()
        );

        campoEmail.setText(
                artistaSelecionado.getEmail()
        );

        comboGenero.setSelectedItem(
                artistaSelecionado.getGenero()
        );

        campoDataNascimento.setText(
                artistaSelecionado
                        .getDataNascimento()
                        .toString()
        );
    }

    private void carregarArtistas() {

        modeloTabela.setRowCount(0);

        List<Artista> artistas =
                artistaService.listarTodos();

        for (Artista artista : artistas) {

            modeloTabela.addRow(
                    new Object[]{
                            artista.getId(),
                            artista.getCpf(),
                            artista.getNomeCompleto(),
                            artista.getTelefone(),
                            artista.getEmail(),
                            artista.getGenero(),
                            artista.getDataNascimento()
                    }
            );
        }
    }

    private void limparCampos() {

        campoCpf.setText("");
        campoNome.setText("");
        campoTelefone.setText("");
        campoEmail.setText("");
        campoDataNascimento.setText("");

        comboGenero.setSelectedIndex(0);

        artistaSelecionado = null;

        tabela.clearSelection();

        campoCpf.requestFocus();
    }
}