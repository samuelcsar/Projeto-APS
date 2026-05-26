/**
 * Entidade que representa um item disponível no cardápio do restaurante.
 */
export default class ItemCardapio {
    /**
     * @param {number} id Identificador único do item.
     * @param {string} nome Nome do prato ou bebida.
     * @param {string} descricao Descrição detalhada.
     * @param {number} preco Preço do item.
     * @param {string[]} alergenos Lista de alérgenos contidos no prato.
     */
    constructor(id, nome, descricao, preco, alergenos = []) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = parseFloat(preco);
        this.alergenos = alergenos;
    }
}
