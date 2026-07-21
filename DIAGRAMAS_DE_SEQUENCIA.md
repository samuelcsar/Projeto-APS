# Diagramas de Sequência UML - Santini Gourmet
## Documentação de Engenharia de Software e Modelagem de Objetos

**Curso:** Bacharelado em Sistemas de Informação  
**Instituição:** Universidade Federal da Paraíba (UFPB) - Campus IV  
**Disciplina:** Programação Orientada a Objetos (POO)  
**Projeto:** Sistema de Gestão Operacional Santini Gourmet  

---

## 1. Visão Geral e Mapeamento Arquitetural

Esta documentação apresenta os **Diagramas de Sequência UML** do sistema **Santini Gourmet**, detalhando a dinâmica comportamental do software, as mensagens trocadas entre objetos em tempo de execução e a estrita correspondência com a arquitetura em camadas e os requisitos (Casos de Uso / Histórias de Usuário).

### Tabela de Correspondência: Casos de Uso vs. Classes vs. Diagramas

| Caso de Uso / História de Usuário | Diagrama de Sequência Correspondente | Classes e Interfaces Envolvidas na Implementação Java |
| :--- | :--- | :--- |
| **US01** - Consulta de Cardápio Digital<br/>**US07** - Mapa de Mesas em Tempo Real | **Diagrama 1:** Abertura de Atendimento e Painel de Mesas | `MainView`, `MesaController`, `MesaService`, `MesaDAO`, `MesaDAOSQLite`, `StatusMesa`, `MesaObserver` |
| **US02** - Alertas Visuais de Alérgenos<br/>**US03** - Notificação de Prato Pronto<br/>**US04** - Fila Digital (KDS)<br/>**US05** - SLA de Preparo<br/>**US06** - Destaque de Restrições | **Diagrama 2:** Registro de Pedido, Alérgenos e Notificação da Cozinha | `MainView`, `Pedido`, `ItemCardapio`, `StatusPedido` |
| **US09** - Divisão Avançada de Contas (Split)<br/>**US10** - Pagamento via QR Code Pix | **Diagrama 3:** Fechamento de Conta e Divisão Inteligente (Strategy) | `MainView`, `ContaService`, `EstrategiaDivisaoConta`, `DivisaoIgualitariaStrategy`, `MesaController`, `MesaService` |
| **US08** - Check-out de Higienização Obrigatório | **Diagrama 4:** Trava de Segurança e Higienização de Mesa | `MainView`, `MesaController`, `MesaService`, `MesaDAO`, `MesaDAOSQLite`, `StatusMesa` |

---

## 2. Legenda e Convenções UML Utilizadas

Para garantir o **uso adequado dos elementos UML** e a **organização visual**, foram aplicadas as seguintes convenções padronizadas nos diagramas em sintaxe Mermaid:

*   **Atores (`actor`):** Entidades externas que iniciam fluxos de uso (`Garçom`, `Chef / Cozinha`, `Caixa`, `Equipe de Limpeza`).
*   **Participantes (`participant`):** Instâncias das classes das camadas `View`, `Controller`, `Service`, `DAO`, `Model` e `Strategy`.
*   **Linha de Vida (Lifeline):** Representação vertical da existência do objeto ao longo do tempo.
*   **Barra de Ativação (`activate` / `deactivate`):** Indica o período exato em que o objeto está executando um método ou processando informações na memória.
*   **Mensagem Síncrona (`->>`):** Chamada de método em que o emissor bloqueia aguardando a resposta.
*   **Mensagem de Retorno (`-->>`):** Linha tracejada indicando o retorno de dados ou término de execução de um método.
*   **Blocos Estruturados de Controle (`alt / else`, `opt`):**
    *   `alt / else`: Representa desvios condicionais e fluxos alternativos/de erro (ex: tentativa de abrir mesa ocupada vs. livre).
    *   `opt`: Fluxo opcional acionado por condições de regra de negócio (ex: alerta de alérgenos).

---

