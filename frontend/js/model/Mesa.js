import StatusMesa from './StatusMesa.js';

/**
 * Entidade que representa uma Mesa física no salão do restaurante.
 */
export default class Mesa {
    /**
     * @param {number} numero Identificador único da mesa.
     * @param {number} capacidade Quantidade de assentos disponíveis.
     * @param {string} status Status atual da mesa (LIVRE, OCUPADA ou AGUARDANDO_LIMPEZA).
     */
    constructor(numero, capacidade, status = StatusMesa.LIVRE) {
        this.numero = numero;
        this.capacidade = capacidade;
        this.status = status;
    }
}
