import StatusMesa from '../model/StatusMesa.js';
import Pedido from '../model/Pedido.js';

/**
 * Camada de Serviço contendo as regras de negócio rígidas do salão do restaurante.
 * Controla as transições de status válidas e a criação de comandas ativas.
 */
export default class MesaService {
    /**
     * @param {MesaDAO} mesaDAO 
     */
    constructor(mesaDAO) {
        this.mesaDAO = mesaDAO;
    }

    /**
     * Abre atendimento em uma mesa livre.
     */
    abrirAtendimento(numeroMesa) {
        const mesa = this.mesaDAO.buscarPorNumero(numeroMesa);
        if (!mesa) {
            throw new Error(`Mesa de número ${numeroMesa} não foi encontrada.`);
        }

        if (mesa.status !== StatusMesa.LIVRE) {
            throw new Error(`Não é possível acomodar novos clientes. A mesa está no status: ${mesa.status}`);
        }

        mesa.status = StatusMesa.OCUPADA;
        this.mesaDAO.atualizar(mesa);
    }

    /**
     * Fecha o consumo financeiro e envia a mesa obrigatoriamente para a limpeza.
     */
    fecharContaELiberarParaLimpeza(numeroMesa) {
        const mesa = this.mesaDAO.buscarPorNumero(numeroMesa);
        if (!mesa) {
            throw new Error(`Mesa de número ${numeroMesa} não foi encontrada.`);
        }

        if (mesa.status !== StatusMesa.OCUPADA) {
            throw new Error(`Operação inválida. Apenas mesas no status OCUPADA podem ter a conta fechada.`);
        }

        mesa.status = StatusMesa.AGUARDANDO_LIMPEZA;
        this.mesaDAO.atualizar(mesa);

        // Atualiza a comanda ativa para o status PAGO
        const pedido = this.mesaDAO.buscarPedidoPorMesa(numeroMesa);
        if (pedido) {
            pedido.status = 'PAGO';
            this.mesaDAO.atualizarPedido(pedido);
        }
    }

    /**
     * Regra do "Check-out de Higienização":
     * Só permite liberar a mesa para LIVRE se ela estiver no status AGUARDANDO_LIMPEZA.
     */
    confirmarHigienizacao(numeroMesa) {
        const mesa = this.mesaDAO.buscarPorNumero(numeroMesa);
        if (!mesa) {
            throw new Error(`Mesa de número ${numeroMesa} não foi encontrada.`);
        }

        if (mesa.status !== StatusMesa.AGUARDANDO_LIMPEZA) {
            throw new Error(`Regra Violada: A mesa ${numeroMesa} não pode ser liberada para uso sem antes passar pela higienização física confirmada.`);
        }

        mesa.status = StatusMesa.LIVRE;
        this.mesaDAO.atualizar(mesa);
    }

    /**
     * Registra um novo pedido no salão.
     */
    criarPedido(numeroMesa, itens, restricoes) {
        const mesa = this.mesaDAO.buscarPorNumero(numeroMesa);
        if (!mesa || mesa.status !== StatusMesa.OCUPADA) {
            throw new Error(`A mesa ${numeroMesa} precisa estar OCUPADA para fazer pedidos.`);
        }

        const id = Math.floor(1000 + Math.random() * 9000);
        const pedido = new Pedido(id, mesa, 20, restricoes);
        
        itens.forEach(item => {
            pedido.adicionarItem(item);
        });

        this.mesaDAO.salvarPedido(pedido);
        return pedido;
    }
}
