import MesaDAO from './dao/MesaDAO.js';
import MesaService from './service/MesaService.js';
import MesaController from './controller/MesaController.js';
import ItemCardapio from './model/ItemCardapio.js';
import StatusMesa from './model/StatusMesa.js';

// 1. Instanciação da Arquitetura em Camadas
const dao = new MesaDAO();
const service = new MesaService(dao);
const controller = new MesaController(service);

// 2. Definição do Cardápio Padrão do Restaurante
const cardapio = [
    new ItemCardapio(101, "Fettuccine Alfredo com Camarão", "Fettuccine com molho cremoso à base de manteiga, queijo parmesão e camarões grelhados.", 89.90, ["Lactose", "Crustáceos", "Glúten"]),
    new ItemCardapio(102, "Filé Mignon ao Molho Madeira", "Filé mignon grelhado regado ao molho madeira clássico, servido com arroz e fritas.", 95.00, ["Glúten"]),
    new ItemCardapio(103, "Salmão Grelhado com Ervas", "Posta de salmão grelhada ao molho de ervas finas, servida com purê de batatas.", 85.00, ["Peixes", "Lactose"]),
    new ItemCardapio(201, "Suco Natural de Laranja", "Suco natural espremido na hora.", 12.00, []),
    new ItemCardapio(202, "Pudim Santini", "Pudim de leite condensado tradicional com calda de caramelo especial.", 22.00, ["Lactose", "Ovos"]),
    new ItemCardapio(203, "Petit Gâteau", "Bolinho de chocolate quente com recheio cremoso, servido com sorvete de creme.", 28.00, ["Glúten", "Lactose", "Ovos"])
];

// 3. Estado Local da Interface de Usuário
let mesaSelecionadaId = null;
let itensPedidoCorrente = [];

// 4. Elementos do DOM
const mesasGrid = document.getElementById('mesas-grid');
const tabButtons = document.querySelectorAll('.tab-btn');
const tabContents = document.querySelectorAll('.tab-content');

// Garçom DOM
const garcomMesaInfo = document.getElementById('garcom-mesa-info');
const cardapioItemsContainer = document.getElementById('cardapio-items');
const itensPedidoLista = document.getElementById('itens-pedido-lista');
const valorTotalPedido = document.getElementById('valor-total-pedido');
const restricoesCampo = document.getElementById('restricoes-campo');
const btnEnviarPedido = document.getElementById('btn-enviar-pedido');

// Cozinha DOM
const kdsFila = document.getElementById('kds-fila');

// Caixa DOM
const caixaMesaInfo = document.getElementById('caixa-mesa-info');
const tabelaConsumoCorpo = document.getElementById('tabela-consumo-corpo');
const caixaTotalValor = document.getElementById('caixa-total-valor');
const splitPessoas = document.getElementById('split-pessoas');
const splitIndividualValor = document.getElementById('split-individual-valor');
const btnGerarPagamento = document.getElementById('btn-gerar-pagamento');
const caixaPagamentoArea = document.getElementById('caixa-pagamento-area');
const btnConfirmarPagamento = document.getElementById('btn-confirmar-pagamento');

// Limpeza DOM
const limpezaLista = document.getElementById('limpeza-lista');

// Modal Alérgenos DOM
const alergenosModal = document.getElementById('alergenos-modal');
const alergenosModalMensagem = document.getElementById('alergenos-modal-mensagem');
const btnFecharModalAlergenos = document.getElementById('btn-fechar-modal-alergenos');

// 5. Inicialização da Aplicação
document.addEventListener('DOMContentLoaded', () => {
    configurarNavegacaoAbas();
    renderizarMesas();
    renderizarCardapio();
    atualizarPainelKDS();
    atualizarPainelLimpeza();
    
    btnFecharModalAlergenos.addEventListener('click', () => {
        alergenosModal.classList.remove('show');
        // Uma vez confirmado pelo garçom, prossegue com o envio do pedido
        concluirEnvioPedido(true);
    });

    btnEnviarPedido.addEventListener('click', () => {
        validarEEnviarPedido();
    });

    splitPessoas.addEventListener('input', calcularSplitConta);

    btnGerarPagamento.addEventListener('click', () => {
        caixaPagamentoArea.style.display = 'block';
        showToast("Sucesso", "QR Code Pix gerado para pagamento!", "sucesso");
    });

    btnConfirmarPagamento.addEventListener('click', () => {
        confirmarPagamentoCaixa();
    });
});

