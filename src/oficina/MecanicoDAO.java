package oficina;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MecanicoDAO {

    public static ArrayList<Mecanico> listar() throws SQLException {
        ArrayList<Mecanico> lista = new ArrayList<>();
        try (Connection con = ConexaoBD.abrir();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, nome FROM mecanico ORDER BY nome")) {
            while (rs.next()) {
                lista.add(new Mecanico(rs.getInt("id"), rs.getString("nome")));
            }
        }
        return lista;
    }
}
