package controller;

import model.Mesa;
import service.MesaService;
import java.util.List;

/**
 * Classe Controladora responsável por receber as requisições da View relacionadas a mesas,
 * invocar as regras de negócio adequadas na camada {@link MesaService} e gerenciar
 * as respostas e tratamento de erros do fluxo.
 */
public class MesaController {

    private final MesaService mesaService;

    /**
     * Construtor do controlador.
     *
     * @param mesaService Serviço de negócio injetado.
     */
    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    /**
     * Comanda a abertura de uma mesa para atendimento.
     *
     * @param numero O número da mesa que se deseja abrir.
     * @return String de feedback da operação (sucesso ou erro).
     */
    public String abrirMesa(int numero) {
        try {
            mesaService.abrirAtendimento(numero);
            return "Sucesso: Mesa " + numero + " está agora Ocupada!";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "Erro ao abrir mesa: " + e.getMessage();
        }
    }

    /**
     * Comanda o encerramento da conta e o envio da mesa para a higienização.
     *
     * @param numero O número da mesa.
     * @return String de feedback da operação.
     */
    public String fecharContaELiberarMesa(int numero) {
        try {
            mesaService.fecharContaELiberarParaLimpeza(numero);
            return "Sucesso: Conta da Mesa " + numero + " fechada. Mesa enviada para limpeza!";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "Erro ao fechar conta: " + e.getMessage();
        }
    }

    /**
     * Executa a confirmação da limpeza física de uma mesa (Check-out de Higienização).
     *
     * @param numero O número da mesa higienizada.
     * @return String de feedback da operação.
     */
    public String confirmarLimpezaMesa(int numero) {
        try {
            mesaService.confirmarHigienizacao(numero);
            return "Sucesso: A higienização da Mesa " + numero + " foi confirmada. A mesa está livre!";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "Erro ao confirmar limpeza: " + e.getMessage();
        }
    }

    /**
     * Retorna a listagem atualizada de mesas em formato amigável.
     *
     * @return Lista contendo os objetos Mesa.
     */
    public List<Mesa> obterPainelMesas() {
        return mesaService.listarTodasAsMesas();
    }
}
