package facade;

public class ReciboDTO {
    private final int numeroMesa;
    private final double totalPedido;
    private final double[] divisaoPorPessoa;

    public ReciboDTO(int numeroMesa, double totalPedido, double[] divisaoPorPessoa) {
        this.numeroMesa = numeroMesa;
        this.totalPedido = totalPedido;
        this.divisaoPorPessoa = divisaoPorPessoa;
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public double getTotalPedido() {
        return totalPedido;
    }

    public double[] getDivisaoPorPessoa() {
        return divisaoPorPessoa;
    }
}
