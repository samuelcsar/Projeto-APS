package strategy;

import model.Pedido;

/**
 * Interface comum para todas as estratégias de divisão de conta.
 */
public interface EstrategiaDivisaoConta {
    
    /**
     * Calcula como a conta deve ser dividida.
     * 
     * @param pedido O pedido que contém os itens consumidos.
     * @param numeroPessoas A quantidade de pessoas para dividir a conta.
     * @return Um array de double onde cada posição representa o valor que uma pessoa deve pagar.
     */
    double[] calcularDivisao(Pedido pedido, int numeroPessoas);
}
