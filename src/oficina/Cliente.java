package oficina;

import java.util.ArrayList;

public class Cliente {
    private int id;
    private String nome;
    private String telefone;
    private ArrayList<Veiculo> veiculos = new ArrayList<>();

    public Cliente(int id, String nome, String telefone) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
    }

    public void adicionarVeiculo(Veiculo veiculo) {
        veiculos.add(veiculo);
    }

    public void removerVeiculo(Veiculo veiculo) {
        veiculos.remove(veiculo);
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getTelefone() { return telefone; }
    public ArrayList<Veiculo> getVeiculos() { return veiculos; }

    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof Cliente && ((Cliente) o).id == id && id != 0);
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    @Override
    public String toString() {
        return nome;
    }
}