## 3. Diagramas de Sequência do Sistema

### Diagrama 1: Abertura de Atendimento e Consulta de Mesas (US01, US07)
**Descrição do Fluxo:** O Garçom inicia o atendimento de uma mesa. A solicitação trafega da `MainView` para o `MesaController` e atinge a regra de negócio no `MesaService`. O serviço consulta o banco de dados através da interface `MesaDAO` (implementada por `MesaDAOSQLite`). Caso a mesa esteja livre, seu estado é atualizado para `OCUPADA`, a alteração é persistida no SQLite e uma notificação é enviada aos objetos `MesaObserver`.

```mermaid
sequenceDiagram
    autonumber
    actor G as Garçom
    participant MV as view.MainView
    participant MC as controller.MesaController
    participant MS as service.MesaService
    participant DAO as dao.MesaDAO<br/>(MesaDAOSQLite)
    participant Obs as observer.MesaObserver

    G ->> MV: Solicita abertura de atendimento (Mesa 1)
    activate MV
    MV ->> MC: abrirMesa(1)
    activate MC
    MC ->> MS: abrirAtendimento(1)
    activate MS
    
    MS ->> DAO: buscarPorNumero(1)
    activate DAO
    DAO -->> MS: retorna objeto Mesa (Status: LIVRE)
    deactivate DAO

    alt Mesa NÃO cadastrada ou Status != LIVRE (Exceção de Regra)
        MS -->> MC: throw IllegalArgumentException / IllegalStateException
        MC -->> MV: Retorna mensagem de erro formatada
        MV -->> G: Exibe mensagem de erro na interface
    else Mesa cadastrada e Status == LIVRE (Happy Path)
        MS ->> MS: mesa.setStatus(StatusMesa.OCUPADA)
        MS ->> DAO: atualizar(mesa)
        activate DAO
        DAO -->> MS: Confirmação de persistência em banco
        deactivate DAO
        
        MS ->> Obs: onMesaStatusChanged(mesa)
        activate Obs
        Note over Obs: Notifica alteração de status em tempo real
        Obs -->> MS: 
        deactivate Obs
        
        MS -->> MC: Retorno com sucesso
        deactivate MS
        MC -->> MV: "Sucesso: Mesa 1 está agora Ocupada!"
        deactivate MC
        MV -->> G: Exibe confirmação de abertura no painel
        deactivate MV
    end
```

---

### Diagrama 2: Registro de Pedido, Alérgenos e Notificação da Cozinha (US02, US03, US04, US05, US06)
**Descrição do Fluxo:** O Garçom vincula itens do cardápio ao pedido. O modelo `Pedido` valida se os alérgenos descritos nos itens (`ItemCardapio`) entram em conflito com a restrição declarada pelo cliente. O pedido é enviado à fila da Cozinha (KDS), que monitora o SLA de tempo de preparo. Ao concluir, o Chef atualiza o pedido para `PRONTO`, disparando um alerta tátil (vibração) no terminal do Garçom.

```mermaid
sequenceDiagram
    autonumber
    actor G as Garçom
    actor C as Chef (Cozinha)
    participant MV as view.MainView
    participant Ped as model.Pedido
    participant Item as model.ItemCardapio

    G ->> MV: Registra pedido com restrição ("Intolerância a Lactose")
    activate MV
    Note over MV: Instancia Pedido(5001, mesa1, 20min, "Intolerância...")
    MV ->> Ped: adicionarItem(fettuccine)
    activate Ped
    Ped -->> MV: Item adicionado
    deactivate Ped

    MV ->> Ped: possuiRestricoesOuAlergenos()
    activate Ped
    Ped ->> Item: getAlergenos()
    activate Item
    Item -->> Ped: List("Lactose", "Crustáceos", "Glúten")
    deactivate Item
    Ped -->> MV: true
    deactivate Ped

    opt Se possuiRestricoesOuAlergenos() == true (US02 / US06)
        Note over MV: Exibe Alerta de Segurança Alimentar para Garçom e KDS<br/>"⚠️ ALERTA: LACTOSE DETECTADA"
    end

    MV ->> C: Dispara pedido para Fila Digital do KDS (US04)
    deactivate MV
    activate C
    Note over C: KDS exibe cronômetro regressivo com SLA (US05)
    
    C ->> Ped: setStatus(StatusPedido.PRONTO)
    activate Ped
    Ped -->> C: Status atualizado
    deactivate Ped
    
    Note over C: Sinalização de conclusão do prato
    C -->> G: >>> [VIBRAÇÃO NO DISPOSITIVO DO GARÇOM] Pedido Pronto! <<< (US03)
    deactivate C
    
    G ->> Ped: setStatus(StatusPedido.ENTREGUE)
    activate G
    activate Ped
    Ped -->> G: Pedido entregue ao cliente
    deactivate Ped
    deactivate G
```

