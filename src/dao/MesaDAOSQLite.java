package dao;

import model.Mesa;
import model.StatusMesa;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementação da persistência de Mesa utilizando o banco de dados leve SQLite.
 * Esta classe utiliza a especificação JDBC padrão para interagir com o arquivo do banco.
 * Contém um mecanismo de fallback em memória para assegurar o funcionamento do software
 * mesmo que o driver JDBC do SQLite não esteja presente no classpath durante a execução inicial.
 */
public class MesaDAOSQLite implements MesaDAO {

    // Esboço da String de Conexão JDBC para o banco de dados local SQLite
    private static final String URL_CONEXAO = "jdbc:sqlite:santinigourmet.db";
    private static final String DRIVER_CLASS = "org.sqlite.JDBC";
    
    private boolean usaBancoReal = true;
    
    // Banco em memória utilizado como fallback para testes rápidos e resiliência
    private static final Map<Integer, Mesa> bancoEmMemoriaFallback = new HashMap<>();

    /**
     * Construtor da classe.
     * Inicializa a estrutura de tabelas do SQLite. Caso falte o driver JDBC do SQLite,
     * ativa automaticamente o modo de simulação em memória.
     */
    public MesaDAOSQLite() {
        try {
            Class.forName(DRIVER_CLASS);
            criarTabelaSeNaoExistir();
        } catch (ClassNotFoundException e) {
            System.out.println("[SQLite DAO Warning] Driver SQLite JDBC não encontrado no classpath.");
            System.out.println("[SQLite DAO Warning] O sistema utilizará automaticamente a Persistência Temporária em Memória.");
            usaBancoReal = false;
            inicializarDadosMockFallback();
        }
    }

    /**
     * Abre uma nova conexão com o banco de dados SQLite.
     */
    private Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL_CONEXAO);
    }

    /**
     * Cria a tabela de mesas no SQLite caso ela não exista.
     */
    private void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS mesa (" +
                     "numero INTEGER PRIMARY KEY," +
                     "capacidade INTEGER NOT NULL," +
                     "status TEXT NOT NULL" +
                     ");";
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("[SQLite DAO Error] Erro ao criar tabela. Ativando fallback em memória. Erro: " + e.getMessage());
            usaBancoReal = false;
            inicializarDadosMockFallback();
        }
    }

    private void inicializarDadosMockFallback() {
        if (bancoEmMemoriaFallback.isEmpty()) {
            // Inicializa algumas mesas padrão para demonstração acadêmica
            bancoEmMemoriaFallback.put(1, new Mesa(1, 4, StatusMesa.LIVRE));
            bancoEmMemoriaFallback.put(2, new Mesa(2, 2, StatusMesa.LIVRE));
            bancoEmMemoriaFallback.put(3, new Mesa(3, 6, StatusMesa.AGUARDANDO_LIMPEZA)); // Mesa pronta para testar higienização
            bancoEmMemoriaFallback.put(4, new Mesa(4, 4, StatusMesa.OCUPADA));
        }
    }

    @Override
    public void salvar(Mesa mesa) {
        if (!usaBancoReal) {
            bancoEmMemoriaFallback.put(mesa.getNumero(), mesa);
            return;
        }

        String sql = "INSERT INTO mesa (numero, capacidade, status) VALUES (?, ?, ?);";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, mesa.getNumero());
            pstmt.setInt(2, mesa.getCapacidade());
            pstmt.setString(3, mesa.getStatus().name());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("[SQLite DAO Error] Falha ao salvar mesa: " + e.getMessage());
        }
    }

    @Override
    public Mesa buscarPorNumero(int numero) {
        if (!usaBancoReal) {
            return bancoEmMemoriaFallback.get(numero);
        }

        String sql = "SELECT numero, capacidade, status FROM mesa WHERE numero = ?;";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, numero);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int cap = rs.getInt("capacidade");
                    StatusMesa status = StatusMesa.valueOf(rs.getString("status"));
                    return new Mesa(numero, cap, status);
                }
            }
        } catch (SQLException e) {
            System.err.println("[SQLite DAO Error] Falha ao buscar mesa: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void atualizar(Mesa mesa) {
        if (!usaBancoReal) {
            bancoEmMemoriaFallback.put(mesa.getNumero(), mesa);
            return;
        }

        String sql = "UPDATE mesa SET capacidade = ?, status = ? WHERE numero = ?;";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, mesa.getCapacidade());
            pstmt.setString(2, mesa.getStatus().name());
            pstmt.setInt(3, mesa.getNumero());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("[SQLite DAO Error] Falha ao atualizar mesa: " + e.getMessage());
        }
    }

    @Override
    public List<Mesa> listarTodas() {
        if (!usaBancoReal) {
            return new ArrayList<>(bancoEmMemoriaFallback.values());
        }

        List<Mesa> mesas = new ArrayList<>();
        String sql = "SELECT numero, capacidade, status FROM mesa;";
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int num = rs.getInt("numero");
                int cap = rs.getInt("capacidade");
                StatusMesa status = StatusMesa.valueOf(rs.getString("status"));
                mesas.add(new Mesa(num, cap, status));
            }
        } catch (SQLException e) {
            System.err.println("[SQLite DAO Error] Falha ao listar mesas: " + e.getMessage());
        }
        return mesas;
    }
}
