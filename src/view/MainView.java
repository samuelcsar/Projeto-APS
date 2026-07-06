package view;

import controller.MesaController;
import dao.MesaDAO;
import dao.MesaDAOSQLite;
import model.*;
import service.MesaService;
import observer.MesaObserver;

import java.util.Arrays;
import java.util.List;

/**
 * Ponto de entrada (Main) e Camada de Visualização do sistema Santini Gourmet.
 * Realiza uma simulação completa e interativa que demonstra as regras de negócio
 * e o funcionamento integrado das camadas Model-DAO-Service-Controller.
 */
public class MainView {

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("         SANTINI GOURMET - SISTEMA DE GESTÃO OPERACIONAL                 ");
        System.out.println("                      Simulação do Happy Path                            ");
        System.out.println("=========================================================================\n");

        // 1. Inicialização da Infraestrutura e Camadas (Inversão de Controle manual)
        System.out.println("[Passo 1] Inicializando Camadas e Persistência...");
        MesaDAO mesaDAO = new MesaDAOSQLite(); // Carrega SQLite ou ativa fallback em memória
        MesaService mesaService = new MesaService(mesaDAO);
        MesaController mesaController = new MesaController(mesaService);

        // Registrando um Observer para demonstrar o Padrão Observer
        mesaService.adicionarObserver(new MesaObserver() {
            @Override
            public void onMesaStatusChanged(Mesa mesa) {
                System.out.println("\n[OBSERVER NOTIFICADO] O status da mesa " + mesa.getNumero() + " mudou para " + mesa.getStatus());
            }
        });

        System.out.println("-> Camadas prontas. Instâncias injetadas via Construtor.\n");

        // 2. Apresentação do Painel Inicial de Mesas
        exibirPainel(mesaController);

        // 3. Simulação da Jornada do Cliente (Happy Path)
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("Fase 1: Chegada do Cliente e Abertura da Mesa pelo Garçom");
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Ação: Cliente é acomodado na Mesa 1.");
        String feedbackAbertura = mesaController.abrirMesa(1);
        System.out.println("Feedback do Controlador: " + feedbackAbertura);
        
        // Tentativa de abrir a mesma mesa para mostrar consistência
        System.out.println("\nTentativa inválida de abrir uma mesa já ocupada:");
        String erroReabrir = mesaController.abrirMesa(1);
        System.out.println("Feedback do Controlador: " + erroReabrir);
        System.out.println("-------------------------------------------------------------------------");

        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("Fase 2: Registro de Pedido com Segurança de Alérgenos");
        System.out.println("-------------------------------------------------------------------------");
        // Criando Itens do Cardápio para teste
        ItemCardapio fettuccine = new ItemCardapio(101, "Fettuccine Alfredo com Camarão", 
                "Fettuccine com molho cremoso à base de manteiga, queijo parmesão e camarões grelhados.", 
                89.90, Arrays.asList("Lactose", "Crustáceos", "Glúten"));

        ItemCardapio pudim = new ItemCardapio(202, "Pudim Santini", 
                "Pudim de leite condensado tradicional com calda de caramelo especial.", 
                22.00, Arrays.asList("Lactose", "Ovos"));

        System.out.println("Prato Selecionado: " + fettuccine.getNome());
        System.out.println("-> Alérgenos mapeados: " + fettuccine.getAlergenos());

        // Criando o Pedido
        Mesa mesa1 = mesaService.buscarMesa(1);
        // Garçom informa restrição do cliente na comanda digital
        Pedido pedido1 = new Pedido(5001, mesa1, 20, "Cliente possui intolerância grave a Lactose");
        pedido1.adicionarItem(fettuccine);
        pedido1.adicionarItem(pudim);

        System.out.println("\nPedido Gerado: " + pedido1);
        
