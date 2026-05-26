import Mesa from '../model/Mesa.js';
import StatusMesa from '../model/StatusMesa.js';

/**
 * Repositório responsável por gerenciar a leitura e escrita de dados de mesas e pedidos.
 * Replicando o papel da persistência do SQLite do Java através do LocalStorage do navegador.
 */
export default class MesaDAO {
    constructor() {
        this.STORAGE_KEY = 'santini_gourmet_mesas';
        this.PEDIDOS_KEY = 'santini_gourmet_pedidos';
        this.inicializarDados();
    }

    /**
     * Carrega valores iniciais se o LocalStorage estiver limpo.
     */
    inicializarDados() {
        if (!localStorage.getItem(this.STORAGE_KEY)) {
            const mesasPadrao = [
                new Mesa(1, 4, StatusMesa.LIVRE),
                new Mesa(2, 2, StatusMesa.LIVRE),
                new Mesa(3, 6, StatusMesa.AGUARDANDO_LIMPEZA),
                new Mesa(4, 4, StatusMesa.OCUPADA)
            ];
            this.salvarTodasMesas(mesasPadrao);
        }
        if (!localStorage.getItem(this.PEDIDOS_KEY)) {
            localStorage.setItem(this.PEDIDOS_KEY, JSON.stringify([]));
        }
    }

    salvarTodasMesas(mesas) {
        localStorage.setItem(this.STORAGE_KEY, JSON.stringify(mesas));
    }

    listarTodas() {
        const dados = localStorage.getItem(this.STORAGE_KEY);
        return JSON.parse(dados) || [];
    }

    buscarPorNumero(numero) {
        const mesas = this.listarTodas();
        return mesas.find(m => m.numero === parseInt(numero)) || null;
    }

    salvar(mesa) {
        const mesas = this.listarTodas();
        if (mesas.some(m => m.numero === mesa.numero)) {
            this.atualizar(mesa);
        } else {
            mesas.push(mesa);
            this.salvarTodasMesas(mesas);
        }
    }

    atualizar(mesa) {
        const mesas = this.listarTodas();
        const index = mesas.findIndex(m => m.numero === mesa.numero);
        if (index !== -1) {
            mesas[index] = mesa;
            this.salvarTodasMesas(mesas);
        }
    }

    // Persistência de Pedidos para o fluxo
    listarTodosPedidos() {
        const dados = localStorage.getItem(this.PEDIDOS_KEY);
        return JSON.parse(dados) || [];
    }

    buscarPedidoPorMesa(numeroMesa) {
        const pedidos = this.listarTodosPedidos();
        return pedidos.find(p => p.mesa.numero === parseInt(numeroMesa) && p.status !== 'PAGO') || null;
    }

    salvarPedido(pedido) {
        const pedidos = this.listarTodosPedidos();
        const index = pedidos.findIndex(p => p.id === pedido.id);
        if (index !== -1) {
            pedidos[index] = pedido;
        } else {
            pedidos.push(pedido);
        }
        localStorage.setItem(this.PEDIDOS_KEY, JSON.stringify(pedidos));
    }

    atualizarPedido(pedido) {
        this.salvarPedido(pedido);
    }
}
