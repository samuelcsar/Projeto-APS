package thirdparty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CloudMesaAPI {
    private static final Map<String, Map<String, Object>> cloudDatabase = new HashMap<>();

    public void insertTable(String tableId, int seats) {
        Map<String, Object> tableData = new HashMap<>();
        tableData.put("id", tableId);
        tableData.put("seats", seats);
        tableData.put("state", "FREE");
        cloudDatabase.put(tableId, tableData);
    }

    public Map<String, Object> fetchTable(String tableId) {
        return cloudDatabase.get(tableId);
    }

    public void syncTableState(String tableId, String state) {
        if (cloudDatabase.containsKey(tableId)) {
            cloudDatabase.get(tableId).put("state", state);
        }
    }

    public List<Map<String, Object>> fetchAllTables() {
        return cloudDatabase.values().stream().collect(Collectors.toList());
    }
}
