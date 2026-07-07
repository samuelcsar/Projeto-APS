package strategy;

import model.Pedido;

/**
 * Estratégia que divide o valor total da conta igualmente entre todas as pessoas.
 */
public class DivisaoIgualitariaStrategy implements EstrategiaDivisaoConta {

    @Override
    public double[] calcularDivisao(Pedido pedido, int numeroPessoas) {
        if (numeroPessoas <= 0) {
            throw new IllegalArgumentException("O número de pessoas deve ser maior que zero.");
        }
        
        double total = pedido.calcularTotal();
        double valorPorPessoa = total / numeroPessoas;
        
        double[] divisao = new double[numeroPessoas];
        for (int i = 0; i < numeroPessoas; i++) {
            divisao[i] = valorPorPessoa;
        }
        
        return divisao;
    }
}