// --- Navegação e Renderização ---

function configurarNavegacaoAbas() {
    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const tabId = btn.getAttribute('data-tab');
            
            tabButtons.forEach(b => b.classList.remove('active'));
            tabContents.forEach(c => c.classList.remove('active'));
            
            btn.classList.add('active');
            document.getElementById(`tab-${tabId}`).classList.add('active');

            // Atualiza dados ao mudar de aba
            atualizarPainelKDS();
            atualizarPainelLimpeza();
            atualizarPainelCaixa();
        });
    });
}

function renderizarMesas() {
    mesasGrid.innerHTML = '';
    const mesas = controller.obterMesas();
    
    mesas.forEach(mesa => {
        const card = document.createElement('div');
        card.className = `mesa-card ${mesa.status.toLowerCase()}`;
        if (mesa.numero === mesaSelecionadaId) {
            card.classList.add('active');
        }
        
        card.innerHTML = `
            <div class="numero">${mesa.numero}</div>
            <div class="capacidade">Capacidade: ${mesa.capacidade} p.</div>
            <span class="badge">${mesa.status.replace('_', ' ')}</span>
        `;
        
        card.addEventListener('click', () => {
            selecionarMesa(mesa);
        });
        
        mesasGrid.appendChild(card);
    });
}

function selecionarMesa(mesa) {
    mesaSelecionadaId = mesa.numero;
    renderizarMesas();

    // Atualiza cabeçalhos dos perfis
    garcomMesaInfo.textContent = `Mesa ${mesa.numero} (${mesa.status.replace('_', ' ')})`;
    caixaMesaInfo.textContent = `Mesa ${mesa.numero} (${mesa.status.replace('_', ' ')})`;

    // Reseta form do garçom para novos lançamentos
    itensPedidoCorrente = [];
    restricoesCampo.value = '';
    atualizarComandaGarcom();

    if (mesa.status === StatusMesa.LIVRE) {
        // Se a mesa está livre, simula abertura automática de atendimento para agilizar
        const res = controller.abrirMesa(mesa.numero);
        if (res.sucesso) {
            showToast("Mesa Aberta", res.mensagem, "sucesso");
            renderizarMesas();
            garcomMesaInfo.textContent = `Mesa ${mesa.numero} (OCUPADA)`;
        }
    }

    atualizarPainelCaixa();
}

// --- Fluxo do Garçom ---

function renderizarCardapio() {
    cardapioItemsContainer.innerHTML = '';
    cardapio.forEach(item => {
        const div = document.createElement('div');
        div.className = 'cardapio-item';
        div.innerHTML = `
            <h4>${item.nome}</h4>
            <p style="font-size: 0.75rem; color: var(--color-text-muted); margin: 0.2rem 0;">${item.descricao}</p>
            <div class="preco">R$ ${item.preco.toFixed(2)}</div>
            ${item.alergenos.length > 0 ? `<span class="alergenos-tag">Contém: ${item.alergenos.join(', ')}</span>` : ''}
        `;
        
        div.addEventListener('click', () => {
            adicionarItemAoPedido(item);
        });
        
        cardapioItemsContainer.appendChild(div);
    });
}

function adicionarItemAoPedido(item) {
    if (!mesaSelecionadaId) {
        showToast("Aviso", "Selecione uma mesa no mapa antes de adicionar itens.", "alerta");
        return;
    }
    const mesa = dao.buscarPorNumero(mesaSelecionadaId);
    if (mesa.status !== StatusMesa.OCUPADA) {
        showToast("Bloqueado", "A mesa deve estar OCUPADA para receber pedidos.", "erro");
        return;
    }

    itensPedidoCorrente.push(item);
    atualizarComandaGarcom();
}

