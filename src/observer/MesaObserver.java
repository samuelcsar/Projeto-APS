package observer;

import model.Mesa;

/**
 * Interface que define o contrato para os observadores das alterações de estado das mesas.
 */
public interface MesaObserver {
    /**
     * Método chamado sempre que o status de uma mesa é alterado.
     * 
     * @param mesa A mesa que sofreu a alteração.
     */
    void onMesaStatusChanged(Mesa mesa);
}
