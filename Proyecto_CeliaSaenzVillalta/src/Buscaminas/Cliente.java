package Buscaminas;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
	public static void main(String [] args) {
		Socket servidor = null;
		DataInputStream din = null;
		DataOutputStream dout = null;
		
		try {
			servidor= new Socket(InetAddress.getLocalHost().getHostAddress(), 6666);
			din = new DataInputStream(servidor.getInputStream());
			dout = new DataOutputStream(servidor.getOutputStream());
			
			jugar(din,dout);	//IOException
			
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				servidor.close();
				din.close();
				dout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 	}
	private static void jugar(DataInputStream din, DataOutputStream dout) throws IOException {
		Scanner scanner = new Scanner(System.in);
		
		String nuevoJuego = "", casilla = "", opcion = "";
		String lineaBlanco = "\n";
		
		int tamanoTablero, tamanoLineas;
			
		//ENVIAR TAMAÑO TABLERO
		tamanoTablero = Integer.parseInt(scanner.nextLine());
		dout.writeBytes((tamanoTablero + lineaBlanco));
		dout.flush();
		
		//RECIBIR RESPUESTA
		tamanoLineas = Integer.parseInt(din.readLine());
		for(int i=0; i<tamanoLineas ;i++) {
			System.out.println(din.readLine());
		}
		
		//ENVIAR PRIMERA CASILLA
		casilla = scanner.nextLine();
		dout.writeBytes((casilla + lineaBlanco));
		dout.flush();
		
		//RECIBIR RESPUESTA
		tamanoLineas = Integer.parseInt(din.readLine());
		for(int i=0; i<tamanoLineas ;i++) {
			System.out.println(din.readLine());
		}
		
		while(true) {
			//ENVIAR OPCION
			opcion = scanner.nextLine();
			dout.writeBytes(opcion + lineaBlanco);
			dout.flush();
			
			if(Integer.parseInt(opcion)==4) {
				//RECIBIR RESPUESTA
				tamanoLineas = Integer.parseInt(din.readLine());
				for(int i=0; i<tamanoLineas ;i++) {
					System.out.println(din.readLine());
				}
				
				//VOLVER A JUGAR
				nuevoJuego = scanner.nextLine();
				dout.writeBytes(nuevoJuego + lineaBlanco);
				dout.flush();
				if(nuevoJuego.equalsIgnoreCase("Y")) {
					jugar(din,dout);
				}
				break;
			}
			
			//RECIBIR RESPUESTA
			System.out.println(din.readLine());
			
			//ENVIAR CASILLA
			casilla = scanner.nextLine();
			dout.writeBytes(casilla + lineaBlanco);
			dout.flush();
			
			//RECIBIR RESPUESTA
			tamanoLineas = Integer.parseInt(din.readLine());				
			if(tamanoLineas > 0) {	//CONTINUA 
				for(int i=0; i<tamanoLineas ;i++) {
					System.out.println(din.readLine());
				}
			}else {	//ACABADO
				tamanoLineas = Integer.parseInt(din.readLine());
				for(int i=0; i<tamanoLineas ;i++) {
					System.out.println(din.readLine());
				}
				
				//VOLVER A JUGAR
				nuevoJuego = scanner.nextLine();
				dout.writeBytes(nuevoJuego + lineaBlanco);
				dout.flush();
				if(nuevoJuego.equalsIgnoreCase("Y")) {
					jugar(din,dout);
				}
				break;
			}
			
		}
 	}
	
	
//    public static void main(String[] args) {
//        BuscaminasJuego buscaminas = new BuscaminasJuego(8);
//        buscaminas.iniciarTablero(0, 0);
//		  buscaminas.jugar();
//    }
}

