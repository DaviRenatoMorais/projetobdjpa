package com.teatro.view;

import com.teatro.model.RegraPreco;
import com.teatro.model.Turno;
import com.teatro.service.RegraPrecoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class RegraPrecoView extends JFrame {

    private final RegraPrecoService regraPrecoService;
    private final MenuPrincipalView menuPrincipalView;

    private JTextField campoValorPorHora;
    private JComboBox<String> comboDiaSemana;
    private JComboBox<String> comboTurno;
    private JTextField campoHoraInicio;
    private JTextField campoHoraFim;
    private JTextField campoMes;
    private JTextField campoAno;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private RegraPreco regraSelecionada;

    public RegraPrecoView(MenuPrincipalView menuPrincipalView) {

        this.menuPrincipalView = menuPrincipalView;
        this.regraPrecoService = new RegraPrecoService();

        setTitle("Teatro Sapeense - Regras de Preço");
        setSize(950, 650);
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
        carregarRegras();
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
                        "Cadastro de Regras de Preço",
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
                new Insets(4, 4, 4, 4);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1;

        campoValorPorHora = new JTextField();

        comboDiaSemana = new JComboBox<>(
                new String[]{
                        "Todos",
                        "SEGUNDA",
                        "TERCA",
                        "QUARTA",
                        "QUINTA",
                        "SEXTA",
                        "SABADO",
                        "DOMINGO"
                }
        );

        comboTurno = new JComboBox<>(
                new String[]{
                        "Todos",
                        "MANHA",
                        "TARDE",
                        "NOITE"
                }
        );

        campoHoraInicio = new JTextField();
        campoHoraFim = new JTextField();
        campoMes = new JTextField();
        campoAno = new JTextField();

        adicionarCampo(
                painelFormulario,
                gbc,
                0,
                "Valor por hora:",
                campoValorPorHora
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                1,
                "Dia da semana:",
                comboDiaSemana
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                2,
                "Turno:",
                comboTurno
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                3,
                "Hora inicial:",
                campoHoraInicio
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                4,
                "Hora final:",
                campoHoraFim
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                5,
                "Mês:",
                campoMes
        );

        adicionarCampo(
                painelFormulario,
                gbc,
                6,
                "Ano:",
                campoAno
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
        gbc.gridy = 7;
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
                                "Valor/Hora",
                                "Dia",
                                "Turno",
                                "Início",
                                "Fim",
                                "Mês",
                                "Ano"
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
                        e -> selecionarRegra()
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

            RegraPreco regra =
                    criarRegraComDados();

            regraPrecoService.salvar(regra);

            JOptionPane.showMessageDialog(
                    this,
                    "Regra de preço cadastrada com sucesso!"
            );

            limparCampos();
            carregarRegras();

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

        if (regraSelecionada == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma regra na tabela."
            );

            return;
        }

        try {

            preencherRegra(
                    regraSelecionada
            );

            regraPrecoService.atualizar(
                    regraSelecionada
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Regra de preço atualizada com sucesso!"
            );

            limparCampos();
            carregarRegras();

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

        if (regraSelecionada == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma regra na tabela."
            );

            return;
        }

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente excluir esta regra?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION
                );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            regraPrecoService.remover(
                    regraSelecionada
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Regra de preço excluída com sucesso!"
            );

            limparCampos();
            carregarRegras();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível excluir a regra:\n"
                            + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private RegraPreco criarRegraComDados() {

        RegraPreco regra =
                new RegraPreco();

        preencherRegra(regra);

        return regra;
    }

    private void preencherRegra(
            RegraPreco regra
    ) {

        String valorTexto =
                campoValorPorHora.getText().trim();

        if (valorTexto.isEmpty()) {

            throw new IllegalArgumentException(
                    "O valor por hora é obrigatório."
            );
        }

        BigDecimal valor;

        try {

            valor =
                    new BigDecimal(
                            valorTexto.replace(",", ".")
                    );

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Informe um valor por hora válido."
            );
        }

        String horaInicioTexto =
                campoHoraInicio.getText().trim();

        String horaFimTexto =
                campoHoraFim.getText().trim();

        LocalTime horaInicio = null;
        LocalTime horaFim = null;

        if (!horaInicioTexto.isEmpty()
                || !horaFimTexto.isEmpty()) {

            if (horaInicioTexto.isEmpty()
                    || horaFimTexto.isEmpty()) {

                throw new IllegalArgumentException(
                        "Informe a hora inicial e a hora final."
                );
            }

            try {

                horaInicio =
                        LocalTime.parse(
                                horaInicioTexto
                        );

                horaFim =
                        LocalTime.parse(
                                horaFimTexto
                        );

            } catch (DateTimeParseException e) {

                throw new IllegalArgumentException(
                        "Os horários devem estar no formato HH:MM."
                );
            }
        }

        Integer mes = null;

        String mesTexto =
                campoMes.getText().trim();

        if (!mesTexto.isEmpty()) {

            try {

                mes =
                        Integer.parseInt(mesTexto);

            } catch (NumberFormatException e) {

                throw new IllegalArgumentException(
                        "O mês deve ser um número."
                );
            }
        }

        Integer ano = null;

        String anoTexto =
                campoAno.getText().trim();

        if (!anoTexto.isEmpty()) {

            try {

                ano =
                        Integer.parseInt(anoTexto);

            } catch (NumberFormatException e) {

                throw new IllegalArgumentException(
                        "O ano deve ser um número."
                );
            }
        }

        regra.setValorPorHora(valor);
        regra.setDiaSemana(obterDiaSemana());
        regra.setTurno(obterTurno());
        regra.setHorarioInicio(horaInicio);
        regra.setHorarioFim(horaFim);
        regra.setMes(mes);
        regra.setAno(ano);
    }

    private DayOfWeek obterDiaSemana() {

        int indice =
                comboDiaSemana.getSelectedIndex();

        if (indice == 0) {
            return null;
        }

        return DayOfWeek.of(indice);
    }

    private Turno obterTurno() {

        int indice =
                comboTurno.getSelectedIndex();

        if (indice == 0) {
            return null;
        }

        return Turno.values()[indice - 1];
    }

    private void selecionarRegra() {

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

        regraSelecionada =
                regraPrecoService.buscarPorId(id);

        if (regraSelecionada == null) {
            return;
        }

        campoValorPorHora.setText(
                regraSelecionada
                        .getValorPorHora()
                        .toString()
        );

        selecionarDiaSemana(
                regraSelecionada.getDiaSemana()
        );

        selecionarTurno(
                regraSelecionada.getTurno()
        );

        campoHoraInicio.setText(
                regraSelecionada.getHorarioInicio() == null
                        ? ""
                        : regraSelecionada
                          .getHorarioInicio()
                          .toString()
        );

        campoHoraFim.setText(
                regraSelecionada.getHorarioFim() == null
                        ? ""
                        : regraSelecionada
                          .getHorarioFim()
                          .toString()
        );

        campoMes.setText(
                regraSelecionada.getMes() == null
                        ? ""
                        : regraSelecionada
                          .getMes()
                          .toString()
        );

        campoAno.setText(
                regraSelecionada.getAno() == null
                        ? ""
                        : regraSelecionada
                          .getAno()
                          .toString()
        );
    }

    private void selecionarDiaSemana(
            DayOfWeek dia
    ) {

        if (dia == null) {

            comboDiaSemana.setSelectedIndex(0);

            return;
        }

        comboDiaSemana.setSelectedIndex(
                dia.getValue()
        );
    }

    private void selecionarTurno(
            Turno turno
    ) {

        if (turno == null) {

            comboTurno.setSelectedIndex(0);

            return;
        }

        comboTurno.setSelectedIndex(
                turno.ordinal() + 1
        );
    }

    private void carregarRegras() {

        modeloTabela.setRowCount(0);

        List<RegraPreco> regras =
                regraPrecoService.listarTodos();

        for (RegraPreco regra : regras) {

            modeloTabela.addRow(
                    new Object[]{
                            regra.getId(),
                            regra.getValorPorHora(),
                            regra.getDiaSemana(),
                            regra.getTurno(),
                            regra.getHorarioInicio(),
                            regra.getHorarioFim(),
                            regra.getMes(),
                            regra.getAno()
                    }
            );
        }
    }

    private void limparCampos() {

        campoValorPorHora.setText("");
        campoHoraInicio.setText("");
        campoHoraFim.setText("");
        campoMes.setText("");
        campoAno.setText("");

        comboDiaSemana.setSelectedIndex(0);
        comboTurno.setSelectedIndex(0);

        regraSelecionada = null;

        tabela.clearSelection();

        campoValorPorHora.requestFocus();
    }

    private void voltarAoMenu() {

        dispose();

        menuPrincipalView.setVisible(true);
    }
}