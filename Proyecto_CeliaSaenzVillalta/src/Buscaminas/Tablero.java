package Buscaminas;

public class Tablero{
	
	private int [][] tablero;
	private int tamano;
	private int casillasColocadas;
	private int numBombas;
	
	public Tablero(int tamano, int numBombas)
	//PRE: _
	//POST: inicia un tablero de tamaño tamano y con un numero de bombas NUMBOMBAS
	{
		this.casillasColocadas = 0;
		this.numBombas = numBombas;
		
		this.tamano = tamano;
		this.tablero = new int [tamano][tamano];
		iniciarTablero();
	}
	private void iniciarTablero()
	//PRE: _
	//POST: inicia el tablero con -2 como predeterminado
	{
		for(int f=0; f<tamano ; f++) {
			for(int c=0 ; c<tamano ;c++) {
				tablero[f][c] = -2;
			}
		}
	}
	public boolean quitarFlag(int fila, int columna) 
	//PRE: 	fila y columna deben de corresponderse a una casilla existente en el tablero
	//POST: devuelve: 	true si ha podido quitar la FLAG almacenada en la casilla pasada por parámetros,
	//					false en caso contrario, es decir que no haya una FLAG almacenada en la casilla pasada por parámetros
	{
		if(tablero[fila][columna] == 10) {
			colocarCasilla(fila, columna, -2);
			this.numBombas++;
			this.casillasColocadas--;
			this.casillasColocadas--; //AL COLOCAR CASILLA ANTES SE COLOCA 1 y SE IGUALAN SI SE RESTA UNA SOLA VEZ
			return true;
		}
		return false;
	}
	public void colocarCasilla(int fila, int columna, int num) 
	//PRE: 	fila y columna deben de corresponderse a una casilla existente en el tablero y 
	//		num debe de ser un entero entre 	{10: FLAG-posible bomba (P),
	//											 0: no hay ninguna bomba asociada a esa casilla
	//											 x: hay una suma de x bombas en las casillas vecinas, siendo x entre 1 y 8
	//											 -1: BOMBA, hay una bomba (P)}
	//POST: se coloca la casilla en el tablero con el numero num
	{
		if(!casillaColocada(fila,columna)) {
			this.tablero[fila][columna] = num;
			if(num == 10) { //PASAN UNA POSIBLE BOMBA
				this.numBombas--;
			}
			this.casillasColocadas++;
		}
	}
	public boolean casillaColocada(int fila, int columna) 
	//PRE: 	fila y columna deben de corresponderse a una casilla existente en el tablero y 
	//POST: devuelve: 	true si la casilla pasada por parámetros está inicializada, es decir que contenga un número
	//					false en caso contrario 
	{
		if(getNumero(fila,columna)> -2) {
			return true;
		}
		return false;
	}
	public int getNumero(int fila, int columna) 
	//PRE: 	fila y columna deben de corresponderse a una casilla existente en el tablero y 
	//POST: devuelve el numero almacenado por la casilla
	{
		return tablero[fila][columna];
	}
	public boolean ganado() 
	//PRE: _
	//POST: devuelve: 	true si ya se ha conseguido completar todo el tablero
	//					false en caso contrario 
	{
		return (casillasColocadas == tamano*tamano);
	}
	
	public String mostrarTablero() 
	//PRE: _
	//POST: devuelve la String asociada al Tablero: 
	//			el tablero en sí + 
	//			el número de casillas colocadas + 
	//			el número de bombas
	{
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
