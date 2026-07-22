package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representa um Combo (refeição composta) utilizando o padrão Composite.
 */
public class ComboCardapio implements ComponenteCardapio {
    private String nome;
    private String descricao;
    private List<ComponenteCardapio> itens;
    private double descontoExtra;

    public ComboCardapio(String nome, String descricao, double descontoExtra) {
        this.nome = nome;
        this.descricao = descricao;
        this.descontoExtra = descontoExtra;
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(ComponenteCardapio item) {
        this.itens.add(item);
    }

    public void removerItem(ComponenteCardapio item) {
        this.itens.remove(item);
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public double getPreco() {
        double total = 0.0;
        for (ComponenteCardapio item : itens) {
            total += item.getPreco();
        }
        return Math.max(0, total - descontoExtra);
    }

    @Override
    public List<String> getAlergenos() {
        return itens.stream()
                .flatMap(item -> item.getAlergenos().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public boolean contemAlergeno(String alergeno) {
        for (ComponenteCardapio item : itens) {
            if (item.contemAlergeno(alergeno)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s (Combo) (R$ %.2f) - Alérgenos: %s", nome, getPreco(), getAlergenos().isEmpty() ? "Nenhum" : getAlergenos());
    }
}
