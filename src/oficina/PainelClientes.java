package oficina;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class PainelClientes extends JPanel {

    private DefaultTableModel modeloClientes = Tema.modelo("Nome", "Telefone");
    private JTable tabelaClientes = Tema.tabela(modeloClientes, "Lista de clientes");
    private DefaultTableModel modeloVeiculos = Tema.modelo("Placa", "Modelo", "Ano");
    private JTable tabelaVeiculos = Tema.tabela(modeloVeiculos, "Veículos do cliente");

    private JTextField txtBusca = Tema.campo(20);
    private JTextField txtNome = Tema.campo(20);
    private JTextField txtTelefone = Tema.campo(20);
    private JTextField txtPlaca = Tema.campo(8);
    private JTextField txtModelo = Tema.campo(8);
    private JTextField txtAno = Tema.campo(4);

    private ArrayList<Cliente> listados = new ArrayList<>(); 
    private Cliente selecionado = null;                     
    private boolean atualizando = false;                   

    public PainelClientes() {
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Tema.FUNDO);

        add(criarTopo(), BorderLayout.NORTH);
        add(criarCentro(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);

        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || atualizando) {
                return;
            }
            int linha = tabelaClientes.getSelectedRow();
            if (linha >= 0) {
                selecionado = listados.get(linha);
                mostrarSelecionado();
            }
        });
    }


    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout(0, 10));
        topo.setOpaque(false);
        topo.add(Tema.titulo("Clientes e Veículos"), BorderLayout.NORTH);

        JLabel lblBusca = Tema.rotulo("Buscar cliente (nome ou placa):");
        lblBusca.setLabelFor(txtBusca);

        JButton btnBuscar = Tema.botaoPrincipal("Buscar");
        btnBuscar.addActionListener(e -> carregarClientes(txtBusca.getText()));
        txtBusca.addActionListener(e -> carregarClientes(txtBusca.getText()));

        JButton btnTodos = Tema.botaoSecundario("Mostrar todos");
        btnTodos.addActionListener(e -> {
            txtBusca.setText("");
            carregarClientes("");
        });

        JPanel busca = new JPanel(new BorderLayout(8, 0));
        busca.setOpaque(false);
        busca.add(lblBusca, BorderLayout.WEST);
        busca.add(txtBusca, BorderLayout.CENTER);
        busca.add(Tema.linha(btnBuscar, btnTodos), BorderLayout.EAST);
        topo.add(busca, BorderLayout.CENTER);
        return topo;
    }

    private JPanel criarCentro() {
        JPanel centro = new JPanel(new BorderLayout(15, 0));
        centro.setOpaque(false);

        JScrollPane listaClientes = Tema.rolagem(tabelaClientes);
        listaClientes.setPreferredSize(new Dimension(360, 0));
        centro.add(listaClientes, BorderLayout.WEST);

        JPanel direita = new JPanel(new BorderLayout(0, 12));
        direita.setOpaque(false);

        JPanel grupoCliente = Tema.grupo("Dados do cliente");
        JPanel campos = new JPanel(new GridLayout(2, 1, 0, 8));
        campos.setOpaque(false);
        campos.add(Tema.campoComRotulo("Nome *", txtNome));
        campos.add(Tema.campoComRotulo("Telefone *", txtTelefone));
        grupoCliente.add(campos, BorderLayout.CENTER);
        direita.add(grupoCliente, BorderLayout.NORTH);

        JPanel grupoVeiculos = Tema.grupo("Veículos do cliente");
        JPanel camposVeiculo = new JPanel(new GridLayout(1, 3, 8, 0));
        camposVeiculo.setOpaque(false);
        camposVeiculo.add(Tema.campoComRotulo("Placa *", txtPlaca));
        camposVeiculo.add(Tema.campoComRotulo("Modelo *", txtModelo));
        camposVeiculo.add(Tema.campoComRotulo("Ano", txtAno));

        JButton btnAdicionar = Tema.botaoSecundario("Adicionar veículo");
        btnAdicionar.addActionListener(e -> adicionarVeiculo());
        JButton btnRemover = Tema.botaoExcluir("Remover veículo");
        btnRemover.addActionListener(e -> removerVeiculo());

        JPanel entrada = new JPanel(new BorderLayout(0, 8));
        entrada.setOpaque(false);
        entrada.add(camposVeiculo, BorderLayout.NORTH);
        entrada.add(Tema.linha(btnAdicionar, btnRemover), BorderLayout.CENTER);

        grupoVeiculos.add(entrada, BorderLayout.NORTH);
        grupoVeiculos.add(Tema.rolagem(tabelaVeiculos), BorderLayout.CENTER);
        direita.add(grupoVeiculos, BorderLayout.CENTER);

        centro.add(direita, BorderLayout.CENTER);
        return centro;
    }

    private JPanel criarRodape() {
        JButton btnNovo = Tema.botaoSecundario("Novo");
        btnNovo.addActionListener(e -> limpar());
        JButton btnSalvar = Tema.botaoPrincipal("Salvar");
        btnSalvar.addActionListener(e -> salvar());
        JButton btnExcluir = Tema.botaoExcluir("Excluir");
        btnExcluir.addActionListener(e -> excluir());

        JLabel legenda = Tema.rotulo("* campos obrigatórios");
        legenda.setForeground(Tema.TEXTO_SEC);
        return Tema.linha(btnNovo, btnSalvar, btnExcluir, legenda);
    }

    public void atualizar() {
        txtBusca.setText("");
        carregarClientes("");
    }

    private void carregarClientes(String filtro) {
        String busca = filtro.trim().toLowerCase();
        atualizando = true;
        listados.clear();
        modeloClientes.setRowCount(0);
        try {
            for (Cliente c : ClienteDAO.listar(busca)) {
                listados.add(c);
                modeloClientes.addRow(new Object[] { c.getNome(), c.getTelefone() });
            }
        } catch (java.sql.SQLException ex) {
            Tema.erroBanco(this, ex);
        }
        atualizando = false;

        if (selecionado != null) {
            int posicao = listados.indexOf(selecionado);
            if (posicao >= 0) {
                selecionado = listados.get(posicao); 
                tabelaClientes.setRowSelectionInterval(posicao, posicao);
                carregarVeiculos();
            } else {
                limpar();
            }
        }
    }

    private void mostrarSelecionado() {
        txtNome.setText(selecionado.getNome());
        txtTelefone.setText(selecionado.getTelefone());
        limparCamposVeiculo();
        carregarVeiculos();
    }

    private void carregarVeiculos() {
        modeloVeiculos.setRowCount(0);
        if (selecionado != null) {
            for (Veiculo v : selecionado.getVeiculos()) {
                String ano = (v.getAno() == 0) ? "" : String.valueOf(v.getAno());
                modeloVeiculos.addRow(new Object[] { v.getPlaca(), v.getModelo(), ano });
            }
        }
    }

    private void limparCamposVeiculo() {
        txtPlaca.setText("");
        txtModelo.setText("");
        txtAno.setText("");
    }

    private void limpar() {
        selecionado = null;
        tabelaClientes.clearSelection();
        txtNome.setText("");
        txtTelefone.setText("");
        limparCamposVeiculo();
        carregarVeiculos();
        txtNome.requestFocusInWindow();
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText().trim();
        if (nome.isEmpty() || telefone.isEmpty()) {
            aviso("Preencha o nome e o telefone do cliente.");
            return;
        }

        try {
            if (selecionado == null) {
                selecionado = new Cliente(0, nome, telefone);
                ClienteDAO.inserir(selecionado);
            } else {
                selecionado.setNome(nome);
                selecionado.setTelefone(telefone);
                ClienteDAO.atualizar(selecionado);
            }
        } catch (java.sql.SQLException ex) {
            Tema.erroBanco(this, ex);
            return;
        }

        txtBusca.setText("");
        carregarClientes("");
        JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!", "Salvar",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void excluir() {
        if (selecionado == null) {
            aviso("Selecione um cliente na lista para excluir.");
            return;
        }
     
        try {
            if (OrdemServicoDAO.clienteTemOrdens(selecionado)) {
                aviso("Este cliente possui ordens de serviço e não pode ser excluído.");
                return;
            }
        } catch (java.sql.SQLException ex) {
            Tema.erroBanco(this, ex);
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja excluir o cliente " + selecionado.getNome() + "?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resposta == JOptionPane.YES_OPTION) {
            try {
                ClienteDAO.excluir(selecionado);
            } catch (java.sql.SQLException ex) {
                Tema.erroBanco(this, ex);
                return;
            }
            selecionado = null;
            txtBusca.setText("");
            carregarClientes("");
            limpar();
        }
    }

    private void adicionarVeiculo() {
        if (selecionado == null) {
            aviso("Salve ou selecione um cliente antes de adicionar um veículo.");
            return;
        }
        String placa = txtPlaca.getText().trim().toUpperCase();
        String modelo = txtModelo.getText().trim();
        if (placa.isEmpty() || modelo.isEmpty()) {
            aviso("Preencha a placa e o modelo do veículo.");
            return;
        }
    
        try {
            if (VeiculoDAO.placaExiste(placa)) {
                aviso("Esta placa já está cadastrada.");
                return;
            }
        } catch (java.sql.SQLException ex) {
            Tema.erroBanco(this, ex);
            return;
        }

        int ano = 0;
        String textoAno = txtAno.getText().trim();
        if (!textoAno.isEmpty()) {
            try {
                ano = Integer.parseInt(textoAno);
            } catch (NumberFormatException ex) {
                aviso("O ano deve ser um número, como 2015.");
                return;
            }
            if (ano < 1900 || ano > 2100) {
                aviso("Informe um ano válido, como 2015.");
                return;
            }
        }

        Veiculo novo = new Veiculo(0, placa, modelo, ano, selecionado);
        try {
            VeiculoDAO.inserir(novo);
        } catch (java.sql.SQLException ex) {
            selecionado.removerVeiculo(novo); 
            Tema.erroBanco(this, ex);
            return;
        }
        carregarVeiculos();
        limparCamposVeiculo();
    }

    private void removerVeiculo() {
        int linha = tabelaVeiculos.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione um veículo na lista para remover.");
            return;
        }
        Veiculo v = selecionado.getVeiculos().get(linha);
        try {
            if (OrdemServicoDAO.veiculoTemOrdens(v)) {
                aviso("Este veículo possui ordens de serviço e não pode ser removido.");
                return;
            }
            VeiculoDAO.excluir(v);
        } catch (java.sql.SQLException ex) {
            Tema.erroBanco(this, ex);
            return;
        }
        selecionado.removerVeiculo(v);
        carregarVeiculos();
    }

    private void aviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }
}
