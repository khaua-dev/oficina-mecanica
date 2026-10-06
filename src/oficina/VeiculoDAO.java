package oficina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VeiculoDAO {

    static void carregarDoCliente(Connection con, Cliente cliente) throws SQLException {
        String sql = "SELECT id, placa, modelo, ano FROM veiculo WHERE cliente_id = ? ORDER BY id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cliente.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                  
                    new Veiculo(rs.getInt("id"), rs.getString("placa"), rs.getString("modelo"),
                            rs.getInt("ano"), cliente);   // ano nulo vira 0
                }
            }
        }
    }


    public static boolean placaExiste(String placa) throws SQLException {
        String sql = "SELECT COUNT(*) FROM veiculo WHERE placa = ?";
        try (Connection con = ConexaoBD.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

  
    public static void inserir(Veiculo v) throws SQLException {
        String sql = "INSERT INTO veiculo (placa, modelo, ano, cliente_id) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexaoBD.abrir();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getModelo());
            if (v.getAno() == 0) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, v.getAno());
            }
            ps.setInt(4, v.getCliente().getId());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    v.setId(chaves.getInt(1));
                }
            }
        }
    }

    public static void excluir(Veiculo v) throws SQLException {
        try (Connection con = ConexaoBD.abrir();
             PreparedStatement ps = con.prepareStatement("DELETE FROM veiculo WHERE id = ?")) {
            ps.setInt(1, v.getId());
            ps.executeUpdate();
        }
    }
}
