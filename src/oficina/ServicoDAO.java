package oficina;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ServicoDAO {

    public static ArrayList<Servico> listar() throws SQLException {
        ArrayList<Servico> lista = new ArrayList<>();
        try (Connection con = ConexaoBD.abrir();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, descricao, preco FROM servico ORDER BY id")) {
            while (rs.next()) {
                lista.add(new Servico(rs.getInt("id"), rs.getString("descricao"), rs.getDouble("preco")));
            }
        }
        return lista;
    }
}
