/**
 * Classe Controladora responsável por mediar as ações do DOM da interface gráfica
 * com a camada de negócio MesaService. Trata erros lógicos e padroniza os retornos.
 */
export default class MesaController {
    /**
     * @param {MesaService} mesaService 
     */
    constructor(mesaService) {
        this.mesaService = mesaService;
    }

    /**
     * Comanda a abertura de uma mesa.
     */
    abrirMesa(numero) {
        try {
            this.mesaService.abrirAtendimento(numero);
            return { sucesso: true, mensagem: `Sucesso: Mesa ${numero} está ocupada!` };
        } catch (e) {
            return { sucesso: false, mensagem: e.message };
        }
    }

    /**
     * Comanda a liberação do caixa.
     */
    fecharContaELiberarMesa(numero) {
        try {
            this.mesaService.fecharContaELiberarParaLimpeza(numero);
            return { sucesso: true, mensagem: `Sucesso: Conta da Mesa ${numero} fechada e enviada para limpeza!` };
        } catch (e) {
            return { sucesso: false, mensagem: e.message };
        }
    }

    /**
     * Comanda a confirmação de limpeza física.
     */
    confirmarLimpezaMesa(numero) {
        try {
            this.mesaService.confirmarHigienizacao(numero);
            return { sucesso: true, mensagem: `Sucesso: Mesa ${numero} higienizada e liberada!` };
        } catch (e) {
            return { sucesso: false, mensagem: e.message };
        }
    }

    /**
     * Comanda o envio de pedidos.
     */
    fazerPedidoMesa(numero, itens, restricoes) {
        try {
            const pedido = this.mesaService.criarPedido(numero, itens, restricoes);
            return { sucesso: true, payload: pedido, mensagem: `Sucesso: Pedido ${pedido.id} enviado!` };
        } catch (e) {
            return { sucesso: false, payload: null, mensagem: e.message };
        }
    }

    /**
     * Obtém todas as mesas do repositório.
     */
    obterMesas() {
        return this.mesaService.mesaDAO.listarTodas();
    }

    /**
     * Obtém o pedido ativo (não pago) de uma mesa.
     */
    obterPedidoAtivo(numero) {
        return this.mesaService.mesaDAO.buscarPedidoPorMesa(numero);
    }
}
