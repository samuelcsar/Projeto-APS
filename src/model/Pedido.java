package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade de domínio que representa um Pedido no restaurante Santini Gourmet.
 * Mantém referências aos itens solicitados, à mesa de origem, ao status de preparo,
 * restrições alimentares e controle de SLA de preparação.
 */
public class Pedido {
    private int id;
    private Mesa mesa;
    private List<ItemCardapio> itens;
    private StatusPedido status;
    private int tempoPreparoMinutos; // SLA de preparação
    private String restricoesAlimentares; // Observações informadas pelo cliente

    /**
     * Construtor padrão para novos pedidos.
     * Inicia o pedido com status RECEBIDO e lista vazia de itens.
     *
     * @param id                    Identificador único do pedido.
     * @param mesa                  Mesa de origem do pedido.
     * @param tempoPreparoMinutos   Tempo limite estimado para o preparo (SLA).
     * @param restricoesAlimentares Alertas de restrições de saúde do cliente.
     */
    public Pedido(int id, Mesa mesa, int tempoPreparoMinutos, String restricoesAlimentares) {
        this.id = id;
        this.mesa = mesa;
        this.itens = new ArrayList<>();
        this.status = StatusPedido.RECEBIDO;
        this.tempoPreparoMinutos = tempoPreparoMinutos;
        this.restricoesAlimentares = restricoesAlimentares != null ? restricoesAlimentares : "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public List<ItemCardapio> getItens() {
        return new ArrayList<>(itens); // Mantém o encapsulamento
    }

    public void adicionarItem(ItemCardapio item) {
        if (item != null) {
            this.itens.add(item);
        }
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public int getTempoPreparoMinutos() {
        return tempoPreparoMinutos;
    }

    public void setTempoPreparoMinutos(int tempoPreparoMinutos) {
        this.tempoPreparoMinutos = tempoPreparoMinutos;
    }

    public String getRestricoesAlimentares() {
        return restricoesAlimentares;
    }

    public void setRestricoesAlimentares(String restricoesAlimentares) {
        this.restricoesAlimentares = restricoesAlimentares != null ? restricoesAlimentares : "";
    }

    /**
     * Calcula o valor total do pedido com base no preço de cada item.
     *
     * @return O valor total formatado em double.
     */
    public double calcularTotal() {
        double total = 0.0;
        for (ItemCardapio item : itens) {
            total += item.getPreco();
        }
        return total;
    }

    /**
     * Verifica se o pedido possui alguma restrição alimentar cadastrada
     * ou se algum item do pedido possui alérgenos declarados.
     *
     * @return true se houver restrições alimentares ou alérgenos associados.
     */
    public boolean possuiRestricoesOuAlergenos() {
        if (!restricoesAlimentares.trim().isEmpty()) {
            return true;
        }
        for (ItemCardapio item : itens) {
            if (!item.getAlergenos().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Pedido %d - Mesa %d [Status: %s | SLA: %d min | Itens: %d | Total: R$ %.2f]",
                id, mesa.getNumero(), status, tempoPreparoMinutos, itens.size(), calcularTotal());
    }
}
