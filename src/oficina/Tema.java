package oficina;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Tema {

    public static final Color AZUL_ESCURO = new Color(0x1F3A5F);
    public static final Color AZUL = new Color(0x1565C0);
    public static final Color AZUL_MENU = new Color(0x2F5486);
    public static final Color FUNDO = new Color(0xF4F6F8);
    public static final Color TEXTO = new Color(0x212121);
    public static final Color TEXTO_SEC = new Color(0x5F6368);
    public static final Color BORDA = new Color(0x80878E);
    public static final Color VERMELHO = new Color(0xB3261E);
    public static final Color FOCO = new Color(0xE65100);
    public static final Color SELECAO = new Color(0xDCE9F9);
    public static final Color CABECALHO = new Color(0xE3E8EE);

    public static final Font FONTE = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONTE_NEGRITO = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONTE_SUBTITULO = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONTE_TITULO = new Font("SansSerif", Font.BOLD, 22);

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FONTE_TITULO);
        l.setForeground(AZUL_ESCURO);
        return l;
    }

    public static JLabel subtitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FONTE_SUBTITULO);
        l.setForeground(AZUL_ESCURO);
        return l;
    }

    public static JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FONTE);
        l.setForeground(TEXTO);
        return l;
    }

    public static JButton botaoPrincipal(String texto) {
        JButton b = new JButton(texto) {
            @Override
            public void setEnabled(boolean ativo) {
                super.setEnabled(ativo);
                setBackground(ativo ? AZUL : new Color(0xE0E3E7));
            }
        };
        b.setFont(FONTE_NEGRITO);
        b.setBackground(AZUL);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        return b;
    }

    public static JButton botaoSecundario(String texto) {
        return botaoComBorda(texto, AZUL);
    }

    public static JButton botaoExcluir(String texto) {
        return botaoComBorda(texto, VERMELHO);
    }

    private static JButton botaoComBorda(String texto, Color cor) {
        JButton b = new JButton(texto);
        b.setFont(FONTE_NEGRITO);
        b.setBackground(Color.WHITE);
        b.setForeground(cor);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cor, 2),
                BorderFactory.createEmptyBorder(7, 16, 7, 16)));
        return b;
    }


    public static JTextField campo(int colunas) {
        JTextField f = new JTextField(colunas);
        f.setFont(FONTE);
        f.setForeground(TEXTO);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        f.setPreferredSize(new Dimension(f.getPreferredSize().width, 32));
        return f;
    }

    public static void estilo(JComboBox<?> c) {
        c.setFont(FONTE);
        c.setPreferredSize(new Dimension(100, 32));
    }

    public static JPanel campoComRotulo(String texto, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = rotulo(texto);
        l.setLabelFor(campo);
        p.add(l, BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    public static JPanel grupo(String titulo) {
        JPanel g = new JPanel(new BorderLayout(0, 8));
        g.setBackground(Color.WHITE);
        TitledBorder borda = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDA), titulo);
        borda.setTitleFont(FONTE_SUBTITULO);
        borda.setTitleColor(AZUL_ESCURO);
        g.setBorder(BorderFactory.createCompoundBorder(borda, BorderFactory.createEmptyBorder(6, 10, 10, 10)));
        return g;
    }

    public static JPanel linha(Component... itens) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setOpaque(false);
        for (int i = 0; i < itens.length; i++) {
            if (i > 0) {
                p.add(Box.createHorizontalStrut(10));
            }
            p.add(itens[i]);
        }
        p.add(Box.createHorizontalGlue());
        return p;
    }

    public static JPanel alinharAbaixo(JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(c, BorderLayout.SOUTH);
        return p;
    }

    public static DefaultTableModel modelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    public static JTable tabela(DefaultTableModel modelo, String nomeAcessivel) {
        JTable t = new JTable(modelo);
        t.setFont(FONTE);
        t.setForeground(TEXTO);
        t.setRowHeight(28);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setSelectionBackground(SELECAO);
        t.setSelectionForeground(TEXTO);
        t.setGridColor(new Color(0xD0D5DA));
        t.setFillsViewportHeight(true);
        t.getTableHeader().setFont(FONTE_NEGRITO);
        t.getTableHeader().setBackground(CABECALHO);
        t.getTableHeader().setForeground(TEXTO);
        t.getTableHeader().setReorderingAllowed(false);
        t.getAccessibleContext().setAccessibleName(nomeAcessivel);
        return t;
    }

    public static JScrollPane rolagem(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(BORDA));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    public static String textoStatus(String status) {
        if (status.equals("Aberta")) {
            return "\u25CF Aberta";
        } else if (status.equals("Em andamento")) {
            return "\u25D0 Em andamento";
        } else {
            return "\u2714 Finalizada";
        }
    }

    public static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String texto = String.valueOf(value);
            if (texto.contains("Aberta")) {
                c.setForeground(new Color(0x0D47A1));
            } else if (texto.contains("andamento")) {
                c.setForeground(new Color(0x7A4B00));
            } else {
                c.setForeground(new Color(0x1B5E20));
            }
            c.setFont(FONTE_NEGRITO);
            return c;
        }
    }


    public static String numero(double valor) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor);
    }

    public static String dinheiro(double valor) {
        return "R$ " + numero(valor);
    }

    public static String data(LocalDate data) {
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }


    public static void erroBanco(java.awt.Component tela, java.sql.SQLException ex) {
        javax.swing.JOptionPane.showMessageDialog(tela,
                "Não foi possível acessar o banco de dados.\n" + ex.getMessage(),
                "Erro no banco de dados", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}