function removerItemDoPedido(index) {
    itensPedidoCorrente.splice(index, 1);
    atualizarComandaGarcom();
}

function atualizarComandaGarcom() {
    itensPedidoLista.innerHTML = '';
    let total = 0;
    
    itensPedidoCorrente.forEach((item, index) => {
        const li = document.createElement('li');
        li.innerHTML = `
            <span>${item.nome}</span>
            <span>R$ ${item.preco.toFixed(2)} <span class="remover" data-index="${index}"><i class="fa-solid fa-trash"></i></span></span>
        `;
        itensPedidoLista.appendChild(li);
        total += item.preco;
    });

    // Event listeners para remoção
    itensPedidoLista.querySelectorAll('.remover').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const idx = parseInt(btn.getAttribute('data-index'));
            removerItemDoPedido(idx);
        });
    });

    valorTotalPedido.textContent = `R$ ${total.toFixed(2)}`;
    btnEnviarPedido.disabled = (itensPedidoCorrente.length === 0 || !mesaSelecionadaId);
}

function validarEEnviarPedido() {
    const restricoes = restricoesCampo.value.trim().toLowerCase();
    
    // Análise de Alérgenos (US02)
    let contemAlergenosPerigosos = false;
    let alergenosEncontrados = [];

    if (restricoes.length > 0) {
        itensPedidoCorrente.forEach(item => {
            item.alergenos.forEach(alergeno => {
                if (restricoes.includes(alergeno.toLowerCase())) {
                    contemAlergenosPerigosos = true;
                    alergenosEncontrados.push(`${item.nome} (${alergeno})`);
                }
            });
        });
    }

    if (contemAlergenosPerigosos) {
        // Exibe o Alerta Crítico de Segurança Alimentar (US02)
        alergenosModalMensagem.innerHTML = `
            O cliente declarou restrições alimentares. Porém, o pedido contém ingredientes perigosos:<br><br>
            <strong style="color: var(--color-accent);">${alergenosEncontrados.join('<br>')}</strong><br><br>
            Por favor, confirme se a equipe da cozinha foi devidamente avisada sobre o preparo especial ou mude os itens.
        `;
        alergenosModal.classList.add('show');
    } else {
        concluirEnvioPedido(false);
    }
}

function concluirEnvioPedido(teveAlertaConfirmado) {
    const restricoesVal = restricoesCampo.value;
    const res = controller.fazerPedidoMesa(mesaSelecionadaId, itensPedidoCorrente, restricoesVal);
    
    if (res.sucesso) {
        showToast(
            "Pedido Enviado", 
            `Pedido #${res.payload.id} enviado para a fila da Cozinha!${teveAlertaConfirmado ? " (Com alerta de alérgenos confirmado pelo garçom)" : ""}`, 
            teveAlertaConfirmado ? "alerta" : "sucesso"
        );
        
        // Reseta comanda digital
        itensPedidoCorrente = [];
        restricoesCampo.value = '';
        atualizarComandaGarcom();
        atualizarPainelKDS();
    } else {
        showToast("Erro", res.mensagem, "erro");
    }
}

// --- Fluxo da Cozinha (KDS) ---

