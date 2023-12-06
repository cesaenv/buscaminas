package Buscaminas;

import java.util.Random;
import java.util.Scanner;

public class BuscaminasJuego {
	//ATRIBUTOS
	private int [][] tablero;
	private int numBombas;
	private int numBombasCorrectas;
	private int tamano;
	private Tablero tableroCliente;
	
	//CONSTRUCTORES
	public BuscaminasJuego(int tamano){
		this.tamano = tamano;
		this.numBombas = (tamano*tamano)/6;
		this.tablero = new int [tamano][tamano];
		this.tableroCliente = new Tablero(tamano,numBombas);
		this.numBombasCorrectas = 0;
	}
	
	//MÉTODOS
	public void iniciarTablero(int fila, int columna) { //la casilla inicial se trata de la pasada por parámetro
		//Liberar casilla inicial
		tablero[fila][columna] = 0;
		
		//Colocar bombas
		this.colocarBombas(fila, columna); //bombas se representan con -1
		
		//Colocar tableros
		this.colocarTablero();
		this.colocarTableroCliente(fila, columna, 0);
		
	}
//	public void jugar() {
//		Scanner sc = new Scanner(System.in);
//		String filaColumna, filaS, columnaS;
//		String [] filaColumnaSpliteado;
//		
//		int fila, columna, numero = 0;
//		
//		while(!ganado() && numero != 3) {
//			System.out.println(tableroCliente.mostrarTablero());
//			
//			System.out.println("Elegir opcion: ");
//			System.out.println("	1. Despejar casilla");
//			System.out.println("	2. Posible bomba");
//			System.out.println("	3. Resolver");
//
//			numero = Integer.parseInt(sc.nextLine());
//			
//			if(numero != 3) {
//				System.out.println("Introducir fila y columna: (separados por -, ejemplo 0-3)");
//				filaColumna = sc.nextLine();
//				filaColumnaSpliteado = filaColumna.split("-");
//				filaS = filaColumnaSpliteado[0];
//				columnaS = filaColumnaSpliteado[1];
//				
//				fila = Integer.parseInt(filaS);
//				columna = Integer.parseInt(columnaS);
//				
//				if(!opcion(numero,fila,columna)) {
//					break;
//				}else {
//					break;
//				}
//			}
//		}
//		if(ganado()) {
//			System.out.println("¡Enhorabuena!, has ganado");
//		}if(numero == 3) {
//			System.out.println(mostrarTableroEntero());
//		}else {
//			System.out.println(mostrarTableroEntero());
//			System.out.println("BOMBA, has perdido");
//		}
//			
//	}
	public boolean opcion(int opcion, int fila, int columna) {
		if(opcion == 1) {
			if(!esBomba(fila,columna)) {
				if(this.colocarCasillaCliente(fila, columna)) { 
					return false; //ha encontrado una bomba en una de las casillas adyacentes
				}
				if(tablero[fila][columna] == 0) {
					this.colocarTableroCliente(fila, columna, 0);
				}
			}else{
				return false;
			}
		}
		if(opcion == 2) {
			if(tableroCliente.casillaColocada(fila,columna)) {
				if(!tableroCliente.quitarFlag(fila,columna)) {
					opcion(1,fila,columna);
				}
			}else {
				colocarCasillaPosibleBomba(fila,columna);
				if(esBomba(fila,columna)){
					numBombasCorrectas++;
				}
			}
		}
		if(opcion == 3) {
			descolocarCasillaPosibleBomba(fila,columna);
		}
		return true;
	}
	public boolean ganado() {
		return tableroCliente.ganado() && numBombasCorrectas == numBombas;
	}
	public boolean esBomba(int fila, int columna) {
		return (tablero[fila][columna] == -1);
	}
	public String stringTablero() {
		return tableroCliente.mostrarTablero();
	}
	
