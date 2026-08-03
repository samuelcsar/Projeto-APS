package factory;

import model.ComboCardapio;
import model.ComponenteCardapio;
import model.ItemCardapio;

import java.util.List;

/**
 * Factory Method para a criação de componentes do cardápio.
 * Encapsula a lógica de criação para o padrão Composite (Item e Combo).
 */
public class CardapioFactory {

    /**
     * Cria um item de cardápio simples.
     */
    public static ComponenteCardapio criarItem(int id, String nome, String descricao, double preco) {
        return new ItemCardapio(id, nome, descricao, preco);
    }

    /**
     * Cria um item de cardápio simples com lista de alérgenos.
     */
    public static ComponenteCardapio criarItem(int id, String nome, String descricao, double preco, List<String> alergenos) {
        return new ItemCardapio(id, nome, descricao, preco, alergenos);
    }

    /**
     * Cria um combo vazio, pronto para receber itens.
     * Retorna explicitamente um ComboCardapio para permitir o uso de adicionarItem() e removerItem().
     */
    public static ComboCardapio criarCombo(String nome, String descricao, double descontoExtra) {
        return new ComboCardapio(nome, descricao, descontoExtra);
    }
}
