package oficina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class OrdemServicoDAO {

    private static final String CONSULTA =
            "SELECT o.id, o.data_abertura, o.status, "
            + "v.id AS veiculo_id, v.placa, v.modelo, v.ano, "
            + "c.id AS cliente_id, c.nome AS cliente_nome, c.telefone, "
            + "m.id AS mecanico_id, m.nome AS mecanico_nome "
            + "FROM ordem_servico o "
            + "JOIN veiculo v ON v.id = o.veiculo_id "
            + "JOIN cliente c ON c.id = v.cliente_id "
            + "JOIN mecanico m ON m.id = o.mecanico_id ";


    public static ArrayList<OrdemServico> listar(String status, String cliente, String placa)
            throws SQLException {
        boolean filtraStatus = status != null && !status.equals("Todos") && !status.isEmpty();
        String sql = CONSULTA + "WHERE c.nome LIKE ? AND v.placa LIKE ? "
                + (filtraStatus ? "AND o.status = ? " : "") + "ORDER BY o.id";
        try (Connection con = ConexaoBD.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + cliente + "%");
            ps.setString(2, "%" + placa + "%");
            if (filtraStatus) {
                ps.setString(3, status);
            }
            return lerOrdens(con, ps);
        }
    }

    public static ArrayList<OrdemServico> listarRecentes(int limite) throws SQLException {
        String sql = CONSULTA + "ORDER BY o.id DESC LIMIT ?";
        try (Connection con = ConexaoBD.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limite);
            return lerOrdens(con, ps);
        }
    }

    public static int[] contarPorStatus() throws SQLException {
        int[] total = new int[3];
        String sql = "SELECT status, COUNT(*) AS qtd FROM ordem_servico GROUP BY status";
        try (Connection con = ConexaoBD.abrir();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String status = rs.getString("status");
                if (status.equals("Aberta")) {
                    total[0] = rs.getInt("qtd");
                } else if (status.equals("Em andamento")) {
                    total[1] = rs.getInt("qtd");
                } else {
                    total[2] = rs.getInt("qtd");
                }
            }
        }
        return total;
    }

    public static void inserir(OrdemServico ordem) throws SQLException {
        try (Connection con = ConexaoBD.abrir()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO ordem_servico (veiculo_id, mecanico_id, data_abertura, status) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, ordem.getVeiculo().getId());
                    ps.setInt(2, ordem.getMecanico().getId());
                    ps.setDate(3, java.sql.Date.valueOf(ordem.getDataAbertura()));
                    ps.setString(4, ordem.getStatus());
                    ps.executeUpdate();
                    try (ResultSet chaves = ps.getGeneratedKeys()) {
                        if (chaves.next()) {
                            ordem.setId(chaves.getInt(1));
                        }
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO ordem_servico_item (ordem_id, servico_id, preco) VALUES (?, ?, ?)")) {
                    for (Servico s : ordem.getServicos()) {
                        ps.setInt(1, ordem.getId());
                        ps.setInt(2, s.getId());
                        ps.setDouble(3, s.getPreco());
                        ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public static void atualizarStatus(OrdemServico ordem) throws SQLException {
        try (Connection con = ConexaoBD.abrir();
             PreparedStatement ps = con.prepareStatement("UPDATE ordem_servico SET status = ? WHERE id = ?")) {
            ps.setString(1, ordem.getStatus());
            ps.setInt(2, ordem.getId());
            ps.executeUpdate();
        }
    }

    public static boolean clienteTemOrdens(Cliente cliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ordem_servico o JOIN veiculo v ON v.id = o.veiculo_id "
                + "WHERE v.cliente_id = ?";
        return contar(sql, cliente.getId()) > 0;
    }

    public static boolean veiculoTemOrdens(Veiculo veiculo) throws SQLException {
        return contar("SELECT COUNT(*) FROM ordem_servico WHERE veiculo_id = ?", veiculo.getId()) > 0;
    }

    private static int contar(String sql, int id) throws SQLException {
        try (Connection con = ConexaoBD.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private static ArrayList<OrdemServico> lerOrdens(Connection con, PreparedStatement ps) throws SQLException {
        ArrayList<OrdemServico> lista = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = new Cliente(rs.getInt("cliente_id"),
                        rs.getString("cliente_nome"), rs.getString("telefone"));
                Veiculo veiculo = new Veiculo(rs.getInt("veiculo_id"), rs.getString("placa"),
                        rs.getString("modelo"), rs.getInt("ano"), cliente);
                Mecanico mecanico = new Mecanico(rs.getInt("mecanico_id"), rs.getString("mecanico_nome"));
                lista.add(new OrdemServico(rs.getInt("id"), veiculo, mecanico,
                        rs.getDate("data_abertura").toLocalDate(), rs.getString("status")));
            }
        }

        String sqlItens = "SELECT s.id, s.descricao, i.preco FROM ordem_servico_item i "
                + "JOIN servico s ON s.id = i.servico_id WHERE i.ordem_id = ? ORDER BY i.id";
        try (PreparedStatement itens = con.prepareStatement(sqlItens)) {
            for (OrdemServico o : lista) {
                itens.setInt(1, o.getId());
                try (ResultSet rs = itens.executeQuery()) {
                    while (rs.next()) {
                   
                        o.getServicos().add(new Servico(rs.getInt("id"), rs.getString("descricao"),
                                rs.getDouble("preco")));
                    }
                }
            }
        }
        return lista;
    }
}
