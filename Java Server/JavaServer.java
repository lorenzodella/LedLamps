import java.io.*;
// Della Matera Lorenzo 5E

import java.net.*;


public class JavaServer{
	private ServerSocket canale_ser;
	private Keychain keychain;
	
	public JavaServer() {
		//sem = new Semaphore(1);
		keychain = new Keychain();
		try {
			canale_ser=new ServerSocket(12345);
			start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		try{
			while(true) {
				System.out.println("\n________________Waiting for connections on port 12345...________________");
				Socket socket = canale_ser.accept();
				new LampClient(socket, keychain);
			}
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
}