        // Simulação do Alerta de Segurança Alimentar (US02)
        if (pedido1.possuiRestricoesOuAlergenos()) {
            System.out.println("\n⚠️  [ALERTA DE SEGURANÇA ALIMENTAR PARA O GARÇOM] ⚠️");
            System.out.println("Atenção! Este pedido contém ingredientes potencialmente perigosos:");
            for (ItemCardapio item : pedido1.getItens()) {
                System.out.println(" - " + item.getNome() + ": " + item.getAlergenos());
            }
            System.out.println("Restrição Declarada pelo Cliente: \"" + pedido1.getRestricoesAlimentares() + "\"");
        }
        System.out.println("-------------------------------------------------------------------------");

        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("Fase 3: KDS - Cozinha (Fila de Preparação e Destaque de Restrição)");
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("KDS Display:");
        System.out.println("PEDIDO ID: " + pedido1.getId() + " | MESA: " + pedido1.getMesa().getNumero());
        System.out.println("SLA - TEMPO DE PREPARO LIMITE: " + pedido1.getTempoPreparoMinutos() + " minutos");
        System.out.println("ITENS A PREPARAR:");
        for (ItemCardapio item : pedido1.getItens()) {
            System.out.println("  - " + item.getNome());
        }
        // Destaque em Vermelho das Restrições na Cozinha (Simulação visual textual)
        System.out.println("\n\u001B[31m[KDS ALERTA CRÍTICO DE PREPARO] RESTRIÇÃO EXIBIDA EM VERMELHO NO MONITOR DA COZINHA:\u001B[0m");
        System.out.println("\u001B[31m>>> ALERTA: " + pedido1.getRestricoesAlimentares().toUpperCase() + " <<<\u001B[0m");
        
        // Chef conclui o preparo
        pedido1.setStatus(StatusPedido.PRONTO);
        System.out.println("\nCozinheiro sinaliza conclusão do preparo.");
        System.out.println(">>> [VIBRAÇÃO NO DISPOSITIVO DO GARÇOM] Notificação: O pedido da Mesa " 
                + pedido1.getMesa().getNumero() + " está PRONTO para servir! <<<");
        pedido1.setStatus(StatusPedido.ENTREGUE);
        System.out.println("Garçom entrega o pedido na mesa.");
        System.out.println("-------------------------------------------------------------------------");

        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("Fase 4: Caixa (Divisão de Conta, Pagamento e Bloqueio de Mesa)");
        System.out.println("-------------------------------------------------------------------------");
        double totalConta = pedido1.calcularTotal();
        System.out.println("Consumo Total da Mesa 1: R$ " + String.format("%.2f", totalConta));
        
        // Simulação do Split de Contas (US09)
        int quantidadePessoas = 2;
        double valorPorPessoa = totalConta / quantidadePessoas;
        System.out.println("Simulando Split de Conta por Pessoa (Dividido para " + quantidadePessoas + " pessoas):");
        System.out.println("-> Valor por pessoa: R$ " + String.format("%.2f", valorPorPessoa));
        
        System.out.println("\nAção: Gerando QR Code para pagamento via PIX...");
        System.out.println("  [QR CODE SIMULADO - PAGAMENTO SANTINI GOURMET]  ");
        System.out.println("  [ https://santinigourmet.com.br/pix/mesa1/pay ]  ");
        
        // Caixa confirma pagamento
        pedido1.setStatus(StatusPedido.PAGO);
        System.out.println("\nPagamento confirmado eletronicamente.");
        
        // Mesa é enviada para higienização
        String feedbackFechamento = mesaController.fecharContaELiberarMesa(1);
        System.out.println("Feedback do Controlador: " + feedbackFechamento);
        
        // Tentativa de abrir a mesa enquanto ela está aguardando limpeza (Demonstrando a trava)
        System.out.println("\nTentativa inválida de acomodar novos clientes antes da higienização física:");
        String erroOcuparLimpeza = mesaController.abrirMesa(1);
        System.out.println("Feedback do Controlador: " + erroOcuparLimpeza);
        System.out.println("-------------------------------------------------------------------------");

        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("Fase 5: Equipe de Limpeza - Check-out de Higienização");
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Ação: Equipe de Limpeza realiza a higienização física e envia confirmação.");
        
        String feedbackLimpeza = mesaController.confirmarLimpezaMesa(1);
        System.out.println("Feedback do Controlador: " + feedbackLimpeza);
        
        System.out.println("\nTentativa de higienizar uma mesa que já está livre (Validação de transição):");
        String erroDuplaLimpeza = mesaController.confirmarLimpezaMesa(1);
        System.out.println("Feedback do Controlador: " + erroDuplaLimpeza);
        System.out.println("-------------------------------------------------------------------------");

        // 4. Exibição do Painel Final de Mesas
        System.out.println();
        exibirPainel(mesaController);
        
        System.out.println("\n=========================================================================");
        System.out.println("                FIM DA SIMULAÇÃO - SANTINI GOURMET                       ");
        System.out.println("=========================================================================");
    }

    private static void exibirPainel(MesaController controller) {
        System.out.println("=== PAINEL DE MONITORAMENTO DAS MESAS (TEMPO REAL) ===");
        List<Mesa> mesas = controller.obterPainelMesas();
        for (Mesa mesa : mesas) {
            System.out.println("  " + mesa);
        }
        System.out.println("=======================================================");
    }
}
