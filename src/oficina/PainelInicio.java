package oficina;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PainelInicio extends JPanel {

    private JLabel lblAbertas = new JLabel("0");
    private JLabel lblAndamento = new JLabel("0");
    private JLabel lblFinalizadas = new JLabel("0");

    private DefaultTableModel modelo = Tema.modelo("Nº", "Cliente", "Placa", "Status");
    private JTable tabela = Tema.tabela(modelo, "Últimas ordens de serviço");

    public PainelInicio(TelaPrincipal tela) {
        setLayout(new BorderLayout(0, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Tema.FUNDO);

        JPanel topo = new JPanel(new BorderLayout(0, 15));
        topo.setOpaque(false);
        topo.add(Tema.titulo("Painel inicial"), BorderLayout.NORTH);

        JPanel cartoes = new JPanel(new GridLayout(1, 3, 15, 0));
        cartoes.setOpaque(false);
        cartoes.add(criarCartao("Ordens abertas", lblAbertas));
        cartoes.add(criarCartao("Em andamento", lblAndamento));
        cartoes.add(criarCartao("Finalizadas", lblFinalizadas));
        topo.add(cartoes, BorderLayout.CENTER);

        JButton btnNova = Tema.botaoPrincipal("+ Nova ordem de serviço");
        btnNova.addActionListener(e -> tela.mostrar("nova"));
        JButton btnCliente = Tema.botaoSecundario("Cadastrar cliente");
        btnCliente.addActionListener(e -> tela.mostrar("clientes"));
        topo.add(Tema.linha(btnNova, btnCliente), BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);

        tabela.getColumnModel().getColumn(0).setMaxWidth(70);
        tabela.getColumnModel().getColumn(3).setCellRenderer(new Tema.StatusRenderer());

        JPanel base = new JPanel(new BorderLayout(0, 8));
        base.setOpaque(false);
        base.add(Tema.subtitulo("Últimas ordens de serviço"), BorderLayout.NORTH);
        base.add(Tema.rolagem(tabela), BorderLayout.CENTER);
        add(base, BorderLayout.CENTER);
    }

    private JPanel criarCartao(String rotulo, JLabel numero) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDA),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        numero.setFont(new Font("SansSerif", Font.BOLD, 36));
        numero.setForeground(Tema.AZUL_ESCURO);
        p.add(numero, BorderLayout.NORTH);
        p.add(Tema.rotulo(rotulo), BorderLayout.CENTER);
        return p;
    }

    public void atualizar() {
        try {
            int[] total = OrdemServicoDAO.contarPorStatus();
            lblAbertas.setText(String.valueOf(total[0]));
            lblAndamento.setText(String.valueOf(total[1]));
            lblFinalizadas.setText(String.valueOf(total[2]));

            modelo.setRowCount(0);
            for (OrdemServico o : OrdemServicoDAO.listarRecentes(6)) {
                modelo.addRow(new Object[] {
                    o.getId(),
                    o.getVeiculo().getCliente().getNome(),
                    o.getVeiculo().getPlaca(),
                    Tema.textoStatus(o.getStatus())
                });
            }
        } catch (java.sql.SQLException ex) {
            Tema.erroBanco(this, ex);
        }
    }
}
