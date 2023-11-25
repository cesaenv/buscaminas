package Buscaminas;

public class Tablero{
	
	private int [][] tablero;
	private int tamano;
	private int casillasColocadas;
	private int numBombas;
	
	public Tablero(int tamano){
		this.casillasColocadas = 0;
		this.numBombas = (tamano*tamano)/4;
		
		this.tamano = tamano;
		this.tablero = new int [tamano][tamano];
		iniciarTablero();
	}
	private void iniciarTablero() {
		for(int f=0; f<tamano ; f++) {
			for(int c=0 ; c<tamano ;c++) {
				tablero[f][c] = -2;
			}
		}
	}
	public void quitarBomba(int fila, int columna) {
		if(tablero[fila][columna] == 10) {
			colocarCasilla(fila, columna, -2);
			this.numBombas++;
			this.casillasColocadas--;
			this.casillasColocadas--; //AL COLOCAR CASILLA ANTES SE COLOCA 1 y SE IGUALAN SI SE RESTA UNA SOLA VEZ
		}
	}
	public void colocarCasilla(int fila, int columna, int num) {
		this.tablero[fila][columna] = num;
		if(num == 10) { //PASAN UNA POSIBLE BOMBA
			this.numBombas--;
		}
		this.casillasColocadas++;
	}
	public boolean casillaColocada(int fila, int columna) {
		if(tablero[fila][columna] > -2) {
			return true;
		}
		return false;
	}
	public int getNumero(int fila, int columna) {
		return tablero[fila][columna];
	}
	public boolean ganado() {
		return (casillasColocadas == tamano*tamano ||
				casillasColocadas + numBombas == tamano*tamano);
	}
	
	public String mostrarTablero() {
		String dev = "";
		for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
            	if(tablero[f][c] == -2) {
            		dev = dev + "- ";
            	}else if(tablero[f][c] == -1) {
            		dev = dev + "B ";
            	}else if(tablero[f][c] == 10) {
            		dev = dev + "P ";
            	}else {
            		dev = dev + tablero[f][c] + " ";
            	}
            }
            dev = dev + "\n"; // Nueva línea para cada fila
        }
		dev = dev + "Casillas colocadas: " + casillasColocadas + "\n";
		dev = dev + "Numero de bombas: " + numBombas + "\n";
		
		return dev;
	}

}
