# Documentação Técnica e Acadêmica do Projeto: Santini Gourmet
## Sistema de Gestão Operacional de Restaurante

**Curso:** Bacharelado em Sistemas de Informação  
**Instituição:** Universidade Federal da Paraíba (UFPB) - Campus IV  
**Disciplina:** Programação Orientada a Objetos (POO)  
**Docente:** Prof. Dr. Dorgival Netto  
**Grupo:** 3  

---

## ETAPA 01 - Compreensão do Sistema

### 1. Visão Geral
O setor gastronômico de alta rotatividade exige dinamismo, precisão e controle de fluxo operacional em tempo real. No entanto, o restaurante **Santini Gourmet** ainda opera com processos analógicos, fundamentados em comandas físicas de papel e comunicação verbal desestruturada entre a linha de frente (recepção e garçons) e a retaguarda (cozinha e higienização). Esta dinâmica manual acarreta gargalos graves: erros frequentes na transcrição de pedidos, ausência de monitoramento do tempo de preparo de pratos (SLA), mesas inativas devido a falhas na comunicação do status de limpeza, e demora no checkout financeiro.

A solução proposta consiste no desenvolvimento do **Sistema de Gestão Operacional Santini Gourmet**. Trata-se de uma plataforma digital integrada baseada no paradigma de Orientação a Objetos. O sistema centraliza o controle do fluxo operacional do restaurante, eliminando o papel e promovendo a rastreabilidade absoluta de cada etapa de atendimento. Por meio de terminais digitais específicos para cada perfil de usuário, a informação transita de forma assíncrona e estruturada, reduzindo drasticamente o tempo de espera, minimizando custos decorrentes de desperdícios e garantindo a segurança alimentar dos clientes através de regras de negócio rígidas.

### 2. Atores do Sistema
Para delimitar as responsabilidades e interações no sistema, identificou-se quatro atores principais, mapeados aos perfis do estabelecimento:

*   **Garçom:** Responsável pelo atendimento direto aos clientes no salão. Utiliza o terminal móvel para consultar o cardápio digital, realizar lançamentos de pedidos vinculados às mesas, acompanhar o status de preparo e registrar restrições específicas dos clientes.
*   **Cozinha (Chef):** Responsável pela preparação dos pratos e bebidas. Utiliza o terminal KDS (*Kitchen Display System*) para visualizar a fila de pedidos pendentes por ordem cronológica, monitorar tempos de preparo recomendados e sinalizar a finalização de cada item.
*   **Limpeza / Recepção:** Responsável pela recepção de clientes, distribuição física nas mesas e manutenção da salubridade do salão. Acompanha o mapa de mesas em tempo real e atua no fluxo obrigatório de liberação de mesas.
*   **Caixa / Gerência:** Responsável pela finalização financeira do atendimento, conciliação de pagamentos e emissão de notas fiscais. O gerente também pode gerir parâmetros do sistema como cardápio e alocação de equipe.

### 3. Histórias de Usuário (User Stories)

Com o intuito de detalhar os requisitos sob a ótica dos atores identificados, foram elaboradas as seguintes Histórias de Usuário:

#### Perfil: Garçom
*   **US01 - Consulta de Cardápio Digital:** Como Garçom, eu quero visualizar um cardápio digital interativo e categorizado em meu dispositivo móvel, para que eu possa apresentar as opções disponíveis de pratos e bebidas de forma rápida aos clientes.
*   **US02 - Alertas Visuais de Alérgenos:** Como Garçom, eu quero receber alertas visuais destacados sobre alérgenos na interface de seleção de pratos no momento do pedido, para que eu possa alertar e proteger clientes com restrições alimentares declaradas.
*   **US03 - Notificação de Prato Pronto:** Como Garçom, eu quero receber uma notificação tátil (vibração do dispositivo) quando um prato de uma das minhas mesas for finalizado pela cozinha, para que eu possa entregá-lo imediatamente ao cliente enquanto está na temperatura ideal.

#### Perfil: Cozinha (KDS)
*   **US04 - Fila Digital de Pedidos (KDS):** Como Chef de Cozinha, eu quero visualizar os pedidos pendentes em uma fila digital organizada por ordem de prioridade e tempo de chegada, para que eu possa gerenciar a linha de produção de forma eficiente.
*   **US05 - SLA de Tempo de Preparo:** Como Chef de Cozinha, eu quero que o sistema exiba um cronômetro regressivo com o tempo limite (SLA) para a preparação de cada prato, para que a equipe de cozinha mantenha o padrão de agilidade do restaurante.
*   **US06 - Destaque Visual de Restrições Alimentares:** Como Cozinheiro, eu quero visualizar um destaque visual vermelho chamativo nos pratos da fila digital que possuam restrições alimentares (ex: sem glúten, sem lactose), para evitar contaminação cruzada e erros críticos na preparação.