function atualizarPainelKDS() {
    kdsFila.innerHTML = '';
    const pedidos = dao.listarTodosPedidos();
    // Filtra pedidos que estão na cozinha (RECEBIDO, EM_PREPARO, PRONTO)
    const pedidosCozinha = pedidos.filter(p => p.status !== 'PAGO' && p.status !== 'ENTREGUE');
    
    if (pedidosCozinha.length === 0) {
        kdsFila.innerHTML = `
            <div class="action-box" style="grid-column: 1 / -1;">
                <i class="fa-solid fa-list-check"></i>
                <p>Nenhum pedido pendente na fila de preparação.</p>
            </div>
        `;
        return;
    }

    pedidosCozinha.forEach(p => {
        const card = document.createElement('div');
        const possuiRestricao = p.restricoesAlimentares && p.restricoesAlimentares.trim().length > 0;
        card.className = `kds-card ${possuiRestricao ? 'tem-restricao' : ''}`;
        
        // Simulação do SLA regressivo
        const minutosPassados = Math.floor((Date.now() - p.timestamp) / 60000);
        const tempoRestante = Math.max(0, p.tempoPreparoMinutos - minutosPassados);

        card.innerHTML = `
            <div class="kds-header">
                <span class="id">Pedido #${p.id}</span>
                <span class="mesa-num">Mesa ${p.mesa.numero}</span>
            </div>
            <div class="sla-badge">
                <i class="fa-solid fa-hourglass-half"></i> SLA Restante: ${tempoRestante} min
            </div>
            
            ${possuiRestricao ? `
                <div class="kds-restricoes-alerta">
                    <i class="fa-solid fa-triangle-exclamation"></i> RESTRIÇÃO: ${p.restricoesAlimentares.toUpperCase()}
                </div>
            ` : ''}
            
            <ul class="kds-itens">
                ${p.itens.map(item => `
                    <li>
                        <i class="fa-solid fa-circle-chevron-right" style="font-size:0.75rem; color: var(--color-primary);"></i>
                        ${item.nome}
                        ${item.alergenos.length > 0 ? `<br><small style="color:var(--color-accent); font-size:0.7rem;">Alérgenos: ${item.alergenos.join(', ')}</small>` : ''}
                    </li>
                `).join('')}
            </ul>

            <div style="margin-top: 1rem;">
                ${p.status === 'RECEBIDO' ? `
                    <button class="btn-primary" onclick="alterarStatusPedido(${p.id}, 'EM_PREPARO')">Iniciar Preparo</button>
                ` : `
                    <button class="btn-accent" onclick="alterarStatusPedido(${p.id}, 'PRONTO')">Finalizar Preparo</button>
                `}
            </div>
        `;
        kdsFila.appendChild(card);
    });
}

// Vincula a função de alterar status do pedido ao escopo global (para funcionar no onclick dos botões dinâmicos)
window.alterarStatusPedido = (pedidoId, novoStatus) => {
    const pedidos = dao.listarTodosPedidos();
    const pedido = pedidos.find(p => p.id === pedidoId);
    
    if (pedido) {
        pedido.status = novoStatus;
        dao.salvarPedido(pedido);
        
        if (novoStatus === 'PRONTO') {
            showToast(
                "Prato Pronto!", 
                `🔔 [Notificação ao Garçom] O Pedido #${pedido.id} da Mesa ${pedido.mesa.numero} está pronto! O dispositivo vibrou.`, 
                "alerta"
            );
            // Simula entrega automática do prato na mesa
            pedido.status = 'ENTREGUE';
            dao.salvarPedido(pedido);
        } else {
            showToast("Cozinha", `Preparo iniciado para o Pedido #${pedido.id}`, "sucesso");
        }
        
        atualizarPainelKDS();
        atualizarPainelCaixa();
    }
};

// --- Fluxo do Caixa ---