---

### Diagrama 3: Fechamento de Conta, Divisão (Strategy) e QR Code Pix (US09, US10)
**Descrição do Fluxo:** Ao solicitar a conta, o Caixa invoca o `ContaService` utilizando a estratégia `DivisaoIgualitariaStrategy` (Padrão Strategy). Calculada a divisão por pessoa, a `MainView` simula a emissão do QR Code Pix. Após a baixa do pagamento, o `MesaController` fecha a conta e atualiza o estado da mesa no banco SQLite para `AGUARDANDO_LIMPEZA`.

```mermaid
sequenceDiagram
    autonumber
    actor CX as Caixa
    participant MV as view.MainView
    participant CS as service.ContaService
    participant Strat as strategy.EstrategiaDivisaoConta<br/>(DivisaoIgualitariaStrategy)
    participant Ped as model.Pedido
    participant MC as controller.MesaController
    participant MS as service.MesaService
    participant DAO as dao.MesaDAO

    CX ->> MV: Solicita fechamento e divisão da conta em 2 pessoas
    activate MV
    MV ->> Ped: calcularTotal()
    activate Ped
    Ped -->> MV: Retorna valor total (ex: R$ 111.90)
    deactivate Ped

    MV ->> CS: calcularDivisaoConta(pedido, 2, divisaoIgualitariaStrategy)
    activate CS
    CS ->> Strat: calcularDivisao(pedido, 2)
    activate Strat
    Strat -->> CS: Retorna double[] (R$ 55.95 por pessoa)
    deactivate Strat
    CS -->> MV: Retorna valores divididos
    deactivate CS
    
    Note over MV: Gera QR Code PIX dinâmico na tela (US10)
    
    CX ->> MV: Confirma pagamento eletrônico
    MV ->> Ped: setStatus(StatusPedido.PAGO)
    activate Ped
    Ped -->> MV: 
    deactivate Ped

    MV ->> MC: fecharContaELiberarMesa(1)
    activate MC
    MC ->> MS: fecharContaELiberarParaLimpeza(1)
    activate MS
    
    MS ->> DAO: buscarPorNumero(1)
    activate DAO
    DAO -->> MS: retorna objeto Mesa (Status: OCUPADA)
    deactivate DAO

    MS ->> MS: mesa.setStatus(StatusMesa.AGUARDANDO_LIMPEZA)
    MS ->> DAO: atualizar(mesa)
    activate DAO
    DAO -->> MS: Persistência confirmada
    deactivate DAO

    MS -->> MC: Retorno de confirmação
    deactivate MS
    MC -->> MV: "Sucesso: Conta da Mesa 1 fechada. Enviada para limpeza!"
    deactivate MC
    MV -->> CX: Exibe status atualizado da mesa no painel
    deactivate MV
```

---

### Diagrama 4: Trava Operacional e Check-out de Higienização (US08)
**Descrição do Fluxo:** Demonstra a regra de negócio estrita do **Check-out de Higienização Obrigatório**. Se um garçom tentar acomodar novos clientes em uma mesa no estado `AGUARDANDO_LIMPEZA`, a camada `MesaService` bloqueia a ação lançando uma exceção. A mesa só retorna ao status `LIVRE` após o envio da confirmação pela Equipe de Limpeza.

