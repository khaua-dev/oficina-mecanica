package oficina;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class PainelOrdens extends JPanel {

    private JComboBox<String> cmbStatus = new JComboBox<>(
            new String[] {"Todos", "Aberta", "Em andamento", "Finalizada"});
    private JTextField txtCliente = Tema.campo(12);
    private JTextField txtPlaca = Tema.campo(8);

    private DefaultTableModel modeloOrdens =
            Tema.modelo("Nº", "Data", "Cliente", "Placa", "Mecânico", "Status", "Total");
    private JTable tabelaOrdens = Tema.tabela(modeloOrdens, "Lista de ordens de serviço");

    private DefaultTableModel modeloServicos = Tema.modelo("Serviço", "Preço (R$)");
    private JTable tabelaServicos = Tema.tabela(modeloServicos, "Serviços da ordem selecionada");
    private JLabel lblDetalhe = Tema.rotulo("");
    private JLabel lblTotal = new JLabel("R$ 0,00");

    private JButton btnIniciar = Tema.botaoSecundario("Iniciar ordem");
    private JButton btnFinalizar = Tema.botaoPrincipal("Finalizar ordem");

    private ArrayList<OrdemServico> exibidas = new ArrayList<>(); // ordens que aparecem na tabela
    private boolean atualizando = false;

    public PainelOrdens() {
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Tema.FUNDO);

        Tema.estilo(cmbStatus);

        JPanel topo = new JPanel(new BorderLayout(0, 10));
        topo.setOpaque(false);
        topo.add(Tema.titulo("Ordens de serviço"), BorderLayout.NORTH);
        topo.add(criarFiltros(), BorderLayout.CENTER);
        add(topo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 1, 0, 12));
        centro.setOpaque(false);
        centro.add(Tema.rolagem(tabelaOrdens));
        centro.add(criarDetalhes());
        add(centro, BorderLayout.CENTER);

        btnIniciar.addActionListener(e -> iniciar());
        btnFinalizar.addActionListener(e -> finalizar());
        add(Tema.linha(btnIniciar, btnFinalizar), BorderLayout.SOUTH);

        int[] larguras = {40, 90, 140, 90, 120, 140, 100};
        for (int i = 0; i < larguras.length; i++) {
            tabelaOrdens.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
        tabelaOrdens.getColumnModel().getColumn(5).setCellRenderer(new Tema.StatusRenderer());

        tabelaOrdens.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !atualizando) {
                mostrarDetalhes();
            }
        });
    }

    private JPanel criarFiltros() {
        JButton btnFiltrar = Tema.botaoPrincipal("Filtrar");
        btnFiltrar.addActionListener(e -> carregar());
        JButton btnLimpar = Tema.botaoSecundario("Limpar");
        btnLimpar.addActionListener(e -> limparFiltros());
        txtCliente.addActionListener(e -> carregar());   // tecla Enter
        txtPlaca.addActionListener(e -> carregar());

        JPanel filtros = new JPanel(new GridLayout(1, 5, 10, 0));
        filtros.setOpaque(false);
        filtros.add(Tema.campoComRotulo("Status", cmbStatus));
        filtros.add(Tema.campoComRotulo("Cliente", txtCliente));
        filtros.add(Tema.campoComRotulo("Placa", txtPlaca));
        filtros.add(Tema.alinharAbaixo(btnFiltrar));
        filtros.add(Tema.alinharAbaixo(btnLimpar));
        return filtros;
    }

    private JPanel criarDetalhes() {
        JPanel grupo = Tema.grupo("Detalhes da ordem selecionada");
        grupo.add(lblDetalhe, BorderLayout.NORTH);
        grupo.add(Tema.rolagem(tabelaServicos), BorderLayout.CENTER);

        lblTotal.setFont(Tema.FONTE_TITULO);
        lblTotal.setForeground(Tema.AZUL_ESCURO);
        JPanel total = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        total.setOpaque(false);
        total.add(Tema.subtitulo("Total:"));
        total.add(lblTotal);
        grupo.add(total, BorderLayout.SOUTH);
        return grupo;
    }

    public void atualizar() {
        limparFiltros();
    }

    private void limparFiltros() {
        cmbStatus.setSelectedIndex(0);
        txtCliente.setText("");
        txtPlaca.setText("");
        carregar();
    }

    private void carregar() {
        String status = (String) cmbStatus.getSelectedItem();
        String cliente = txtCliente.getText().trim().toLowerCase();
        String placa = txtPlaca.getText().trim().toLowerCase();

        atualizando = true;
        exibidas.clear();
        modeloOrdens.setRowCount(0);
        try {
            for (OrdemServico o : OrdemServicoDAO.listar(status, cliente, placa)) {
                exibidas.add(o);
                modeloOrdens.addRow(new Object[] {
                    o.getId(),
                    Tema.data(o.getDataAbertura()),
                    o.getVeiculo().getCliente().getNome(),
                    o.getVeiculo().getPlaca(),
                    o.getMecanico().getNome(),
                    Tema.textoStatus(o.getStatus()),
                    Tema.dinheiro(o.calcularTotal())
                });
            }
        } catch (java.sql.SQLException ex) {
            Tema.erroBanco(this, ex);
        }
        atualizando = false;
        mostrarDetalhes();
    }

    private void mostrarDetalhes() {
        modeloServicos.setRowCount(0);
        int linha = tabelaOrdens.getSelectedRow();
        if (linha < 0) {
            lblDetalhe.setText("Selecione uma ordem na lista para ver os detalhes.");
            lblTotal.setText("R$ 0,00");
            btnIniciar.setEnabled(false);
            btnFinalizar.setEnabled(false);
            return;
        }

        OrdemServico o = exibidas.get(linha);
        lblDetalhe.setText("Ordem nº " + o.getId()
                + "   |   Veículo: " + o.getVeiculo().getModelo() + " (" + o.getVeiculo().getPlaca() + ")"
                + "   |   Cliente: " + o.getVeiculo().getCliente().getNome()
                + "   |   Mecânico: " + o.getMecanico().getNome());
        for (Servico s : o.getServicos()) {
            modeloServicos.addRow(new Object[] { s.getDescricao(), Tema.numero(s.getPreco()) });
        }
        lblTotal.setText(Tema.dinheiro(o.calcularTotal()));

        btnIniciar.setEnabled(o.getStatus().equals("Aberta"));
        btnFinalizar.setEnabled(o.getStatus().equals("Em andamento"));
    }


    private void iniciar() {
        OrdemServico o = ordemSelecionada();
        if (o != null) {
            o.iniciar();
            try {
                OrdemServicoDAO.atualizarStatus(o);
            } catch (java.sql.SQLException ex) {
                Tema.erroBanco(this, ex);
            }
            recarregarMantendoSelecao(o);
        }
    }

    private void finalizar() {
        OrdemServico o = ordemSelecionada();
        if (o == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this,
                "Finalizar a ordem nº " + o.getId() + "?\nDepois disso não será possível alterar os serviços.",
                "Finalizar ordem", JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            o.finalizar();
            try {
                OrdemServicoDAO.atualizarStatus(o);
            } catch (java.sql.SQLException ex) {
                Tema.erroBanco(this, ex);
            }
            recarregarMantendoSelecao(o);
        }
    }

    private OrdemServico ordemSelecionada() {
        int linha = tabelaOrdens.getSelectedRow();
        if (linha < 0) {
            return null;
        }
        return exibidas.get(linha);
    }

    private void recarregarMantendoSelecao(OrdemServico o) {
        carregar();
        int posicao = exibidas.indexOf(o);
        if (posicao >= 0) {
            tabelaOrdens.setRowSelectionInterval(posicao, posicao);
        }
    }
}
