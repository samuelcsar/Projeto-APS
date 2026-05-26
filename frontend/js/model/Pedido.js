/**
 * Entidade que modela a comanda/pedido de uma mesa no restaurante.
 */
export default class Pedido {
    /**
     * @param {number} id Identificador único do pedido.
     * @param {object} mesa Objeto Mesa associado ao pedido.
     * @param {number} tempoPreparoMinutos SLA máximo de preparo (em minutos).
     * @param {string} restricoesAlimentares Alertas declarados pelo cliente na mesa.
     */
    constructor(id, mesa, tempoPreparoMinutos = 20, restricoesAlimentares = "") {
        this.id = id;
        this.mesa = mesa;
        this.itens = [];
        this.status = 'RECEBIDO'; // RECEBIDO, EM_PREPARO, PRONTO, ENTREGUE, PAGO
        this.tempoPreparoMinutos = tempoPreparoMinutos;
        this.restricoesAlimentares = restricoesAlimentares;
        this.timestamp = Date.now();
    }

    /**
     * Adiciona um item do cardápio à comanda.
     * @param {ItemCardapio} item 
     */
    adicionarItem(item) {
        if (item) {
            this.itens.push(item);
        }
    }

    /**
     * Calcula o total acumulado do pedido.
     * @returns {number} O total formatado.
     */
    calcularTotal() {
        return this.itens.reduce((soma, item) => soma + item.preco, 0);
    }

    /**
     * Verifica a existência de alergias declaradas ou itens alérgenos.
     * @returns {boolean} True se houver restrições.
     */
    possuiRestricoesOuAlergenos() {
        if (this.restricoesAlimentares && this.restricoesAlimentares.trim().length > 0) {
            return true;
        }
        return this.itens.some(item => item.alergenos && item.alergenos.length > 0);
    }
}
