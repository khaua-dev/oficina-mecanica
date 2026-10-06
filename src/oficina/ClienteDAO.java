package oficina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ClienteDAO {

    public static ArrayList<Cliente> listar(String busca) throws SQLException {
        String sql = "SELECT id, nome, telefone FROM cliente "
                + "WHERE nome LIKE ? OR id IN (SELECT cliente_id FROM veiculo WHERE placa LIKE ?) "
                + "ORDER BY nome";
        ArrayList<Cliente> lista = new ArrayList<>();
        try (Connection con = ConexaoBD.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + busca + "%");
            ps.setString(2, "%" + busca + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Cliente(rs.getInt("id"), rs.getString("nome"), rs.getString("telefone")));
                }
            }
            for (Cliente c : lista) {
                VeiculoDAO.carregarDoCliente(con, c);
            }
        }
        return lista;
    }

    public static void inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nome, telefone) VALUES (?, ?)";
        try (Connection con = ConexaoBD.abrir();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getTelefone());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    c.setId(chaves.getInt(1));
                }
            }
        }
    }

    public static void atualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nome = ?, telefone = ? WHERE id = ?";
        try (Connection con = ConexaoBD.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getTelefone());
            ps.setInt(3, c.getId());
            ps.executeUpdate();
        }
    }

    public static void excluir(Cliente c) throws SQLException {
        try (Connection con = ConexaoBD.abrir()) {
            con.setAutoCommit(false);
            try (PreparedStatement veiculos = con.prepareStatement("DELETE FROM veiculo WHERE cliente_id = ?");
                 PreparedStatement cliente = con.prepareStatement("DELETE FROM cliente WHERE id = ?")) {
                veiculos.setInt(1, c.getId());
                veiculos.executeUpdate();
                cliente.setInt(1, c.getId());
                cliente.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
}
