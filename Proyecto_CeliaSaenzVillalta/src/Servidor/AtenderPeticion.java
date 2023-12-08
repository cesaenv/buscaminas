package Servidor;

import javax.swing.*;

import Buscaminas.BuscaminasJuego;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class AtenderPeticion implements Runnable{
	//ATRIBUTOS
	private Socket cliente;
	private BuscaminasJuego buscaminas;

	private int MAX = 10000;
	
	//CONSTRUCTORES
	public AtenderPeticion(Socket cliente) {
		this.cliente = cliente;
	}
	//MÉTODOS
	public void run() {
		DataOutputStream dout = null;
		DataInputStream din = null;
		
		String modo = "";
		try {
			dout = new DataOutputStream(cliente.getOutputStream()); 	//IOException
			din = new DataInputStream(cliente.getInputStream());	//IOException
				
			modo = din.readLine();
			if(modo.equalsIgnoreCase("TERMINAL")) {
				jugarTerminal(din, dout);	//IOException
			}
			if(modo.equalsIgnoreCase("INTERFAZ")) {
				jugarInterfaz(din,dout);	//IOException
			}
			
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(din!= null) {
					din.close();
				}
				if(dout != null) {
					dout.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void jugarInterfaz(DataInputStream din, DataOutputStream dout) throws IOException{
		String recibo = "",lineaBlanco = "\n";
		String filaS,columnaS, modoS;
		String [] casilla, modoArray;
		
		int fila, columna, modo;
		int tamano;
		boolean salir = false;
		
		//RECIBIR TAMAÑO DEL TABLERO
		recibo = din.readLine();
		tamano = Integer.parseInt(recibo);
		this.buscaminas = new BuscaminasJuego(tamano);
		
		//RECIBIR PRIMERA CASILLA
		recibo = din.readLine();


		modoArray = recibo.split(" ");
		casilla = modoArray[1].split("-");
		filaS = casilla[0]; fila = Integer.parseInt(filaS);
		columnaS = casilla[1]; columna = Integer.parseInt(columnaS);
		this.buscaminas.iniciarTablero(fila, columna);


		//MANDAR TABLERO
		dout.writeBytes((tamano+2) + lineaBlanco);
		dout.writeBytes(buscaminas.stringTablero());
		dout.flush();
		
		while(true) {
			//RECIBIR LINEA
			recibo = din.readLine();
			
			modoArray = recibo.split(" ");
			modoS = modoArray[0];
			modo = Integer.parseInt(modoS);
			if(modo==4) { //modo 4: nuevoJuego
				//NUEVO JUEGO				
				this.jugarInterfaz(din,dout);
				break;
			}else {
				casilla = modoArray[1].split("-");
				filaS = casilla[0]; fila = Integer.parseInt(filaS);
				columnaS = casilla[1]; columna = Integer.parseInt(columnaS);
				
				if(!buscaminas.opcion(modo, fila, columna)) { //Si es false entonces ha encontrado una bomba
					//SE HA PERDIDO
					dout.writeBytes((-1) + lineaBlanco);
					dout.writeBytes((tamano) + lineaBlanco);
					dout.writeBytes(buscaminas.mostrarTableroEntero());
					dout.flush();

					//LEER MODO JUEGO
					modoS = din.readLine();

					//NUEVO JUEGO
					this.jugarInterfaz(din,dout);
					break;
				}


				if(buscaminas.ganado())
				{
					System.out.println("Cliente: " + cliente.getLocalAddress() + "	 HA GANADO");
					//SE HA GANADO
					dout.writeBytes((-2) + lineaBlanco);
					dout.writeBytes((tamano) + lineaBlanco);
					dout.writeBytes(buscaminas.stringTablero());
					dout.flush();

					//LEER MODO JUEGO
					modoS = din.readLine();

					//NUEVO JUEGO
					this.jugarInterfaz(din,dout);
					break;
				}else{
					//MANDAR TABLERO
					dout.writeBytes((tamano+2) + lineaBlanco);
					dout.writeBytes(buscaminas.stringTablero());
					dout.flush();
				}
			}
			
		}
		
	}
	
	private void jugarTerminal(DataInputStream din, DataOutputStream dout) throws IOException {
		String recibo ="";
		String elegirOpcion = 	"Elegir opcion: "+ "\n"
								+"	1. Despejar casilla"+ "\n"
								+"	2. Posible bomba"+ "\n"
								+"	3. Quitar posible bomba" + "\n"
								+"	4. Resolver" + "\n";
		String pedirCasilla = "Introducir fila y columna: (separados por -, ejemplo 0-3)" + "\n";
		String volverJugar = "¿Volver a jugar? (Y/N)";
		String ganado = "¡Enhorabuena, has ganado!";
		String lineaBlanco = "\n";
		
		String filaS = "", columnaS = "";
		String [] casilla;
		
		int tamano, numero, fila, columna;
		//RECIBIR TAMAÑO DEL TABLERO
		recibo = din.readLine();
		tamano = Integer.parseInt(recibo);
		this.buscaminas = new BuscaminasJuego(tamano);
		
		//MANDAR PRIMERA CASILLA
		dout.writeBytes((tamano + 2 + 1) + "\n");
		dout.writeBytes(buscaminas.stringTablero());
		dout.writeBytes(pedirCasilla);
		dout.flush();
		
		//RECIBIR PRIMERA CASILLA
		recibo = din.readLine();
		casilla = recibo.split("-");
		filaS = casilla[0]; fila = Integer.parseInt(filaS);
		columnaS = casilla[1]; columna = Integer.parseInt(columnaS);
		this.buscaminas.iniciarTablero(fila, columna);
		
		while(!buscaminas.ganado()) {
			//MANDAR TABLERO + OPCIONES
			dout.writeBytes((tamano+2+5) + lineaBlanco);
			dout.writeBytes(buscaminas.stringTablero());
			dout.writeBytes((elegirOpcion));
			dout.flush();
			
			//RECIBIR OPCION 
			recibo = din.readLine();
			numero = Integer.parseInt(recibo);
			
			if(numero == 4) {
				dout.writeBytes((tamano + 1) + lineaBlanco);
				dout.writeBytes(buscaminas.mostrarTableroEntero());
				dout.writeBytes(volverJugar + lineaBlanco);
				dout.flush();
				
				//NUEVO JUEGO
				recibo = din.readLine();
				if(!recibo.equalsIgnoreCase("N")) {
					din.readLine();
					jugarTerminal(din,dout);
				}
				break;
			}
			
			//SOLICITAR CASILLA
			dout.writeBytes((pedirCasilla));
			dout.flush();
			
			//RECIBIR CASILLA
			recibo = din.readLine();
			casilla = recibo.split("-");
			filaS = casilla[0]; fila = Integer.parseInt(filaS);
			columnaS = casilla[1]; columna = Integer.parseInt(columnaS);
			
			if(!buscaminas.opcion(numero, fila, columna)) { //Si es false entonces ha encontrado una bomba
				dout.writeBytes((-1) + lineaBlanco);
				dout.writeBytes((tamano + 2) + lineaBlanco);
				dout.writeBytes("BOMBA, has perdido" + lineaBlanco);
				dout.writeBytes(buscaminas.mostrarTableroEntero());
				dout.writeBytes(volverJugar + lineaBlanco);
				dout.flush();
				
				//NUEVO JUEGO
				recibo = din.readLine();
				if(!recibo.equalsIgnoreCase("N")) {
					//LEE TERMINAL
					din.readLine();
					jugarTerminal(din,dout);
				}
				break;
			}
		}
		if(buscaminas.ganado()) {
			//GANADO
			dout.writeBytes((-2) + lineaBlanco);
			dout.writeBytes((2) + lineaBlanco);
			dout.writeBytes(ganado + lineaBlanco);
			dout.writeBytes(volverJugar + lineaBlanco);
			dout.flush();
			
			//NUEVO JUEGO
			recibo = din.readLine();
			if(!recibo.equalsIgnoreCase("N")) {
				//LEE TERMINAL
				din.readLine();
				jugarTerminal(din,dout);
			}
		}
			
	}

}
