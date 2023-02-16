// Della Matera Lorenzo 5E

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONObject;

public class DBConnector {
    Connection connection;

    public DBConnector(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {	e.printStackTrace(); }
    }

    private boolean connect(){
        try{
            String url="jdbc:mysql://mysql-lorenzodellamatera.mysql.database.azure.com:3306/ledlamps?useSSL=true&";
            connection = DriverManager.getConnection(url+"user=lorenzodellamatera&password=D_ellamateral_0");
            SQLWarning warning = connection.getWarnings();
            if(warning==null){
                return true;
            }
            else {
                System.out.println("SQLWarning: " + warning);
                return false;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean queryActivateAutomation(int idAutomation){
        if(connect()){
            try{
                Statement stmt = connection.createStatement();
                String querySQL = "UPDATE automations SET " +
                            "isActive = true " +
                        "WHERE idAutomation = " + idAutomation + ";";

                if(stmt.executeUpdate(querySQL)!=1){
                    connection.close();
                    return false;
                }
                
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        else return false;
    }

    public boolean queryDeactivateAutomation(String idLamp){
        if(connect()){
            try{
                Statement stmt = connection.createStatement();
                String querySQL = "UPDATE automations SET " +
                            "isActive = false " +
                        "WHERE idUser IN (" +
                        "SELECT idUser FROM users WHERE idLamp = '" + idLamp + "');";

                if(stmt.executeUpdate(querySQL)!=1){
                    connection.close();
                    return false;
                }
                
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        else return false;
    }

    public LinkedList<Action> querySelectAutomation(int idAutomation){
        if(connect()){
            LinkedList<Action> actions = new LinkedList<Action>();
            ResultSet results;
            try{
                Statement stmt = connection.createStatement();
                String querySQL = "SELECT idAutomation, modeId, custom, fade1, fade2, time, modeLinkParam"+
                                    " FROM actions INNER JOIN modes USING(modeId) WHERE idAutomation = "+idAutomation+" ORDER BY position;";

                System.out.print("SQLQuery: "+querySQL);
                results = stmt.executeQuery(querySQL);
                while (results.next()) {	
                    actions.add(new Action(
                        results.getInt("idAutomation"),
                        results.getInt("modeId"),
                        results.getString("modeLinkParam"),
                        results.getString("custom"),
                        results.getString("fade1"),
                        results.getString("fade2"),
                        results.getInt("time")
                    ));
                }	
                
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return actions;
        }
        else return null;
    }

    public ArrayList<AESKey> querySelectKeys(){
        if(connect()){
            ArrayList<AESKey> keys = new ArrayList<AESKey>();
            ResultSet results;
            try{
                Statement stmt = connection.createStatement();
                String querySQL = "SELECT * FROM aeskeys;";

                System.out.println("SQLQuery: "+querySQL);
                results = stmt.executeQuery(querySQL);
                while (results.next()) {	
                    String sha1key = results.getString("idLamp");
                    String hexkey = results.getString("AESkey");
                    keys.add(new AESKey(sha1key, hexkey));
                }	
                
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return keys;
        }
        else return null;
    }

    public boolean querySetDisconnected(String idlamp){
        if(connect()){
            try{
                Statement stmt = connection.createStatement();
                String querySQL = "UPDATE lamps SET " +
                            "ssid = '', " +
                            "connected = 0, " +
                            "ip = '' " +
                        "WHERE idLamp = '" + idlamp + "';";

                if(stmt.executeUpdate(querySQL)!=1){
                    connection.close();
                    return false;
                }
                
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        else return false;
    }


    public void lock(String idlamp){
        if(connect()){
            try {
                Statement stmt = connection.createStatement();
                stmt.execute("START TRANSACTION;");
                stmt.execute("SELECT * FROM lamps WHERE idLamp='" + idlamp + "' FOR UPDATE;");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean queryUpdate(String jsonString, String idlamp){
        try{
            //Thread.sleep(5000);
            if(connection==null || connection.isClosed()){
                if(connect())
                    return doQuery(jsonString, idlamp);
                else
                    return false;
            }
            else return doQuery(jsonString, idlamp);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
    }

    private boolean doQuery(String jsonString, String idlamp) throws Exception{
        JSONObject jobj = new JSONObject(jsonString);
        Statement stmt = connection.createStatement();

        String querySQL = "UPDATE lamps SET " +
                            "fade1 = '" + jobj.getString("fade1") + "', " +
                            "fade2 = '" + jobj.getString("fade2") + "', " +
                            "custom = '" + jobj.getString("custom") + "', " +
                            "opMode = " + jobj.getInt("opMode") + ", " +
                            "random = " + jobj.getInt("random") + ", " +
                            "brightness = " + jobj.getInt("brightness") + ", " +
                            "ssid = '" + jobj.getString("ssid") + "', " +
                            "connected = " + jobj.getInt("connected") + ", " +
                            "ip = '" + jobj.getString("ip") + "', " +
                            "host = '" + jobj.getString("host") + "' " +
                        "WHERE idlamp = '" + idlamp + "';";

        System.out.print("["+idlamp+"] ");
        System.out.println("SQLQuery: "+querySQL);
        if(stmt.executeUpdate(querySQL)!=1){
            connection.close();
            return false;
        }
        stmt.execute("COMMIT;");
        connection.close();
        return true;
    }
}