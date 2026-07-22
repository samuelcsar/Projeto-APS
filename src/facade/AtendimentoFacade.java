package facade;

import model.Pedido;
import service.ContaService;
import service.MesaService;
import strategy.EstrategiaDivisaoConta;

public class AtendimentoFacade {

    private final MesaService mesaService;
    private final ContaService contaService;

    public AtendimentoFacade(MesaService mesaService, ContaService contaService) {
        this.mesaService = mesaService;
        this.contaService = contaService;
    }

    public ReciboDTO finalizarAtendimento(int numeroMesa, Pedido pedido, int qtdPessoas, EstrategiaDivisaoConta estrategia) {
        // 1. Calcula a divisão da conta
        double[] valoresDivididos = contaService.calcularDivisaoConta(pedido, qtdPessoas, estrategia);

        // 2. Libera a mesa para higienização
        mesaService.fecharContaELiberarParaLimpeza(numeroMesa);

        // 3. (Futuro) Aqui poderia entrar a chamada para um PagamentoService

        // Retorna o consolidado em um DTO simplificado
        return new ReciboDTO(numeroMesa, pedido.calcularTotal(), valoresDivididos);
    }
}
