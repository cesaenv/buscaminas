package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BuscaminasServidor {
	public static void main(String [] args) {
		ServerSocket serverSocket= null;
		Socket cliente = null;
		ExecutorService pool = null;
		
		try {
			serverSocket = new ServerSocket(6666); //IOException
			pool = Executors.newCachedThreadPool();

			while (true) {
				try {
	                cliente = serverSocket.accept(); //IOException
	                AtenderPeticion p = new AtenderPeticion(cliente);
	    			pool.execute(p);
				}catch(IOException e) {
					System.out.println(e);
				}
            }
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