#### Perfil: Limpeza / Recepção
*   **US07 - Mapa de Mesas em Tempo Real:** Como Recepcionista, eu quero visualizar um painel gráfico com a ocupação e o status de cada mesa em tempo real, para que eu possa alocar novos clientes nas mesas disponíveis com rapidez.
*   **US08 - Check-out de Higienização Obrigatório:** Como Auxiliar de Limpeza, eu quero visualizar no sistema quais mesas foram desocupadas e precisam de higienização, e ter a garantia de que uma mesa liberada pelo caixa só retorne ao status "Livre" após eu confirmar a higienização no sistema, assegurando o cumprimento dos protocolos de higiene e organização do restaurante.

#### Perfil: Caixa
*   **US09 - Divisão Avançada de Contas (Split):** Como Caixa do restaurante, eu quero ter a opção de dividir o valor total da conta da mesa de forma avançada (seja por valor igualitário entre os presentes ou selecionando itens específicos consumidos por cada pessoa), para agilizar a experiência de checkout de grupos de clientes.
*   **US10 - Integração de Pagamento via QR Code:** Como Caixa, eu quero gerar um QR Code dinâmico na tela do terminal para pagamentos via Pix ou carteiras digitais, permitindo que a baixa no sistema ocorra de forma automática após a confirmação bancária.

### 4. Jornada do Pedido (Happy Path)
A jornada do cliente no **Santini Gourmet** é modelada pelo seguinte fluxo de sucesso (Happy Path):
No momento em que o cliente chega ao estabelecimento, a recepção o encaminha para uma mesa cujo status é exibido como **Livre**. Ao se acomodar, o Garçom abre o atendimento no sistema (mudando o status da mesa para **Ocupada**) e registra o pedido no cardápio digital, especificando eventuais alergias alimentares (o sistema valida restrições e destaca o alerta). Imediatamente, o pedido é disparado para a fila digital do KDS na Cozinha, onde um cronômetro de SLA é iniciado e as restrições alimentares aparecem destacadas em vermelho. O Chef prepara o prato dentro do tempo previsto e, ao clicar em "Concluir" no KDS, o dispositivo portátil do Garçom vibra. O Garçom serve o prato recém-preparado ao cliente. Após a refeição, os clientes solicitam a conta; o Caixa realiza a divisão avançada dos itens consumidos e exibe o QR Code na tela. Efetuado o pagamento eletrônico, a conta da mesa é liquidada no sistema e o status da mesa é alterado automaticamente para **Aguardando Limpeza** (bloqueando novos atendimentos nessa mesa). A equipe de higienização realiza a limpeza física e, por meio do terminal móvel de limpeza, confirma a execução do "Check-out de Higienização". O sistema valida a conclusão, atualizando o status da mesa de volta para **Livre**, reiniciando com total segurança o ciclo de atendimento do Santini Gourmet.

---

## ETAPA 02 - Definição da Arquitetura

### 1. Escolha da Arquitetura
Para garantir a modularidade, facilidade de manutenção e extensibilidade exigidas em um ambiente acadêmico e profissional, o sistema adota a **Arquitetura em Camadas (Layers)**, que se apresenta como uma adaptação prática do padrão arquitetural MVC (Model-View-Controller) voltada para sistemas modulares em Java.

O código-fonte do sistema está organizado nos seguintes pacotes:

```text
  [ View (Interface de Usuário) ]
                 │
                 ▼
          [ Controller ]
                 │
                 ▼
         [ Service (Negócio) ]
                 │
                 ▼
       [ DAO (Persistência/Interface) ] ───► [ Banco de Dados SQLite ]
                 │
                 ▼
     [ Model (Entidades de Domínio) ]  (Acessível transversalmente)
```

