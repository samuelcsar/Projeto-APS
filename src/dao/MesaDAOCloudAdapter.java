package dao;

import model.Mesa;
import model.StatusMesa;
import thirdparty.CloudMesaAPI;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MesaDAOCloudAdapter implements MesaDAO {

    private final CloudMesaAPI cloudAPI;

    public MesaDAOCloudAdapter() {
        this.cloudAPI = new CloudMesaAPI();
    }

    @Override
    public void salvar(Mesa mesa) {
        cloudAPI.insertTable(String.valueOf(mesa.getNumero()), mesa.getCapacidade());
        cloudAPI.syncTableState(String.valueOf(mesa.getNumero()), mapToCloudState(mesa.getStatus()));
    }

    @Override
    public Mesa buscarPorNumero(int numero) {
        Map<String, Object> data = cloudAPI.fetchTable(String.valueOf(numero));
        if (data == null) {
            return null;
        }
        return mapToMesa(data);
    }

    @Override
    public void atualizar(Mesa mesa) {
        cloudAPI.syncTableState(String.valueOf(mesa.getNumero()), mapToCloudState(mesa.getStatus()));
    }

    @Override
    public List<Mesa> listarTodas() {
        return cloudAPI.fetchAllTables().stream()
                .map(this::mapToMesa)
                .collect(Collectors.toList());
    }

    private String mapToCloudState(StatusMesa status) {
        switch (status) {
            case LIVRE: return "FREE";
            case OCUPADA: return "OCCUPIED";
            case AGUARDANDO_LIMPEZA: return "CLEANING";
            default: return "FREE";
        }
    }

    private StatusMesa mapFromCloudState(String state) {
        switch (state) {
            case "FREE": return StatusMesa.LIVRE;
            case "OCCUPIED": return StatusMesa.OCUPADA;
            case "CLEANING": return StatusMesa.AGUARDANDO_LIMPEZA;
            default: return StatusMesa.LIVRE;
        }
    }

    private Mesa mapToMesa(Map<String, Object> data) {
        int numero = Integer.parseInt((String) data.get("id"));
        int capacidade = (int) data.get("seats");
        StatusMesa status = mapFromCloudState((String) data.get("state"));
        return new Mesa(numero, capacidade, status);
    }
}
