package model;

/**
 * Enumeração que representa os possíveis estados de uma mesa no restaurante Santini Gourmet.
 * Define o fluxo obrigatório de ocupação e higienização.
 */
public enum StatusMesa {
    /**
     * A mesa está livre e pronta para receber novos clientes.
     */
    LIVRE,

    /**
     * A mesa está ocupada por clientes e possui um atendimento ativo.
     */
    OCUPADA,

    /**
     * A mesa foi liberada pelo caixa, mas necessita passar pelo processo
     * de higienização física antes de ser disponibilizada novamente.
     * Representa a trava do "Check-out de Higienização".
     */
    AGUARDANDO_LIMPEZA
}
