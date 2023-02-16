// Della Matera Lorenzo 5E

import java.util.HashMap;

public class Clients {
    private static HashMap<String, LampClient> clients = new HashMap<String, LampClient>();

    public static boolean put(String key, LampClient client){
        if(clients.containsKey(key)) return false;
        clients.put(key, client);
        return true;
    }

    public static void remove(String key){
        clients.remove(key);
    }

    public static LampClient get(String key){
        return clients.get(key);
    }
}
