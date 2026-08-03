package view;

import controller.MesaController;
import dao.MesaDAO;
import facade.AtendimentoFacade;
import facade.ReciboDTO;
import model.*;
import observer.MesaObserver;
import service.ContaService;
import service.MesaService;
import strategy.DivisaoIgualitariaStrategy;
import strategy.EstrategiaDivisaoConta;
import factory.DAOFactory;
import factory.CardapioFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Ponto de entrada (Main) e Camada de Visualização do sistema Santini Gourmet.
 * Realiza uma simulação completa e interativa que demonstra as regras de negócio
 * e o funcionamento integrado das camadas e padrões de projeto (Facade, Strategy, Adapter, Observer).
 */
public class MainView {

    // Variáveis estáticas para manter o estado da simulação
    private static MesaController mesaController;
    private static AtendimentoFacade facade;
    private static MesaService mesaService;

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("         SANTINI GOURMET - SISTEMA DE GESTÃO OPERACIONAL                 ");
        System.out.println("             Simulação do Happy Path (Versão Integrada)                  ");
        System.out.println("=========================================================================\n");

        try {
            // Fase de Inicialização e Integração dos Padrões
            inicializarSistema();
            exibirPainel();

            // Execução das Fases do Atendimento
            simularFase1_ChegadaCliente();
            Pedido pedido = simularFase2_Pedido();
            simularFase3_Cozinha(pedido);
            simularFase4_Caixa(pedido);
            simularFase5_Limpeza();

            // Finalização
            System.out.println();
            exibirPainel();
            System.out.println("\n=========================================================================");
            System.out.println("                FIM DA SIMULAÇÃO - SANTINI GOURMET                       ");
            System.out.println("=========================================================================");

        } catch (Exception e) {
            System.err.println("\n[ERRO CRÍTICO] Ocorreu uma falha inesperada no sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void inicializarSistema() {
        System.out.println("[Passo 1] Inicializando Camadas e Persistência...");

        // PADRÃO DAO / PERSISTÊNCIA: Utiliza o SQLite com fallback em memória para inicializar as mesas padrão.
        MesaDAO mesaDAO = DAOFactory.getMesaDAO();

        mesaService = new MesaService(mesaDAO);
        ContaService contaService = new ContaService();

        mesaController = new MesaController(mesaService);

        // PADRÃO FACADE: Integrado para orquestrar as requisições complexas no momento do Caixa.
        facade = new AtendimentoFacade(mesaService, contaService);

        // PADRÃO OBSERVER: Mantido para demonstrar reatividade.
        mesaService.adicionarObserver(mesa -> {
            System.out.println("\n[OBSERVER] Status da mesa " + mesa.getNumero() + " alterado para: " + mesa.getStatus());
        });

        System.out.println("-> Componentes (Adapter, Facade, Strategy, Observer) integrados com sucesso.\n");
    }

    private static void exibirPainel() {
        System.out.println("=== PAINEL DE MONITORAMENTO DAS MESAS (TEMPO REAL) ===");
        List<Mesa> mesas = mesaController.obterPainelMesas();
        for (Mesa mesa : mesas) {
            System.out.println("  " + mesa);
        }
        System.out.println("=======================================================");
    }

    private static void simularFase1_ChegadaCliente() {
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("Fase 1: Chegada do Cliente e Abertura da Mesa pelo Garçom");
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Ação: Cliente é acomodado na Mesa 1.");
        System.out.println("Feedback: " + mesaController.abrirMesa(1));

        System.out.println("\nTeste de Validação: Tentativa de reabrir a mesa já ocupada:");
        System.out.println("Feedback: " + mesaController.abrirMesa(1));
    }

    private static Pedido simularFase2_Pedido() {
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("Fase 2: Registro de Pedido com Segurança de Alérgenos (Composite)");
        System.out.println("-------------------------------------------------------------------------");

        ComponenteCardapio fettuccine = CardapioFactory.criarItem(101, "Fettuccine Alfredo", "Massa ao molho cremoso com camarão", 89.90, Arrays.asList("Lactose", "Crustáceos", "Glúten"));
        ComponenteCardapio pudim = CardapioFactory.criarItem(202, "Pudim Santini", "Pudim tradicional", 22.00, Arrays.asList("Lactose", "Ovos"));

        ComboCardapio comboCasal = CardapioFactory.criarCombo("Combo Casal Especial", "2 Pratos + Sobremesa", 10.00);
        comboCasal.adicionarItem(fettuccine);
        comboCasal.adicionarItem(fettuccine);
        comboCasal.adicionarItem(pudim);

        Mesa mesa1 = mesaService.buscarMesa(1);
        Pedido pedido = new Pedido(5001, mesa1, 20, "Cliente possui intolerância grave a Lactose");
        pedido.adicionarItem(comboCasal);

        System.out.println("Pedido Gerado: " + pedido);

        if (pedido.possuiRestricoesOuAlergenos()) {
            System.out.println("\n⚠️  [ALERTA DE SEGURANÇA ALIMENTAR PARA O GARÇOM] ⚠️");
            for (ComponenteCardapio item : pedido.getItens()) {
                System.out.println(" - " + item.getNome() + ": " + item.getAlergenos());
            }
            System.out.println("Restrição Declarada pelo Cliente: \"" + pedido.getRestricoesAlimentares() + "\"");
        }
        return pedido;
    }

    private static void simularFase3_Cozinha(Pedido pedido) {
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("Fase 3: KDS - Cozinha (Fila de Preparação e Destaque de Restrição)");
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("SLA - TEMPO DE PREPARO LIMITE: " + pedido.getTempoPreparoMinutos() + " minutos");
        System.out.println("\n\u001B[31m[KDS ALERTA CRÍTICO] RESTRIÇÃO EXIBIDA EM VERMELHO NO MONITOR DA COZINHA:\u001B[0m");
        System.out.println("\u001B[31m>>> ALERTA: " + pedido.getRestricoesAlimentares().toUpperCase() + " <<<\u001B[0m");

        pedido.setStatus(StatusPedido.PRONTO);
        System.out.println("\n>>> [BEEP DISPOSITIVO GARÇOM] O pedido da Mesa " + pedido.getMesa().getNumero() + " está PRONTO para servir! <<<");
        pedido.setStatus(StatusPedido.ENTREGUE);
    }

    private static void simularFase4_Caixa(Pedido pedido) {
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("Fase 4: Caixa (Integração de Facade e Strategy)");
        System.out.println("-------------------------------------------------------------------------");

        // PADRÃO STRATEGY: Escolha dinâmica do método de divisão de conta.
        EstrategiaDivisaoConta estrategia = new DivisaoIgualitariaStrategy();
        int qtdPessoas = 2;

        System.out.println("Ação: Cliente solicita divisão da conta para " + qtdPessoas + " pessoas.");

        // PADRÃO FACADE: Orquestrando ContaService e MesaService em uma única chamada simplificada
        ReciboDTO recibo = facade.finalizarAtendimento(1, pedido, qtdPessoas, estrategia);

        System.out.println("\n   [QR CODE PIX GERADO NA TELA DO CAIXA]   ");
        System.out.println("\n=============== RECIBO ===============");
        System.out.println("Mesa: " + recibo.getNumeroMesa());
        System.out.println("Total Geral: R$ " + String.format("%.2f", recibo.getTotalPedido()));
        System.out.println("Valor por pessoa (" + qtdPessoas + "x): R$ " + String.format("%.2f", recibo.getDivisaoPorPessoa()[0]));
        System.out.println("======================================");

        pedido.setStatus(StatusPedido.PAGO);
    }

    private static void simularFase5_Limpeza() {
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("Fase 5: Equipe de Limpeza - Check-out de Higienização");
        System.out.println("-------------------------------------------------------------------------");
        
        System.out.println("Teste de Validação: Tentativa de acomodar clientes antes da limpeza física:");
        System.out.println("Feedback: " + mesaController.abrirMesa(1));

        System.out.println("\nAção: Equipe de Limpeza realiza a higienização física e envia confirmação no terminal.");
        System.out.println("Feedback: " + mesaController.confirmarLimpezaMesa(1));
    }
}
