package strategy;

import model.Pedido;

/**
 * Estratégia em que uma única pessoa paga o valor total da conta.
 */
public class SemDivisaoStrategy implements EstrategiaDivisaoConta {

    @Override
    public double[] calcularDivisao(Pedido pedido, int numeroPessoas) {
        // Retorna um array de 1 posição contendo o valor total do pedido
        double[] divisao = new double[1];
        divisao[0] = pedido.calcularTotal();
        return divisao;
    }
}
