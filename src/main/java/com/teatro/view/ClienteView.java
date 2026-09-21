package com.teatro.view;

import com.teatro.model.Cliente;
import com.teatro.model.TipoGenero;
import com.teatro.service.ClienteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ClienteView extends JFrame {

    private final ClienteService clienteService;
    private final MenuPrincipalView menuPrincipalView;

    private JTextField campoCpf;
    private JTextField campoNome;
    private JTextField campoTelefone;
    private JTextField campoEmail;
    private JComboBox<TipoGenero> comboGenero;
    private JTextField campoDataNascimento;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private Cliente clienteSelecionado;

    public ClienteView(MenuPrincipalView menuPrincipalView) {

        this.menuPrincipalView = menuPrincipalView;
        this.clienteService = new ClienteService();

        setTitle("Teatro Sapeense - Clientes");
        setSize(900, 600);
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
        carregarClientes();
    }

    private void criarInterface() {

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titulo = new JLabel(
                "Cadastro de Clientes",
                SwingConstants.CENTER
        );

        titulo.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        painelPrincipal.add(titulo, BorderLayout.NORTH);

        JPanel painelFormulario = new JPanel(
                new GridBagLayout()
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        campoCpf = new JTextField();
        campoNome = new JTextField();
        campoTelefone = new JTextField();
        campoEmail = new JTextField();

        comboGenero = new JComboBox<>(
                TipoGenero.values()
        );

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

        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoAtualizar = new JButton("Atualizar");
        JButton botaoExcluir = new JButton("Excluir");
        JButton botaoLimpar = new JButton("Limpar");
        JButton botaoVoltar = new JButton("Voltar ao Menu");

        JPanel painelBotoes = new JPanel(
                new FlowLayout(FlowLayout.CENTER)
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

        modeloTabela = new DefaultTableModel(
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
                    int column
            ) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);

        tabela.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane = new JScrollPane(tabela);

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
                e -> voltarAoMenu()
        );

        tabela.getSelectionModel()
                .addListSelectionListener(
                        e -> selecionarCliente()
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

            Cliente cliente =
                    criarClienteComDados();

            clienteService.salvar(cliente);

            JOptionPane.showMessageDialog(
                    this,
                    "Cliente cadastrado com sucesso!"
            );

            limparCampos();
            carregarClientes();

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

        if (clienteSelecionado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um cliente na tabela."
            );

            return;
        }

        try {

            preencherCliente(
                    clienteSelecionado
            );

            clienteService.atualizar(
                    clienteSelecionado
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Cliente atualizado com sucesso!"
            );

            limparCampos();
            carregarClientes();

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

        if (clienteSelecionado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um cliente na tabela."
            );

            return;
        }

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente excluir este cliente?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION
                );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            clienteService.remover(
                    clienteSelecionado
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Cliente excluído com sucesso!"
            );

            limparCampos();
            carregarClientes();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível excluir o cliente:\n"
                            + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Cliente criarClienteComDados() {

        Cliente cliente = new Cliente();

        preencherCliente(cliente);

        return cliente;
    }

    private void preencherCliente(
            Cliente cliente
    ) {

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

        cliente.setCpf(cpf);
        cliente.setNomeCompleto(nome);
        cliente.setTelefone(telefone);
        cliente.setEmail(email);

        cliente.setGenero(
                (TipoGenero) comboGenero.getSelectedItem()
        );

        cliente.setDataNascimento(
                dataNascimento
        );
    }

    private void selecionarCliente() {

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

        clienteSelecionado =
                clienteService.buscarPorId(id);

        if (clienteSelecionado == null) {
            return;
        }

        campoCpf.setText(
                clienteSelecionado.getCpf()
        );

        campoNome.setText(
                clienteSelecionado.getNomeCompleto()
        );

        campoTelefone.setText(
                clienteSelecionado.getTelefone()
        );

        campoEmail.setText(
                clienteSelecionado.getEmail()
        );

        comboGenero.setSelectedItem(
                clienteSelecionado.getGenero()
        );

        campoDataNascimento.setText(
                clienteSelecionado
                        .getDataNascimento()
                        .toString()
        );
    }

    private void carregarClientes() {

        modeloTabela.setRowCount(0);

        List<Cliente> clientes =
                clienteService.listarTodos();

        for (Cliente cliente : clientes) {

            modeloTabela.addRow(
                    new Object[]{
                            cliente.getId(),
                            cliente.getCpf(),
                            cliente.getNomeCompleto(),
                            cliente.getTelefone(),
                            cliente.getEmail(),
                            cliente.getGenero(),
                            cliente.getDataNascimento()
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

        clienteSelecionado = null;

        tabela.clearSelection();

        campoCpf.requestFocus();
    }

    private void voltarAoMenu() {

        dispose();

        menuPrincipalView.setVisible(true);
    }
}