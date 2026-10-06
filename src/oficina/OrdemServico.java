package oficina;

import java.time.LocalDate;
import java.util.ArrayList;

public class OrdemServico {
    private int id;
    private Veiculo veiculo;
    private Mecanico mecanico;
    private LocalDate dataAbertura;
    private String status;
    private ArrayList<Servico> servicos = new ArrayList<>();

    public OrdemServico(int id, Veiculo veiculo, Mecanico mecanico) {
        this.id = id;
        this.veiculo = veiculo;
        this.mecanico = mecanico;
        this.dataAbertura = LocalDate.now();
        this.status = "Aberta";
    }

    public OrdemServico(int id, Veiculo veiculo, Mecanico mecanico, LocalDate dataAbertura, String status) {
        this.id = id;
        this.veiculo = veiculo;
        this.mecanico = mecanico;
        this.dataAbertura = dataAbertura;
        this.status = status;
    }

    public void adicionarServico(Servico servico) {
        if (status.equals("Finalizada")) {
            System.out.println("Erro: a ordem já foi finalizada.");
            return;
        }
        servicos.add(servico);
    }

    public double calcularTotal() {
        double total = 0;
        for (Servico s : servicos) {
            total += s.getPreco();
        }
        return total;
    }

    public void iniciar() {
        if (status.equals("Aberta")) {
            status = "Em andamento";
        }
    }

    public void finalizar() {
        if (status.equals("Em andamento")) {
            status = "Finalizada";
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Veiculo getVeiculo() { return veiculo; }
    public Mecanico getMecanico() { return mecanico; }
    public LocalDate getDataAbertura() { return dataAbertura; }
    public String getStatus() { return status; }
    public ArrayList<Servico> getServicos() { return servicos; }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof OrdemServico && ((OrdemServico) o).id == id && id != 0);
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }
}
