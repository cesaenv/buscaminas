package Cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClienteTerminal {
	public static void main(String [] args) {
		Socket servidor = null;
		DataInputStream din = null;
		DataOutputStream dout = null;
		
		Scanner scanner = null;
		try {
			servidor= new Socket(InetAddress.getLocalHost().getHostAddress(), 6666);
			din = new DataInputStream(servidor.getInputStream());
			dout = new DataOutputStream(servidor.getOutputStream());
			scanner = new Scanner(System.in);
			
			jugar(din,dout,scanner);	//IOException
			
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(servidor != null) {
					servidor.close();
				}
				if(din != null) {
					din.close();
				}
				if(dout != null) {
					dout.close();
				}
				if(scanner != null) {
					scanner.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 	}
	private static void jugar(DataInputStream din, DataOutputStream dout, Scanner scanner) throws IOException {
		String nuevoJuego = "", casilla = "", opcion = "";
		String lineaBlanco = "\n";
		String enviar = "";
		
		int tamanoTablero, tamanoLineas;
		
		//ENVIAR QUE SE JUEGA EN TERMINAL
		dout.writeBytes(("TERMINAL" + lineaBlanco));
		dout.flush();

		System.out.println("Introducir tamaño del tablero: ");
		//ENVIAR TAMAÑO TABLERO
		while(!tamanoCorrecto(enviar = scanner.nextLine())) {
			System.out.println("Tamaño erróneo, vuelve a introducir");
		}
		tamanoTablero = Integer.parseInt(enviar);
		dout.writeBytes((tamanoTablero + lineaBlanco));
		dout.flush();
		
		//RECIBIR RESPUESTA
		tamanoLineas = Integer.parseInt(din.readLine());
		for(int i=0; i<tamanoLineas ;i++) {
			System.out.println(din.readLine());
		}
		
		//ENVIAR PRIMERA CASILLA
		while(!casillaCorrecta(casilla = scanner.nextLine(), tamanoTablero)){
			System.out.println("Casilla errónea, vuelva a introducir");
		}
		dout.writeBytes((casilla + lineaBlanco));
		dout.flush();
		
		//RECIBIR RESPUESTA
		tamanoLineas = Integer.parseInt(din.readLine());
		for(int i=0; i<tamanoLineas ;i++) {
			System.out.println(din.readLine());
		}
		
		while(true) {
			//ENVIAR OPCION
			while(!tamanoCorrecto(opcion = scanner.nextLine()) || Integer.parseInt(opcion)>4 || Integer.parseInt(opcion)<1) {
				System.out.println("Opción errónea, vuelva a introducir");
			}
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
					jugar(din,dout,scanner);
				}
				break;
			}
			
			//RECIBIR RESPUESTA
			System.out.println(din.readLine());
			
			//ENVIAR CASILLA
			while(!casillaCorrecta(casilla = scanner.nextLine(),tamanoTablero)){
				System.out.println("Casilla errónea, vuelva a introducir");
			}
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
					jugar(din,dout,scanner);
				}
				break;
			}
		}
 	}
	
	private static boolean tamanoCorrecto(String s) {
		boolean dev = true;
		try {
			int n = Integer.parseInt(s);
			if (n<5){
				System.out.println("No se acepta un tablero tan pequeño");
			}
		}catch(NumberFormatException e) {
			dev = false;
		}finally {
			return dev;
		}
	}
	private static boolean casillaCorrecta(String s, int tamanoTablero) {
		String [] casilla = s.split("-");
		if(casilla.length == 2) {
			if(tamanoCorrecto(casilla[0]) && tamanoCorrecto(casilla[1])){
				int fila = Integer.parseInt(casilla[0]);
				int columna = Integer.parseInt(casilla[1]);
				return (fila >= 0 && fila<tamanoTablero &&
						columna >= 0 && columna<tamanoTablero);
			}
		}
		return false;
	}
	
	
//    public static void main(String[] args) {
//        BuscaminasJuego buscaminas = new BuscaminasJuego(8);
//        buscaminas.iniciarTablero(0, 0);
//		  buscaminas.jugar();
//    }
}

