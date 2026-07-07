# Sistema de Gestão Operacional - Santini Gourmet 🍽️

## Apresentação
Somos alunos do curso de Sistemas de Informação da Universidade Federal da Paraíba (UFPB) - Campus IV. Fazemos parte de um projeto da disciplina de Programação Orientada a Objetos, ministrada pelo Prof. Dr. Dorgival Netto, onde temos o objetivo de atender e analisar todos os requisitos de um cliente real, utilizando as diversas práticas da engenharia e arquitetura de software.

Este repositório armazena os artefatos de software, documentação e código-fonte referentes ao desenvolvimento do sistema do restaurante **Santini Gourmet**.

---

## Visão Geral do Projeto
O principal objetivo do sistema especificado pelo cliente (Santini Gourmet) é modernizar e otimizar toda a sua operação interna, substituindo as tradicionais comandas de papel e processos puramente manuais por um fluxo digital integrado de ponta a ponta. 

Atualmente, o restaurante enfrenta problemas clássicos de comunicação entre a recepção, os garçons e a cozinha. Erros na anotação de pedidos, atrasos no envio de comandas físicas à cozinha, falta de rastreamento do tempo de preparo e dificuldades na conciliação e divisão de contas na hora do pagamento são alguns dos principais gargalos operacionais.

Com o **Santini Gourmet - Sistema de Gestão Operacional**, implementamos uma plataforma robusta capaz de:
- **Agilizar os pedidos** por meio de um cardápio digital acessado pelos garçons.
- **Melhorar a segurança dos clientes** com alertas visuais integrados para restrições e alérgenos.
- **Automatizar a comunicação com a cozinha** por meio de uma fila digital de pedidos (KDS - Kitchen Display System), monitorando o tempo de preparo (SLA) em tempo real.
- **Otimizar a logística de mesas** através do controle de status em tempo real com um fluxo obrigatório de higienização ("Check-out de Higienização").
- **Agilizar o fechamento de contas** com opções flexíveis de divisão de contas por item ou por pessoa e pagamentos facilitados por QR Code.

Esta modernização reduz drasticamente o tempo de espera do cliente, elimina erros de comunicação humana e melhora a eficiência e rentabilidade global do estabelecimento.

---

## Equipe e Cliente

### Cliente
* **Restaurante:** Santini Gourmet
* **Segmento:** Restaurante de alta gastronomia e fluxo intenso

### Equipe de Desenvolvimento (Grupo 3)

| Foto | Nome | E-mail | Função / Atuação |
|:---:| :--- | :--- | :--- |
| ![Membro 1](https://via.placeholder.com/100) | *Membro 1* | *membro1@ufpb.br* | Desenvolvedor / Analista |
| ![Membro 2](https://via.placeholder.com/100) | *Membro 2* | *membro2@ufpb.br* | Desenvolvedor / Arquiteto |
| ![Membro 3](https://via.placeholder.com/100) | *Membro 3* | *membro3@ufpb.br* | Desenvolvedor / QA |
| ![Membro 4](https://via.placeholder.com/100) | *Membro 4* | *membro4@ufpb.br* | Product Owner / Designer |

---

## Padrões de Projeto Aplicados

### Observer
O padrão de projeto Observer foi incorporado à arquitetura para promover reatividade e baixo acoplamento no gerenciamento de mesas.
- **Subject:** A classe `MesaService` atua como sujeito, gerenciando o registro de observadores e disparando notificações sempre que uma mesa sofre alteração em seu estado (LIVRE, OCUPADA, AGUARDANDO_LIMPEZA).
- **Observer:** A interface `MesaObserver` estabelece o contrato para os componentes interessados em monitorar o status das mesas.
- **Aplicação Prática:** Permite que interfaces gráficas, painéis de monitoramento e terminais de atendimento sejam atualizados de forma automática e instantânea, sem a necessidade de requisições repetitivas ou acoplamento direto com as regras de negócio da mesa.

### Strategy
O padrão de projeto Strategy foi implementado para lidar com o fechamento e divisão de contas de forma flexível e escalável.
- **Context:** A classe `ContaService` atua como o contexto, recebendo a estratégia escolhida pelo usuário.
- **Strategy:** A interface `EstrategiaDivisaoConta` define o contrato para o cálculo de divisão da conta.
- **Concrete Strategies:** Implementações como `DivisaoIgualitariaStrategy` e `SemDivisaoStrategy` contêm os algoritmos específicos.
- **Aplicação Prática:** Permite que o usuário do sistema (garçom ou cliente) escolha diferentes formas de dividir a conta (por pessoa, sem divisão, etc.) no momento do pagamento, sem poluir a classe de serviço com múltiplos `if/else`, respeitando o princípio Open/Closed do SOLID.

---

## Estrutura do Repositório

O projeto é estruturado conforme a arquitetura de camadas em Java Standard Edition (Java SE) puro:

```text
├── src/
│   ├── model/         # Entidades de domínio (Mesa, Pedido, ItemCardapio)
│   ├── dao/           # Contratos e implementações de persistência (MesaDAO, MesaDAOSQLite)
│   ├── observer/      # Interfaces e implementação do padrão Observer (MesaObserver)
│   ├── strategy/      # Interfaces e implementação do padrão Strategy (EstrategiaDivisaoConta)
│   ├── service/       # Lógica de negócios (MesaService, ContaService)
│   ├── controller/    # Orquestradores de requisições e ações (MesaController)
│   └── view/          # Ponto de entrada e interface com usuário (MainView)
├── README.md          # Página inicial de apresentação do repositório
└── DOCUMENTACAO.md    # Relatório detalhado das Etapas 01 e 02 do projeto
```

## Diagrama de classes

<img width="648" height="956" alt="image" src="https://github.com/user-attachments/assets/f7158047-773f-4cf4-8164-adb1cbeecba2" />


Para mais detalhes sobre as decisões de arquitetura e a análise de requisitos completa, consulte a [Documentação das Etapas 01 e 02](DOCUMENTACAO.md).
