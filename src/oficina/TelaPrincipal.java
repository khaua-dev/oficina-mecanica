package oficina;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class TelaPrincipal extends JFrame {

    private CardLayout cartoes = new CardLayout();
    private JPanel area = new JPanel(cartoes);

    private PainelInicio inicio;
    private PainelClientes clientes;
    private PainelNovaOrdem novaOrdem;
    private PainelOrdens ordens;

    private String[] nomes = {"inicio", "clientes", "nova", "ordens"};
    private String[] textos = {"Início", "Clientes e Veículos", "Nova Ordem", "Ordens de Serviço"};
    private int[] teclas = {KeyEvent.VK_I, KeyEvent.VK_C, KeyEvent.VK_N, KeyEvent.VK_O};
    private JButton[] botoesMenu = new JButton[4];

    public TelaPrincipal() {
        super("Oficina Mecânica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 640);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        inicio = new PainelInicio(this);
        clientes = new PainelClientes();
        novaOrdem = new PainelNovaOrdem(this);
        ordens = new PainelOrdens();

        area.add(inicio, "inicio");
        area.add(clientes, "clientes");
        area.add(novaOrdem, "nova");
        area.add(ordens, "ordens");

        add(criarMenu(), BorderLayout.WEST);
        add(area, BorderLayout.CENTER);

        mostrar("inicio");
    }

    private JPanel criarMenu() {
        JPanel menu = new JPanel(new BorderLayout());
        menu.setBackground(Tema.AZUL_ESCURO);
        menu.setPreferredSize(new Dimension(190, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel itens = new JPanel(new GridLayout(0, 1, 0, 4));
        itens.setOpaque(false);

        JLabel logo = new JLabel("OFICINA");
        logo.setFont(Tema.FONTE_TITULO);
        logo.setForeground(Color.WHITE);
        logo.setBorder(BorderFactory.createEmptyBorder(10, 18, 20, 10));
        itens.add(logo);

        for (int i = 0; i < nomes.length; i++) {
            final String destino = nomes[i];
            JButton b = criarBotaoMenu(textos[i]);
            b.setMnemonic(teclas[i]);  
            b.addActionListener(e -> mostrar(destino));
            botoesMenu[i] = b;
            itens.add(b);
        }
        menu.add(itens, BorderLayout.NORTH);

        JButton sair = criarBotaoMenu("Sair");
        sair.setMnemonic(KeyEvent.VK_S);
        sair.addActionListener(e -> {
            int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente sair do sistema?",
                    "Sair", JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        menu.add(sair, BorderLayout.SOUTH);
        return menu;
    }

    private JButton criarBotaoMenu(String texto) {
        JButton b = new JButton(texto);
        b.setFont(Tema.FONTE);
        b.setForeground(Color.WHITE);
        b.setBackground(Tema.AZUL_ESCURO);
        b.setOpaque(true);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 10));
        return b;
    }

    public void mostrar(String nome) {
        if (nome.equals("inicio")) {
            inicio.atualizar();
        } else if (nome.equals("clientes")) {
            clientes.atualizar();
        } else if (nome.equals("nova")) {
            novaOrdem.atualizar();
        } else if (nome.equals("ordens")) {
            ordens.atualizar();
        }
        cartoes.show(area, nome);

        for (int i = 0; i < nomes.length; i++) {
            boolean atual = nomes[i].equals(nome);
            botoesMenu[i].setBackground(atual ? Tema.AZUL_MENU : Tema.AZUL_ESCURO);
            botoesMenu[i].setFont(atual ? Tema.FONTE_NEGRITO : Tema.FONTE);
        }
    }
}
