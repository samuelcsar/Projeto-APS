package dao;

import model.Mesa;
import java.util.List;

/**
 * Interface que define as operações de persistência para a entidade {@link Mesa}.
 * Serve como um contrato abstrato para desacoplar a camada de negócio da tecnologia de banco de dados.
 */
public interface MesaDAO {
    
    /**
     * Persiste uma nova mesa no banco de dados.
     *
     * @param mesa A entidade Mesa a ser cadastrada.
     */
    void salvar(Mesa mesa);

    /**
     * Busca uma mesa cadastrada a partir do seu número identificador único.
     *
     * @param numero O número da mesa.
     * @return O objeto {@link Mesa} correspondente ou null caso não seja encontrado.
     */
    Mesa buscarPorNumero(int numero);

    /**
     * Atualiza o estado de uma mesa já existente no banco de dados.
     * Utilizado para persistir transições de status (ex: de AGUARDANDO_LIMPEZA para LIVRE).
     *
     * @param mesa A entidade Mesa com as informações atualizadas.
     */
    void atualizar(Mesa mesa);

    /**
     * Recupera todas as mesas cadastradas no restaurante.
     *
     * @return Uma lista contendo todas as instâncias de {@link Mesa} armazenadas.
     */
    List<Mesa> listarTodas();
}
