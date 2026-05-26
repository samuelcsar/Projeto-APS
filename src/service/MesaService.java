package service;

import dao.MesaDAO;
import model.Mesa;
import model.StatusMesa;

import java.util.List;

/**
 * Camada de Negócio que gerencia as operações e regras associadas às mesas do restaurante.
 * Aplica os conceitos de acoplamento fraco ao depender estritamente da interface {@link MesaDAO}
 * em vez de uma implementação concreta de banco de dados.
 */
public class MesaService {

    private final MesaDAO mesaDAO;

    /**
     * Construtor que recebe a abstração de persistência (Injeção de Dependência).
     *
     * @param mesaDAO Implementação da persistência que satisfaz o contrato.
     */
    public MesaService(MesaDAO mesaDAO) {
        this.mesaDAO = mesaDAO;
    }

    /**
     * Inicia o atendimento em uma mesa, alterando seu status para OCUPADA.
     * Regra de negócio: A mesa só pode ser aberta se estiver atualmente LIVRE.
     *
     * @param numeroMesa O número identificador da mesa.
     * @throws IllegalStateException Caso a mesa não esteja livre para atendimento.
     * @throws IllegalArgumentException Caso a mesa informada não exista.
     */
    public void abrirAtendimento(int numeroMesa) {
        Mesa mesa = mesaDAO.buscarPorNumero(numeroMesa);
        if (mesa == null) {
            throw new IllegalArgumentException("Mesa número " + numeroMesa + " não encontrada.");
        }

        if (mesa.getStatus() != StatusMesa.LIVRE) {
            throw new IllegalStateException("Não é possível abrir atendimento. A mesa " 
                    + numeroMesa + " está atualmente no status: " + mesa.getStatus());
        }

        mesa.setStatus(StatusMesa.OCUPADA);
        mesaDAO.atualizar(mesa);
        System.out.println("[Service Log] Atendimento iniciado na Mesa " + numeroMesa + ".");
    }

    /**
     * Libera uma mesa após o pagamento da conta, enviando-a para a fila de higienização.
     * Regra de negócio: A mesa deve estar OCUPADA para ser enviada à higienização.
     *
     * @param numeroMesa O número identificador da mesa.
     * @throws IllegalStateException Caso a mesa não esteja ocupada.
     */
    public void fecharContaELiberarParaLimpeza(int numeroMesa) {
        Mesa mesa = mesaDAO.buscarPorNumero(numeroMesa);
        if (mesa == null) {
            throw new IllegalArgumentException("Mesa número " + numeroMesa + " não encontrada.");
        }

        if (mesa.getStatus() != StatusMesa.OCUPADA) {
            throw new IllegalStateException("Apenas mesas ocupadas podem ter a conta fechada e ser enviadas para limpeza. Status atual: " 
                    + mesa.getStatus());
        }

        mesa.setStatus(StatusMesa.AGUARDANDO_LIMPEZA);
        mesaDAO.atualizar(mesa);
        System.out.println("[Service Log] Pagamento confirmado. Mesa " + numeroMesa + " enviada para Higienização.");
    }

    /**
     * Executa a regra de negócio do "Check-out de Higienização".
     * Regra de negócio: Uma mesa que foi desocupada só pode retornar ao status LIVRE
     * após a equipe de higienização realizar o trabalho físico e registrar a conclusão no sistema.
     *
     * @param numeroMesa O número identificador da mesa a ser higienizada.
     * @throws IllegalStateException Caso a mesa não esteja no estado de AGUARDANDO_LIMPEZA.
     */
    public void confirmarHigienizacao(int numeroMesa) {
        Mesa mesa = mesaDAO.buscarPorNumero(numeroMesa);
        if (mesa == null) {
            throw new IllegalArgumentException("Mesa número " + numeroMesa + " não encontrada.");
        }

        // Validação da regra de negócio de segurança do restaurante
        if (mesa.getStatus() != StatusMesa.AGUARDANDO_LIMPEZA) {
            throw new IllegalStateException("Regra violada: A mesa " + numeroMesa 
                    + " não pode ser liberada pois não está aguardando limpeza. Status atual: " + mesa.getStatus());
        }

        // Transição permitida pelo fluxo de higienização física confirmada
        mesa.setStatus(StatusMesa.LIVRE);
        mesaDAO.atualizar(mesa);
        System.out.println("[Service Log] Check-out de Higienização concluído para a Mesa " 
                + numeroMesa + ". A mesa está agora LIVRE para novos clientes.");
    }

    /**
     * Busca uma mesa específica por número.
     *
     * @param numeroMesa O número da mesa.
     * @return O objeto {@link Mesa} localizado.
     */
    public Mesa buscarMesa(int numeroMesa) {
        return mesaDAO.buscarPorNumero(numeroMesa);
    }

    /**
     * Retorna a listagem completa de mesas registradas.
     *
     * @return Lista contendo todas as mesas.
     */
    public List<Mesa> listarTodasAsMesas() {
        return mesaDAO.listarTodas();
    }
    
    /**
     * Adiciona uma nova mesa ao sistema.
     * 
     * @param mesa A mesa a ser adicionada.
     */
    public void cadastrarMesa(Mesa mesa) {
        if (mesaDAO.buscarPorNumero(mesa.getNumero()) != null) {
            throw new IllegalArgumentException("Mesa de número " + mesa.getNumero() + " já está cadastrada.");
        }
        mesaDAO.salvar(mesa);
    }
}
