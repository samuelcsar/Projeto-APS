package model;

/**
 * Enumeração que representa os estados possíveis de um Pedido no restaurante Santini Gourmet.
 * Define o ciclo de vida do pedido desde a emissão até o fechamento.
 */
public enum StatusPedido {
    /**
     * O pedido foi registrado pelo garçom e enviado à cozinha, mas o preparo ainda não foi iniciado.
     */
    RECEBIDO,

    /**
     * O pedido está sendo ativamente preparado pelos cozinheiros.
     */
    EM_PREPARO,

    /**
     * O prato/bebida foi finalizado pela cozinha e está aguardando retirada pelo garçom.
     */
    PRONTO,

    /**
     * O prato/bebida foi entregue à mesa do cliente.
     */
    ENTREGUE,

    /**
     * O pedido foi liquidado financeiramente no caixa.
     */
    PAGO
}
