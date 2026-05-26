package model;

/**
 * Entidade de domínio que representa uma Mesa do restaurante Santini Gourmet.
 * Aplica os princípios de encapsulamento para garantir que seu estado seja alterado de forma consistente.
 */
public class Mesa {
    private int numero;
    private int capacidade;
    private StatusMesa status;

    /**
     * Construtor padrão da Mesa.
     * Por padrão, uma mesa recém-criada inicia no status LIVRE.
     *
     * @param numero     Número identificador da mesa (deve ser único no sistema).
     * @param capacidade Quantidade máxima de assentos da mesa.
     */
    public Mesa(int numero, int capacidade) {
        this.numero = numero;
        this.capacidade = capacidade;
        this.status = StatusMesa.LIVRE;
    }

    /**
     * Construtor completo para restauração de dados da persistência.
     *
     * @param numero     Número identificador da mesa.
     * @param capacidade Quantidade máxima de assentos da mesa.
     * @param status     Estado atual da mesa.
     */
    public Mesa(int numero, int capacidade, StatusMesa status) {
        this.numero = numero;
        this.capacidade = capacidade;
        this.status = status;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public StatusMesa getStatus() {
        return status;
    }

    public void setStatus(StatusMesa status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Mesa %d [Capacidade: %d | Status: %s]", numero, capacidade, status);
    }
}
