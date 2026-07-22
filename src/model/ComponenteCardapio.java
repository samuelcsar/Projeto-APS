package model;

import java.util.List;

/**
 * Interface comum do padrão Composite para itens individuais e combos do cardápio.
 */
public interface ComponenteCardapio {
    String getNome();
    String getDescricao();
    double getPreco();
    List<String> getAlergenos();
    boolean contemAlergeno(String alergeno);
}
