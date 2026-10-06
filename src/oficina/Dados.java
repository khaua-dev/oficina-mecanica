package oficina;

import java.util.ArrayList;

public class Dados {
    public static ArrayList<Cliente> clientes = new ArrayList<>();
    public static ArrayList<Mecanico> mecanicos = new ArrayList<>();
    public static ArrayList<Servico> servicos = new ArrayList<>();
    public static ArrayList<OrdemServico> ordens = new ArrayList<>();

    public static int proximoIdCliente = 1;
    public static int proximoIdVeiculo = 1;
    public static int proximoIdOrdem = 1;

    public static void carregarExemplos() {
        Cliente joao = new Cliente(proximoIdCliente++, "João Pereira", "(11) 99999-0000");
        Cliente ana = new Cliente(proximoIdCliente++, "Ana Lima", "(11) 98888-1111");
        Cliente pedro = new Cliente(proximoIdCliente++, "Pedro Alves", "(11) 97777-2222");
        clientes.add(joao);
        clientes.add(ana);
        clientes.add(pedro);

        Veiculo gol = new Veiculo(proximoIdVeiculo++, "ABC-1234", "Gol", 2015, joao);
        new Veiculo(proximoIdVeiculo++, "XYZ-7788", "Uno", 2012, joao);
        Veiculo onix = new Veiculo(proximoIdVeiculo++, "QWE-5678", "Onix", 2019, ana);
        Veiculo palio = new Veiculo(proximoIdVeiculo++, "JKL-9012", "Palio", 2010, pedro);

        mecanicos.add(new Mecanico(1, "Carlos Souza"));
        mecanicos.add(new Mecanico(2, "Rafael Dias"));

        servicos.add(new Servico(1, "Troca de óleo", 120.00));
        servicos.add(new Servico(2, "Revisão dos freios", 250.00));
        servicos.add(new Servico(3, "Alinhamento", 90.00));
        servicos.add(new Servico(4, "Balanceamento", 80.00));
        servicos.add(new Servico(5, "Troca de pastilhas", 180.00));

        OrdemServico o1 = new OrdemServico(proximoIdOrdem++, gol, mecanicos.get(0));
        o1.adicionarServico(servicos.get(0));
        o1.adicionarServico(servicos.get(1));
        o1.iniciar();
        ordens.add(o1);

        OrdemServico o2 = new OrdemServico(proximoIdOrdem++, onix, mecanicos.get(0));
        o2.adicionarServico(servicos.get(2));
        ordens.add(o2);

        OrdemServico o3 = new OrdemServico(proximoIdOrdem++, palio, mecanicos.get(1));
        o3.adicionarServico(servicos.get(4));
        o3.iniciar();
        o3.finalizar();
        ordens.add(o3);
    }

    public static boolean placaExiste(String placa) {
        for (Cliente c : clientes) {
            for (Veiculo v : c.getVeiculos()) {
                if (v.getPlaca().equalsIgnoreCase(placa)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean clienteTemOrdens(Cliente cliente) {
        for (OrdemServico o : ordens) {
            if (o.getVeiculo().getCliente() == cliente) {
                return true;
            }
        }
        return false;
    }

    public static boolean veiculoTemOrdens(Veiculo veiculo) {
        for (OrdemServico o : ordens) {
            if (o.getVeiculo() == veiculo) {
                return true;
            }
        }
        return false;
    }
}