```mermaid
sequenceDiagram
    autonumber
    actor G as Garçom
    actor L as Auxiliar de Limpeza
    participant MV as view.MainView
    participant MC as controller.MesaController
    participant MS as service.MesaService
    participant DAO as dao.MesaDAO
    participant Obs as observer.MesaObserver

    %% Cenário A: Tentativa Inválida do Garçom
    G ->> MV: Tenta abrir Mesa 1 (Aguardando Limpeza)
    activate MV
    MV ->> MC: abrirMesa(1)
    activate MC
    MC ->> MS: abrirAtendimento(1)
    activate MS
    MS ->> DAO: buscarPorNumero(1)
    activate DAO
    DAO -->> MS: retorna Mesa (Status: AGUARDANDO_LIMPEZA)
    deactivate DAO
    
    Note over MS: Validação de Regra: status != LIVRE
    MS -->> MC: throw IllegalStateException("Não é possível abrir...")
    deactivate MS
    MC -->> MV: "Erro ao abrir mesa: Mesa aguardando limpeza"
    deactivate MC
    MV -->> G: Exibe mensagem de bloqueio na tela
    deactivate MV

    %% Cenário B: Check-out de Higienização Físico Confirmado
    L ->> MV: Executa Check-out de Higienização (Mesa 1)
    activate MV
    MV ->> MC: confirmarLimpezaMesa(1)
    activate MC
    MC ->> MS: confirmarHigienizacao(1)
    activate MS
    
    MS ->> DAO: buscarPorNumero(1)
    activate DAO
    DAO -->> MS: retorna Mesa (Status: AGUARDANDO_LIMPEZA)
    deactivate DAO

    Note over MS: Validação de Regra: status == AGUARDANDO_LIMPEZA (OK)
    MS ->> MS: mesa.setStatus(StatusMesa.LIVRE)
    MS ->> DAO: atualizar(mesa)
    activate DAO
    DAO -->> MS: Persistência no SQLite efetuada
    deactivate DAO

    MS ->> Obs: onMesaStatusChanged(mesa)
    activate Obs
    Note over Obs: Notifica: Status da mesa 1 mudou para LIVRE
    Obs -->> MS: 
    deactivate Obs

    MS -->> MC: Retorno de sucesso
    deactivate MS
    MC -->> MV: "Sucesso: Higienização confirmada. Mesa livre!"
    deactivate MC
    MV -->> L: Exibe confirmação e atualiza Mapa de Mesas
    deactivate MV
```

---

## 4. Avaliação dos Critérios de Qualidade da Modelagem UML

1.  **Representação Correta das Interações entre Objetos:**
    *   Todos os métodos invocados nos diagramas (ex: `abrirAtendimento()`, `fecharContaELiberarParaLimpeza()`, `confirmarHigienizacao()`, `calcularDivisaoConta()`) existem exatamente com o mesmo nome e parâmetros nas classes Java correspondentes.
2.  **Clareza dos Fluxos de Execução:**
    *   A numeração automática (`autonumber`) permite acompanhar a ordem exata de execução vertical das mensagens de cima para baixo.
3.  **Correspondência entre Diagramas, Casos de Uso e Implementação:**
    *   Cada uma das 10 Histórias de Usuário do documento técnico foi devidamente mapeada e representada em um diagrama específico.
4.  **Uso Adequado dos Elementos UML:**
    *   Emprego rigoroso das raias de ativação (`activate`/`deactivate`), tratamento de retornos tracejados (`-->>`), anotações de estado (`Note over`) e estruturas de controle (`alt/else` para exceções e `opt` para alertas).
5.  **Organização Visual e Legibilidade:**
    *   Separação clara em 4 diagramas temáticos em sintaxe Mermaid nativa com renderização fluida no repositório.
