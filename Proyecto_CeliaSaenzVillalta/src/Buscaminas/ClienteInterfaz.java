package Buscaminas;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClienteInterfaz extends JFrame {
//PANEL JUEGO	
	private JPanel panelJuego;
	private JButton[][] botones;
//PANEL BOTONES_SUPERIORES	
	private JPanel panelBotonesSuperior;
	private JButton nuevoJuegoButton;
    private JButton despejarCasillaButton;
    private JButton colocarBanderasButton;
	
    private static DataOutputStream dout;
    private static DataInputStream din;
    
    
    private int numModo;
    private boolean nuevoJuego;
    private int num = 10;
	
//    public static void main(String[] args) {
//    	ClienteInterfaz nuevoCliente = new ClienteInterfaz(null);
//    	
//    }
    
	public static void main(String[] args) {
		Socket servidor = null;
		
		try {
			servidor= new Socket(InetAddress.getLocalHost().getHostAddress(), 6666);	//IOException
			din = new DataInputStream(servidor.getInputStream());	//IOException
			dout = new DataOutputStream(servidor.getOutputStream());	//IOException
			
			ClienteInterfaz cliente = new ClienteInterfaz();
			cliente.jugar();	//IOException
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
    public ClienteInterfaz() {
    	this.numModo = 1;
    	
    	// Configurar la ventana principal
        setTitle("Buscaminas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
     
        
        // Crear el panel de juego
        panelJuego = new JPanel(new GridLayout(num, num));
        botones = new JButton[num][num];
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                botones[i][j] = new JButton();
                botones[i][j].setBackground(Color.pink);
                Font fuente = new Font("Calibri",20,50);
                botones[i][j].setFont(fuente);
                botones[i][j].setForeground(Color.BLACK);
                botones[i][j].addActionListener(new BotonCasilla(i,j));
                
                panelJuego.add(botones[i][j]);
            }
        }

        // Crear botones adicionales
        nuevoJuegoButton = new JButton("Nuevo Juego");
        nuevoJuegoButton.addActionListener(new BotonModo(4));
        nuevoJuegoButton.setBackground(Color.white);

        despejarCasillaButton = new JButton("DespejarCasilla");
        despejarCasillaButton.addActionListener(new BotonModo(1));
        despejarCasillaButton.setBackground(Color.magenta);

        colocarBanderasButton = new JButton("Colocar Banderas");
        colocarBanderasButton.addActionListener(new BotonModo(2));
        colocarBanderasButton.setBackground(Color.white);
        
        panelBotonesSuperior = new JPanel();
        panelBotonesSuperior.add(nuevoJuegoButton);
        panelBotonesSuperior.add(despejarCasillaButton);
        panelBotonesSuperior.add(colocarBanderasButton);

        // Agregar panel de botones al norte (superior)
        add(panelBotonesSuperior, BorderLayout.NORTH);
        
        // Agregar componentes a la ventana
        add(panelJuego, BorderLayout.CENTER);
       

        // Configurar la ventana
        pack();
        setLocationRelativeTo(null); // Centrar la ventana en la pantalla
        
     // Configurar la ventana para que aparezca en pantalla completa
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        
        setVisible(true);
    }
    public void jugar() throws IOException{
    	String lineaBlanco = "\n";
    	
    	setVisible(true);
    	setVisible(true);
    	
    	//ENVIAR QUE SE JUEGA EN INTERFAZ
    	dout.writeBytes(("INTERFAZ" + lineaBlanco));
    	dout.flush();
    	
    	//ENVIAR TAMAÑO TABLERO
    	dout.writeBytes(num + lineaBlanco);
    	dout.flush();
    	
    	nuevoJuego = false;
    	while(!nuevoJuego) {
    		
    	}
    	System.out.println("salidoBucle");
    }
    
    private void modificarCasillas() throws IOException {
    	String linea;
    	String [] lineaSpliteada;
    	
    	boolean perdido = false;
    	
    	int numLineas;
    	
    	linea = din.readLine();
    	numLineas = Integer.parseInt(linea);
    	System.out.println(numLineas);
    	if(numLineas == -1) { //HAS PERDIDO
        	linea = din.readLine();
    		numLineas = Integer.parseInt(linea);
    		perdido = true;
    	}
    	System.out.println(numLineas);
    	for(int f=0; f<num ; f++){
    		linea = din.readLine();
    		System.out.println(linea);
    		lineaSpliteada = linea.split(" ");
			for(int c=0; c<lineaSpliteada.length; c++) {
				if(lineaSpliteada[c].equals("P")){
					botones[f][c].setText("");
					botones[f][c].setIcon(new ImageIcon("flag.png"));
				}else if(lineaSpliteada[c].equals("B")) {
					botones[f][c].setText("");
					botones[f][c].setIcon(new ImageIcon("bomba.png"));
				}else if(lineaSpliteada[c].equals("0")) {
					botones[f][c].setText("");
					botones[f][c].setIcon(null);
				}else {
					botones[f][c].setText(lineaSpliteada[c]);
					botones[f][c].setIcon(null);
				}
			}
    	}
    	if(perdido) {
    		System.out.println("Se ha perdido");
    		//ENVIAR TAMAÑO TABLERO
			dout.writeBytes(num + "\n");
			dout.flush();
    	}else {
    		System.out.println(din.readLine());
        	System.out.println(din.readLine());
    	}
    }
    
    private void despejarCasillas() {
    	for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                botones[i][j].setText("");
                botones[i][j].setIcon(null);
            }
        }
    }
    
    
    
    class BotonCasilla implements ActionListener{
    	private int fila, columna;
    	
    	public BotonCasilla(int fila, int columna) {
    		this.fila = fila;
    		this.columna = columna;
    	}
    	
    	public void actionPerformed(ActionEvent e) {
    		try {
				dout.writeBytes(numModo + " " + fila + "-" + columna + "\n");
				modificarCasillas();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
    	}
    }
    
    class BotonModo implements ActionListener{
    	private int modo;
    	
    	public BotonModo(int modo) {
    		this.modo = modo;
    	}
    	
    	public void actionPerformed(ActionEvent e) {
    		numModo = modo;
    		
    		if(modo == 1) {			//DESPEJAR CASILLAS
    			despejarCasillaButton.setBackground(Color.magenta);
    			
    			nuevoJuegoButton.setBackground(Color.white);
    			colocarBanderasButton.setBackground(Color.white);
    		}
    		else if(modo == 2) {	//COLOCAR BANDERAS
    			colocarBanderasButton.setBackground(Color.magenta);
    			
    			nuevoJuegoButton.setBackground(Color.white);
    			despejarCasillaButton.setBackground(Color.white);
    		}
    		else if(modo == 4) {	//NUEVO JUEGO
    			nuevoJuegoButton.setBackground(Color.magenta);
    			
    			try {
    				System.out.println("SE HA PULSADO NUEVO JUEGO-CLIENTE");
					dout.writeBytes(modo + " 0-0 " + "\n");
	    			dout.flush();
	    			
	    			despejarCasillas();
	    			
	    			//ENVIAR TAMAÑO TABLERO
	    			dout.writeBytes(num + "\n");
	    			dout.flush();
				} catch (IOException e1) {
					
				}
    			
    			colocarBanderasButton.setBackground(Color.white);
    			despejarCasillaButton.setBackground(Color.white);
    		}
    	}
    }
}