	private boolean colocarCasillaCliente(int fila, int columna) {
		if(tableroCliente.casillaColocada(fila, columna)) {
			return colocarTableroCliente(fila,columna,tablero[fila][columna]);
		}else {
			tableroCliente.colocarCasilla(fila, columna, tablero[fila][columna]);
		}
		return false;
	}
	private void colocarCasillaPosibleBomba(int fila, int columna) {
		if(!tableroCliente.casillaColocada(fila, columna)) {
			tableroCliente.colocarCasilla(fila, columna, 10);
		}
	}
	private boolean descolocarCasillaPosibleBomba(int fila, int columna) {
		if(		tableroCliente.casillaColocada(fila, columna)) {
			if(!tableroCliente.quitarFlag(fila,columna)) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	private void colocarBombas(int fila, int columna) {
		Random rand = new Random();
		int bombasColocadas = 0;
		while (bombasColocadas<numBombas) {
            int filaB = rand.nextInt(tablero.length);
            int columnaB = rand.nextInt(tablero[0].length);

            // Verificar si ya hay una bomba en esa posición o es casilla inicial
            if (	tablero[filaB][columnaB] != -1 &&
            		tablero[filaB][columnaB] != 10 &&
            		!this.casillasAdyacentes(fila, columna, filaB, columnaB) ) {
            	tablero[filaB][columnaB] = -1;
            	bombasColocadas++;
            }
        }
	}
	private void colocarTablero() {
		for(int f=0; f<tamano ; f++) {
			for(int c=0; c<tamano ; c++) {
				if(		tablero[f][c] != -1 &&
						tablero[f][c] != 10	){
					tablero[f][c] = this.contarBombasAlrededor(f, c);
				}
			}
		}
	}
	private boolean colocarTableroCliente(int fila, int columna, int numero) {
		//PRE:  fila y columna debe de demarcar una casilla existente en el tablero, numero será el numero que designe la casilla
		//POST:	devuelve true si ha encontrado alguna bomba y false en caso contrario. 
		//		almacena todas las Casillas adyacentes de la casilla pasada por parámetro (fila,columna) en tableroCliente
		
		tableroCliente.colocarCasilla(fila, columna, tablero[fila][columna]);
		
		for (int f = -1; f <= 1; f++) {
            for (int c = -1; c <= 1; c++) {
            	// Ignorar la propia casilla
                if (f == 0 && c == 0) {
                	continue;
                }
            	
            	int nuevaFila = fila + f;
                int nuevaColumna = columna + c;
                
                // Verificar si la casilla vecina está dentro de los límites del tablero
                if (nuevaFila >= 0 && nuevaFila < tablero.length &&
                    nuevaColumna >= 0 && nuevaColumna < tablero[0].length) {
                	if(!tableroCliente.casillaColocada(nuevaFila, nuevaColumna)) {
                		if(tablero[nuevaFila][nuevaColumna] == 0) {
                			colocarTableroCliente(nuevaFila,nuevaColumna,0);
	                	}else if(tablero[nuevaFila][nuevaColumna] == -1){
	                		return true;
	                	}else {
	                		tableroCliente.colocarCasilla(nuevaFila, nuevaColumna, tablero[nuevaFila][nuevaColumna]);
	                	}
                	}
                }
            }
        }
		return false;
	}
	private boolean casillasAdyacentes(int fila, int columna, int fila2, int columna2) {
		int difFila = fila-fila2;
		int difColumna = columna-columna2;
		if(-1 <= difFila && difFila <= 1) {
			if(-1 <= difColumna && difColumna <= 1) {
				return true;
			}
		}
		return false;
	}
	private int contarBombasAlrededor(int fila, int columna) {
        int contadorBombas = 0;

        // Verificar las posiciones alrededor de la casilla actual
        for (int f = -1; f <= 1; f++) {
            for (int c = -1; c <= 1; c++) {
                // Ignorar la propia casilla
                if (f == 0 && c == 0) {
                	continue;
                }
                	
                // Calcular las coordenadas de la casilla vecina
                int nuevaFila = fila + f;
                int nuevaColumna = columna + c;

                // Verificar si la casilla vecina está dentro de los límites del tablero
                if (nuevaFila >= 0 && nuevaFila < tablero.length &&
                    nuevaColumna >= 0 && nuevaColumna < tablero[0].length) {
                    // Incrementar el contador si hay una bomba en la casilla vecina
                    if (tablero[nuevaFila][nuevaColumna] == -1) {
                        contadorBombas++;
                    }
                }
            }
        }
        return contadorBombas;
    }

	public String mostrarTableroEntero() {
		String dev = "";
        for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
                if (tablero[f][c] == -1) {
                    // Casilla con bomba
                    dev = dev + "B ";
                } else {
                	dev = dev + tablero[f][c] + " ";
                } 
            }
            dev = dev + "\n"; // Nueva línea para cada fila
        }
        return dev;
    }
}