function atualizarPainelCaixa() {
    tabelaConsumoCorpo.innerHTML = '';
    caixaTotalValor.textContent = 'R$ 0,00';
    btnGerarPagamento.disabled = true;
    caixaPagamentoArea.style.display = 'none';

    if (!mesaSelecionadaId) {
        tabelaConsumoCorpo.innerHTML = `
            <tr>
                <td colspan="2" class="action-box" style="padding: 2rem 0;">Selecione uma mesa ocupada para ver a conta</td>
            </tr>
        `;
        return;
    }

    const pedido = dao.buscarPedidoPorMesa(mesaSelecionadaId);
    if (!pedido || pedido.itens.length === 0) {
        tabelaConsumoCorpo.innerHTML = `
            <tr>
                <td colspan="2" class="action-box" style="padding: 2rem 0;">Nenhum consumo registrado para a Mesa ${mesaSelecionadaId}</td>
            </tr>
        `;
        return;
    }

    pedido.itens.forEach(item => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${item.nome}</td>
            <td style="text-align: right;">R$ ${item.preco.toFixed(2)}</td>
        `;
        tabelaConsumoCorpo.appendChild(tr);
    });

    const total = pedido.itens.reduce((soma, it) => soma + it.preco, 0);
    caixaTotalValor.textContent = `R$ ${total.toFixed(2)}`;
    btnGerarPagamento.disabled = false;
    
    calcularSplitConta();
}

function calcularSplitConta() {
    const totalTexto = caixaTotalValor.textContent.replace('R$ ', '').replace(',', '.');
    const total = parseFloat(totalTexto) || 0;
    const pessoas = parseInt(splitPessoas.value) || 1;
    
    const valorIndividual = total / pessoas;
    splitIndividualValor.textContent = `R$ ${valorIndividual.toFixed(2)}`;
}

function confirmarPagamentoCaixa() {
    const res = controller.fecharContaELiberarMesa(mesaSelecionadaId);
    if (res.sucesso) {
        showToast("Caixa", res.mensagem, "sucesso");
        renderizarMesas();
        
        // Atualiza a visualização
        mesaSelecionadaId = null;
        atualizarPainelCaixa();
        atualizarPainelLimpeza();
    } else {
        showToast("Erro", res.mensagem, "erro");
    }
}

// --- Fluxo de Limpeza (Check-out de Higienização) ---

function atualizarPainelLimpeza() {
    limpezaLista.innerHTML = '';
    const mesas = controller.obterMesas();
    const mesasAguardando = mesas.filter(m => m.status === StatusMesa.AGUARDANDO_LIMPEZA);

    if (mesasAguardando.length === 0) {
        limpezaLista.innerHTML = `
            <div class="action-box">
                <i class="fa-solid fa-circle-check" style="color: var(--status-livre);"></i>
                <p>Todas as mesas estão higienizadas e prontas para uso!</p>
            </div>
        `;
        return;
    }

    mesasAguardando.forEach(m => {
        const div = document.createElement('div');
        div.style.display = 'flex';
        div.style.justify = 'space-between';
        div.style.alignItems = 'center';
        div.style.padding = '1rem';
        div.style.background = 'rgba(255,255,255,0.02)';
        div.style.border = '1px solid rgba(234, 179, 8, 0.2)';
        div.style.borderRadius = '8px';

        div.innerHTML = `
            <div>
                <strong style="color:#fff; font-size:1.1rem;">Mesa ${m.numero}</strong>
                <span style="font-size:0.8rem; color:var(--color-text-muted); margin-left: 1rem;">(Capacidade: ${m.capacidade} pessoas)</span>
            </div>
            <button class="btn-accent" style="width:auto; padding: 0.5rem 1.5rem;" onclick="confirmarLimpezaMesa(${m.numero})">
                <i class="fa-solid fa-broom"></i> Confirmar Higienização
            </button>
        `;
        limpezaLista.appendChild(div);
    });
}

window.confirmarLimpezaMesa = (numeroMesa) => {
    const res = controller.confirmarLimpezaMesa(numeroMesa);
    if (res.sucesso) {
        showToast("Limpeza", res.mensagem, "sucesso");
        renderizarMesas();
        atualizarPainelLimpeza();
    } else {
        showToast("Erro", res.mensagem, "erro");
    }
};

// --- Sistema de Toasts ---

function showToast(titulo, mensagem, tipo = 'sucesso') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${tipo}`;
    
    let icone = 'fa-circle-check';
    if (tipo === 'erro') icone = 'fa-circle-xmark';
    if (tipo === 'alerta') icone = 'fa-bell';

    toast.innerHTML = `
        <i class="fa-solid ${icone}"></i>
        <div>
            <strong style="display:block; font-size:0.85rem; color:#fff;">${titulo}</strong>
            <span style="font-size:0.75rem;">${mensagem}</span>
        </div>
    `;
    
    container.appendChild(toast);
    
    // Remove após 5 segundos com transição
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        toast.style.transition = 'all 0.5s ease-out';
        setTimeout(() => toast.remove(), 500);
    }, 5000);
}
