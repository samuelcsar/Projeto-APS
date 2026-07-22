package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade de domínio que representa um Item do Cardápio do restaurante Santini Gourmet.
 * Armazena informações do prato/bebida e sua lista de alérgenos associada para segurança alimentar.
 */
public class ItemCardapio implements ComponenteCardapio {
    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private List<String> alergenos;

    /**
     * Construtor do Item do Cardápio sem alérgenos.
     *
     * @param id        Identificador único do item.
     * @param nome      Nome do prato ou bebida.
     * @param descricao Descrição dos ingredientes e preparo.
     * @param preco     Preço de venda do item.
     */
    public ItemCardapio(int id, String nome, String descricao, double preco) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.alergenos = new ArrayList<>();
    }

    /**
     * Construtor completo do Item do Cardápio com alérgenos.
     *
     * @param id        Identificador único do item.
     * @param nome      Nome do prato ou bebida.
     * @param descricao Descrição dos ingredientes e preparo.
     * @param preco     Preço de venda do item.
     * @param alergenos Lista de alérgenos associados (ex: "Glúten", "Lactose", "Amendoim").
     */
    public ItemCardapio(int id, String nome, String descricao, double preco, List<String> alergenos) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.alergenos = alergenos != null ? alergenos : new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public List<String> getAlergenos() {
        return new ArrayList<>(alergenos); // Retorna uma cópia para preservar encapsulamento
    }

    public void setAlergenos(List<String> alergenos) {
        this.alergenos = alergenos != null ? alergenos : new ArrayList<>();
    }

    /**
     * Verifica se o item possui um alérgeno específico.
     *
     * @param alergeno Nome do alérgeno a verificar (ex: "Glúten").
     * @return true se o item contém o alérgeno, false caso contrário.
     */
    public boolean contemAlergeno(String alergeno) {
        if (alergeno == null) return false;
        for (String a : alergenos) {
            if (a.equalsIgnoreCase(alergeno)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s (R$ %.2f) - Alérgenos: %s", nome, preco, alergenos.isEmpty() ? "Nenhum" : alergenos);
    }
}
