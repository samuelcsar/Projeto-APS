package factory;

import dao.MesaDAO;
import dao.MesaDAOSQLite;
import dao.MesaDAOCloudAdapter;

/**
 * Factory Method para a criação de instâncias de MesaDAO.
 * Encapsula a lógica de decisão sobre qual implementação do banco de dados usar.
 */
public class DAOFactory {

    /**
     * Retorna a implementação apropriada de MesaDAO.
     * No momento, retorna a instância Singleton do SQLite.
     * Pode ser expandido futuramente para ler um arquivo de configuração 
     * e retornar a implementação Cloud, por exemplo.
     *
     * @return Implementação de MesaDAO
     */
    public static MesaDAO getMesaDAO() {
        // Exemplo: Se "usarCloud" for true, retornaria MesaDAOCloudAdapter.getInstance()
        return MesaDAOSQLite.getInstance();
    }
}
