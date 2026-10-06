package oficina;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
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

public class PainelNovaOrdem extends JPanel {

    private TelaPrincipal tela;

    private JComboBox<Cliente> cmbCliente = new JComboBox<>();
    private JComboBox<Veiculo> cmbVeiculo = new JComboBox<>();
    private JComboBox<Mecanico> cmbMecanico = new JComboBox<>();
    private JTextField txtData = Tema.campo(10);
    private JComboBox<Servico> cmbServico = new JComboBox<>();

    private DefaultTableModel modeloServicos = Tema.modelo("Serviço", "Preço (R$)");
    private JTable tabelaServicos = Tema.tabela(modeloServicos, "Serviços da ordem");
    private JLabel lblTotal = new JLabel("R$ 0,00");

    private ArrayList<Servico> escolhidos = new ArrayList<>();
    private boolean carregando = false;                         

    public PainelNovaOrdem(TelaPrincipal tela) {
        this.tela = tela;
        setLayout(new BorderLayout(0, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Tema.FUNDO);

        Tema.estilo(cmbCliente);
        Tema.estilo(cmbVeiculo);
        Tema.estilo(cmbMecanico);
        Tema.estilo(cmbServico);
        txtData.setEditable(false);

        cmbCliente.addActionListener(e -> {
            if (!carregando) {
                carregarVeiculos();
            }
        });

        add(Tema.titulo("Nova ordem de serviço"), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 15));
        centro.setOpaque(false);
        centro.add(criarGrupoAtendimento(), BorderLayout.NORTH);
        centro.add(criarGrupoServicos(), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        JButton btnAbrir = Tema.botaoPrincipal("Abrir ordem");
        btnAbrir.addActionListener(e -> abrirOrdem());
        JButton btnCancelar = Tema.botaoSecundario("Cancelar");
        btnCancelar.addActionListener(e -> tela.mostrar("inicio"));
        add(Tema.linha(btnAbrir, btnCancelar), BorderLayout.SOUTH);
    }


    private JPanel criarGrupoAtendimento() {
        JPanel grupo = Tema.grupo("1. Dados do atendimento");
        JPanel campos = new JPanel(new GridLayout(1, 4, 10, 0));
        campos.setOpaque(false);
        campos.add(Tema.campoComRotulo("Cliente *", cmbCliente));
        campos.add(Tema.campoComRotulo("Veículo (placa) *", cmbVeiculo));
        campos.add(Tema.campoComRotulo("Mecânico *", cmbMecanico));
        campos.add(Tema.campoComRotulo("Data", txtData));
        grupo.add(campos, BorderLayout.CENTER);
        return grupo;
    }

    private JPanel criarGrupoServicos() {
        JPanel grupo = Tema.grupo("2. Serviços da ordem");

        JButton btnAdicionar = Tema.botaoSecundario("Adicionar");
        btnAdicionar.addActionListener(e -> adicionarServico());
        JPanel linhaServico = new JPanel(new BorderLayout(10, 0));
        linhaServico.setOpaque(false);
        linhaServico.add(Tema.campoComRotulo("Serviço", cmbServico), BorderLayout.CENTER);
        linhaServico.add(Tema.alinharAbaixo(btnAdicionar), BorderLayout.EAST);
        grupo.add(linhaServico, BorderLayout.NORTH);

        grupo.add(Tema.rolagem(tabelaServicos), BorderLayout.CENTER);

        JButton btnRemover = Tema.botaoExcluir("Remover serviço");
        btnRemover.addActionListener(e -> removerServico());

        lblTotal.setFont(Tema.FONTE_TITULO);
        lblTotal.setForeground(Tema.AZUL_ESCURO);
        JPanel total = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        total.setOpaque(false);
        total.add(Tema.subtitulo("Total:"));
        total.add(lblTotal);

        JPanel baixo = new JPanel(new BorderLayout());
        baixo.setOpaque(false);
        baixo.add(btnRemover, BorderLayout.WEST);
        baixo.add(total, BorderLayout.EAST);
        grupo.add(baixo, BorderLayout.SOUTH);
        return grupo;
    }


    public void atualizar() {
        carregando = true;
        cmbCliente.removeAllItems();
        cmbMecanico.removeAllItems();
        cmbServico.removeAllItems();
        try {
            for (Cliente c : ClienteDAO.listar("")) {
                cmbCliente.addItem(c);
            }
            for (Mecanico m : MecanicoDAO.listar()) {
                cmbMecanico.addItem(m);
            }
            for (Servico s : ServicoDAO.listar()) {
                cmbServico.addItem(s);
            }
        } catch (java.sql.SQLException ex) {
            Tema.erroBanco(this, ex);
        }
        carregando = false;

        carregarVeiculos();
        txtData.setText(Tema.data(LocalDate.now()));
        escolhidos.clear();
        atualizarTabela();
    }

    private void carregarVeiculos() {
        cmbVeiculo.removeAllItems();
        Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
        if (cliente != null) {
            for (Veiculo v : cliente.getVeiculos()) {
                cmbVeiculo.addItem(v);
            }
        }
    }

    private void atualizarTabela() {
        modeloServicos.setRowCount(0);
        double total = 0;
        for (Servico s : escolhidos) {
            modeloServicos.addRow(new Object[] { s.getDescricao(), Tema.numero(s.getPreco()) });
            total += s.getPreco();
        }
        lblTotal.setText(Tema.dinheiro(total));
    }

    private void adicionarServico() {
        Servico servico = (Servico) cmbServico.getSelectedItem();
        if (servico == null) {
            aviso("Não há serviços cadastrados.");
            return;
        }
        escolhidos.add(servico);
        atualizarTabela();
    }

    private void removerServico() {
        int linha = tabelaServicos.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione um serviço na lista para remover.");
            return;
        }
        escolhidos.remove(linha);
        atualizarTabela();
    }

    private void abrirOrdem() {
        Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
        Veiculo veiculo = (Veiculo) cmbVeiculo.getSelectedItem();
        Mecanico mecanico = (Mecanico) cmbMecanico.getSelectedItem();

        if (cliente == null) {
            aviso("Cadastre um cliente antes de abrir uma ordem de serviço.");
            return;
        }
        if (veiculo == null) {
            aviso("Este cliente não tem veículo. Cadastre um na tela Clientes e Veículos.");
            return;
        }
        if (mecanico == null) {
            aviso("Não há mecânico cadastrado.");
            return;
        }
        if (escolhidos.isEmpty()) {
            aviso("Adicione pelo menos um serviço à ordem.");
            return;
        }

        OrdemServico ordem = new OrdemServico(0, veiculo, mecanico);
        for (Servico s : escolhidos) {
            ordem.adicionarServico(s);
        }
        try {
            OrdemServicoDAO.inserir(ordem);
        } catch (java.sql.SQLException ex) {
            Tema.erroBanco(this, ex);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Ordem de serviço nº " + ordem.getId() + " aberta com sucesso!\nTotal: "
                + Tema.dinheiro(ordem.calcularTotal()),
                "Ordem aberta", JOptionPane.INFORMATION_MESSAGE);
        tela.mostrar("ordens");
    }

    private void aviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }
}
