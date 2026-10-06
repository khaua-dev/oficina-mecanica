package oficina;

public class Veiculo {
    private int id;
    private String placa;
    private String modelo;
    private int ano;
    private Cliente cliente;

    public Veiculo(int id, String placa, String modelo, int ano, Cliente cliente) {
        this.id = id;
        this.placa = placa;
        this.modelo = modelo;
        this.ano = ano;
        this.cliente = cliente;
        cliente.adicionarVeiculo(this); 
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPlaca() { return placa; }
    public String getModelo() { return modelo; }
    public int getAno() { return ano; }
    public Cliente getCliente() { return cliente; }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof Veiculo && ((Veiculo) o).id == id && id != 0);
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    @Override
    public String toString() {
        return placa + " - " + modelo;
    }
}
