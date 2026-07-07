package service;

import model.Pedido;
import strategy.EstrategiaDivisaoConta;

/**
 * Serviço responsável pelas operações financeiras e de fechamento de conta.
 * Utiliza o Padrão Strategy para calcular a divisão do pagamento.
 */
public class ContaService {

    /**
     * Calcula a divisão da conta de um pedido utilizando uma estratégia específica.
     * 
     * @param pedido O pedido que será pago.
     * @param quantidadePessoas O número de pessoas dividindo a conta.
     * @param estrategia A estratégia de divisão escolhida pelo cliente (Strategy).
     * @return Um array com os valores que cada pessoa deve pagar.
     */
    public double[] calcularDivisaoConta(Pedido pedido, int quantidadePessoas, EstrategiaDivisaoConta estrategia) {
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo.");
        }
        if (estrategia == null) {
            throw new IllegalArgumentException("Uma estratégia de divisão deve ser informada.");
        }
        
        System.out.println("[ContaService] Calculando divisão de conta para o Pedido " + pedido.getId() + " usando " + estrategia.getClass().getSimpleName());
        
        // Delega o cálculo para a estratégia recebida por parâmetro
        return estrategia.calcularDivisao(pedido, quantidadePessoas);
    }
}
