package oficina;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
     
        }
        UIManager.put("Button.focus", Tema.FOCO);         
        UIManager.put("OptionPane.messageFont", Tema.FONTE); 
        UIManager.put("OptionPane.buttonFont", Tema.FONTE);


        try (java.sql.Connection teste = ConexaoBD.abrir()) {

        } catch (java.sql.SQLException ex) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Não foi possível conectar ao MySQL.\n"
                    + "Verifique se o servidor está ligado, se o script banco/oficina.sql foi executado\n"
                    + "e se o usuário e a senha em ConexaoBD.java estão corretos.\n\n" + ex.getMessage(),
                    "Erro de conexão", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}
