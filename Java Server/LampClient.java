// Della Matera Lorenzo 5E

import java.io.*;
import java.net.*;
import java.util.*;
import javax.crypto.spec.*;

public class LampClient {
    private Socket socket;
    private PrintStream out;
    private BufferedReader in;
    private Keychain keychain;
    private AESKey AESkey;
    private DBConnector dbConnector;
    private Automator automator;

    private int restart;
    
    public LampClient(Socket socket, Keychain keychain){
        this.socket = socket;
        this.keychain = keychain;
        this.dbConnector = new DBConnector();
        
        try {
            socket.setSoTimeout(5000);
            System.out.print("Connected!");
            out = new PrintStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new ReadThread().start();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void saveJSONdata(String frase) {
        System.out.print("["+AESkey.getSha1key()+"] ");
		System.out.println("receive: "+frase);
		if(dbConnector.queryUpdate(frase, AESkey.getSha1key())){
            System.out.print("["+AESkey.getSha1key()+"] ");
            System.out.println("*saved on db*");
        }
    }

    public void send(String data) {
        dbConnector.lock(AESkey.getSha1key());
		try {
			String len = data.length() + "$";
			out.print(len+AES.encrypt(data, AESkey.getKey(), AESkey.getIvParameterSpec()));
            System.out.print("["+AESkey.getSha1key()+"] ");
			System.out.println("sent: "+data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public void restart() {
		restart = 5;
	}

    public void startAutomator(int idAutomation){
        System.out.print("["+AESkey.getSha1key()+"] ");
        LinkedList<Action> list = dbConnector.querySelectAutomation(idAutomation);
        if(list.size()>0 && dbConnector.queryActivateAutomation(idAutomation)){
            automator = new Automator(list, this);
            automator.start();
            System.out.println(" => automation "+idAutomation+" started");
        }
    }
    public void updateAutomator(int idAutomation){
        if(automator!=null){
            LinkedList<Action> list = dbConnector.querySelectAutomation(idAutomation);
            if(list.size()>0)
                automator.update(list);
        }
    }
    public void stopAutomator(){
        if(automator!=null){
            dbConnector.queryDeactivateAutomation(AESkey.getSha1key());
            automator.interrupt();
            automator = null;
            System.out.print("["+AESkey.getSha1key()+"] ");
            System.out.println("automator stopped");
        }
    }
    


    class ReadThread extends Thread{

        @Override
        public void run() {
            try {
                if(handshake()){
                    System.out.println("--- Welcome " + AESkey.getSha1key() + " ---");
                    if(Clients.put(AESkey.getSha1key(), LampClient.this))
                        handle();       //loop until client disconnects
                    else
                        System.out.println("!! err: Client already connected");
                }
                else {
                    System.out.println("!! err: Handshake falied");
                }
                socket.close();
                if(AESkey!=null){
                    stopAutomator();
                    dbConnector.querySetDisconnected(AESkey.getSha1key());
                    System.out.print("["+AESkey.getSha1key()+"] ");
                    Clients.remove(AESkey.getSha1key());
				    AESkey.reset();
                }
                System.out.println(":::::disconnected:::::");
            } catch(IOException e){
                e.printStackTrace();
            }
        }

        private boolean handshake(){
            try {
                String input = in.readLine();
    
                //decode request
                AESkey = keychain.handshake_findKey(input);
                if(AESkey==null){
                    System.out.println("!! err1: no key match");
                    return false;
                }
    
                //generate random iv
                byte[] newiv = AES.getIV();
                //System.out.println("newivbyte: "+Arrays.toString(newiv));
                String response = Base64.getEncoder().encodeToString(newiv);
    
                String cipherText = AES.encrypt(response, AESkey.getKey(), AESkey.getIvParameterSpec());
                //System.out.println("cipher: "+cipherText);
                String len = response.length() + "$";
                out.print(len+cipherText);
    
                //check connession
                input = in.readLine();
                AESkey.setIvParameterSpec(new IvParameterSpec(newiv));		//new iv
                String plainText = AES.decrypt(input, AESkey.getKey(), AESkey.getIvParameterSpec());
                if(!plainText.equals(AESkey.getSha1key())){
                    System.out.println("!! err2: new key not matching"+plainText);
                    return false;
                }
    
                AESkey.setSha1key_enc(AES.encrypt(AESkey.getSha1key(), AESkey.getKey(), AESkey.getIvParameterSpec()));
                return true;
            } catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }

        private void handle(){
            String frase = "";
            while(!frase.equals("#")) {
                try {
                    frase=in.readLine();
                    if(restart<5) {
                        restart = 0;
                        System.out.print("["+AESkey.getSha1key()+"] ");
                        System.out.println("*sync* ");
                    }
                    if(frase!=null && !frase.equals(AESkey.getSha1key_enc())){
                        frase = AES.decrypt(frase, AESkey.getKey(), AESkey.getIvParameterSpec());
                        if(!frase.equals("#"))
                            saveJSONdata(frase);
                    }
                } catch(SocketTimeoutException e) {
                    restart++;
                    System.out.print("["+AESkey.getSha1key()+"] ");
                    System.out.println("restart? "+restart);
                    if(restart>=4)
                        break;
                } catch(Exception e) {
                    break;
                }
            }
        }
    }

}