*   **`model` (Modelo/Domínio):** Contém as classes que representam os conceitos e regras do domínio do negócio (como `Mesa`, `Pedido`, `ItemCardapio` e enums de status). São classes ricas em comportamento ou objetos de valor puros (POJOs), que contêm dados e regras locais (ex: alteração interna de estado).
*   **`dao` (Data Access Object / Persistência):** Define os contratos (interfaces) e as implementações físicas para armazenamento e recuperação de dados. Nenhuma outra camada se comunica diretamente com drivers de banco de dados ou executa comandos SQL; toda a persistência é abstraída aqui.
*   **`service` (Serviço / Lógica de Negócios):** Funciona como a camada intermediária onde residem os fluxos de trabalho coordenados e regras de negócio complexas que envolvem múltiplas entidades ou transições de estado restritas (como o processo do "Check-out de Higienização").
*   **`controller` (Controlador):** Atua como o intermediário de comunicação entre a interface do usuário (View) e as regras de negócio (Service). Ele intercepta as interações do usuário, formata e valida os dados de entrada e direciona para os métodos adequados do serviço.
*   **`view` (Apresentação):** Camada responsável pela interação com o usuário final (no escopo atual, representada por uma simulação rica via Console no terminal). Exibe informações visuais e coleta as ações de entrada.

### 2. Justificativa Técnica
A escolha de uma arquitetura modular em camadas se justifica pelos seguintes benefícios técnicos para o cenário do Santini Gourmet:

1.  **Separação de Preocupações (Separation of Concerns - SoC):** Cada classe possui uma responsabilidade única e bem definida. A interface com o usuário não precisa conhecer regras de validação fiscal do caixa, e as classes de domínio (`model`) não conhecem os detalhes de como são salvas em banco de dados.
2.  **Manutenibilidade e Testabilidade:** Alterações em uma camada não geram efeito cascata de erros em todo o sistema. Por exemplo, se a regra de divisão de conta mudar, apenas a classe correspondente na camada `service` será alterada. Da mesma forma, as classes podem ser testadas unitariamente de forma isolada, mockando-se as interfaces das camadas inferiores.
3.  **Facilidade de Substituição Tecnológica:** Se o restaurante optar por migrar a interface de terminal (Console) para uma interface gráfica desktop (JavaFX) ou uma página web, a única camada afetada será a `view` e o `controller`. Toda a lógica de negócio (`service`) e dados (`model`, `dao`) permanecerão intactos.

### 3. Padrão DAO e Baixo Acoplamento

#### O Papel das Interfaces no Padrão DAO
O acoplamento direto entre classes de negócio e código de persistência é um dos principais fatores de fragilidade em arquiteturas orientadas a objetos. Para evitar isso, aplicamos o **Padrão DAO (Data Access Object)** estruturado com **Interfaces**.

Definimos contratos genéricos (ex: `MesaDAO` como uma interface Java) contendo assinaturas de métodos puramente abstratos (`buscarPorNumero`, `salvar`, `atualizar`). A camada de negócio (`MesaService`) depende e interage **exclusivamente** com a interface `MesaDAO`. A implementação concreta (como `MesaDAOSQLite`) é injetada ou instanciada por mecanismos de controle de dependência. Isso atende ao princípio de inversão de dependência (D do SOLID): **"Módulos de alto nível não devem depender de módulos de baixo nível; ambos devem depender de abstrações"**.

Se no futuro o banco de dados precisar ser alterado do SQLite local para uma solução na nuvem (PostgreSQL, MongoDB, etc.), basta criar uma nova classe que implemente a interface original (ex: `MesaDAOPostgres`) e alterar a injeção do objeto. A camada `service` continuará operando normalmente sem que uma única linha de seu código precise ser alterada.

#### Escolha do Banco de Dados SQLite
A decisão de utilizar o **SQLite** no desenvolvimento inicial do projeto é estratégica e fundamentada nos seguintes pontos:
-   **Leveza e Portabilidade:** O SQLite não exige a instalação e configuração de um servidor de banco de dados (SGBD) complexo em background. O banco de dados é contido em um único arquivo de dados local (`santinigourmet.db`), permitindo a execução imediata do projeto em qualquer máquina apenas adicionando o driver JDBC (`.jar`) ao classpath.
-   **Conformidade com Padrões de Mercado:** O acesso ao SQLite por meio do driver JDBC do Java expõe o desenvolvedor às APIs padronizadas da JDK (`java.sql.Connection`, `java.sql.PreparedStatement`, `java.sql.ResultSet`), preparando o código perfeitamente para migrações transparentes para SGBDs corporativos de larga escala.
-   **Isolamento Transparente:** Como detalhado na arquitetura, a camada `service` nunca tem visibilidade das strings de conexão, instruções SQL ou instâncias de conexão do SQLite. Toda essa infraestrutura de baixo nível fica encapsulada e contida unicamente dentro do pacote `dao`, mantendo as regras de negócio limpas e puramente focadas no domínio do restaurante.
