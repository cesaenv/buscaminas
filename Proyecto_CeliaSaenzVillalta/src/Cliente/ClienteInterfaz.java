package Cliente;
import javax.swing.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClienteInterfaz extends JFrame {
	static ClienteInterfaz cliente;
//PANEL JUEGO
	private JPanel panelJuego;
	private JButton[][] botones;

//PANEL SUPERIORES
	private JPanel panelSuperior;
	private JButton nuevoJuegoButton;
    private JButton despejarCasillaButton;
    private JButton colocarBanderasButton;

	private Label labelNumBombas;
	private Label labelNumCasillasColocadas;
	private JLabel labelReloj;

//FUNCIONALIDAD SOCKET
	private static DataOutputStream dout;
    private static DataInputStream din;
    
    private boolean terminado;
    private Date inicio;
    private int numModo;
    private static int num;
    
	public static void main(String[] args) {
		Socket servidor = null;
		
		try {
			servidor= new Socket(InetAddress.getLocalHost().getHostAddress(), 6666);	//IOException
			din = new DataInputStream(servidor.getInputStream());	//IOException
			dout = new DataOutputStream(servidor.getOutputStream());	//IOException

			num = 0;
			if(seleccionarNivel()){
				cliente = new ClienteInterfaz();
				cliente.jugar();	//IOException
				
			}

		}catch(IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
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
	private void nuevoJuego(){
		setVisible(false);
		this.terminado = false;
		this.labelReloj.setText("00:00");
		
		if(seleccionarNivel()){
			if(num == 0) {
				mostrarResultados();
				nuevoJuego();
			}else {
				generarPanelJuego();
				try {
					setVisible(true);
	
					String lineaBlanco = "\n";
	
					//ENVIAR MODO
					dout.writeBytes(4 + lineaBlanco);
					dout.flush();
	
					//ENVIAR TAMAÑO TABLERO
					dout.writeBytes(num + lineaBlanco);
					dout.flush();
	
					this.inicio = new Date();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
    
    public ClienteInterfaz() {
    	this.numModo = 1;
		this.inicio = new Date();
		
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
				botones[i][j].setBackground(Color.lightGray);
				Font fuente = new Font("Calibri",20,40);
				botones[i][j].setFont(fuente);
				botones[i][j].setForeground(Color.BLACK);
				botones[i][j].setHorizontalAlignment(JButton.CENTER);
				botones[i][j].addActionListener(new BotonCasilla(i,j));

				panelJuego.add(botones[i][j]);
			}
		}

		// Agregar componentes a la ventana
		add(panelJuego, BorderLayout.CENTER);

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

		// Crear label adicionales
		labelNumBombas = new Label();
		labelNumCasillasColocadas = new Label();

		labelReloj = new JLabel();
		labelReloj.setHorizontalAlignment(JLabel.CENTER);
		labelReloj.setFont(labelReloj.getFont().deriveFont(24.0f));
		labelReloj.setText("00:00");

		// PANEL SUPERIOR
		panelSuperior = new JPanel();
		panelSuperior.add(labelReloj);

		panelSuperior.add(nuevoJuegoButton);
		panelSuperior.add(despejarCasillaButton);
		panelSuperior.add(colocarBanderasButton);

		panelSuperior.add(labelNumBombas);
		panelSuperior.add(labelNumCasillasColocadas);


		// Agregar panel de botones al norte (superior)
		add(panelSuperior, BorderLayout.NORTH);

        // Configurar la ventana
        pack();
        setLocationRelativeTo(null); // Centrar la ventana en la pantalla
        
     	// Configurar la ventana para que aparezca en pantalla completa
        setExtendedState(JFrame.MAXIMIZED_BOTH);

    }
	private void actualizarReloj() {
		if(!terminado) {
			Date act = new Date(new Date().getTime()-inicio.getTime());

			SimpleDateFormat formato = new SimpleDateFormat("mm:ss");
			String horaFormateada = formato.format(act);

			labelReloj.setText(horaFormateada);
		}
	}

	private void generarPanelJuego(){
		remove(panelJuego);

		// Crear el panel de juego
		panelJuego = new JPanel(new GridLayout(num, num));
		botones = new JButton[num][num];
		for (int f = 0; f < num; f++) {
			for (int c = 0; c < num; c++) {
				botones[f][c] = new JButton();
				botones[f][c].setBackground(Color.lightGray);
				Font fuente = new Font("Calibri",20,40);
				botones[f][c].setFont(fuente);
				botones[f][c].setForeground(Color.BLACK);
				botones[f][c].setAutoscrolls(true);
				botones[f][c].addActionListener(new BotonCasilla(f,c));

				panelJuego.add(botones[f][c]);
			}
		}

		// Agregar componentes a la ventana
		add(panelJuego, BorderLayout.CENTER);

		labelNumBombas.setText("");
		labelNumCasillasColocadas.setText("");
	}
    public void jugar() throws IOException, InterruptedException {
		setVisible(true);

    	String lineaBlanco = "\n";
    	
    	//ENVIAR MODO DE JUEGO
    	dout.writeBytes(("INTERFAZ" + lineaBlanco));
    	dout.flush();
    	
    	//ENVIAR TAMAÑO TABLERO
    	dout.writeBytes(num + lineaBlanco);
    	dout.flush();

    	while(!Thread.currentThread().isInterrupted()) {
			Thread.sleep(1000);
			actualizarReloj();
    	}

    }
    
    private void modificarCasillas() throws IOException {
    	long tiempoFinal = 0;
    	
    	String linea;
    	String [] lineaSpliteada;
    	
    	boolean perdido = false;
		boolean ganado = false;
    	
    	int numLineas;
    	
    	linea = din.readLine();
    	numLineas = Integer.parseInt(linea);
    	if(numLineas == -1) { //HAS PERDIDO
        	linea = din.readLine();
    		numLineas = Integer.parseInt(linea);
    		perdido = true;
    	}
		if(numLineas == -2) { //HAS GANADO
			tiempoFinal = new Date().getTime() - this.inicio.getTime();
			terminado = true;
			
			linea = din.readLine();
			numLineas = Integer.parseInt(linea);
			ganado = true;
		}
    	for(int f=0; f<num ; f++){
    		linea = din.readLine();
    		lineaSpliteada = linea.split(" ");
			for(int c=0; c<lineaSpliteada.length; c++) {
				if(lineaSpliteada[c].equals("P")){
					botones[f][c].setText("");
					botones[f][c].setBackground(Color.gray);
					//botones[f][c].setIcon(new ImageIcon("Imegenes\\flag.png"));
				}else if(lineaSpliteada[c].equals("B")) {
					botones[f][c].setText("");
					botones[f][c].setBackground(Color.red);
					//botones[f][c].setIcon(new ImageIcon("Imagenes\\bomba.png"));
				}else if(lineaSpliteada[c].equals("0")) {
					botones[f][c].setText("");
					botones[f][c].setIcon(null);
				}else {
					botones[f][c].setBackground(Color.lightGray);
					botones[f][c].setText(lineaSpliteada[c]);
					botones[f][c].setText(lineaSpliteada[c]);
					botones[f][c].setIcon(null);
				}
			}
    	}
		if(!ganado && !perdido){
			labelNumBombas.setText(din.readLine());
			labelNumCasillasColocadas.setText(din.readLine());

		}else{
			String mensaje = "HAS PERDIDO";
			if(ganado) {
				mensaje = "HAS GANADO";
			}
			JOptionPane.showMessageDialog(null, mensaje);

			if(ganado){
				din.readLine();
				din.readLine();
				
				String nivelSeleccionado = "";
				if (num == 8) {
					nivelSeleccionado = "n_Principiante";
				}
				if (num == 12) {
					nivelSeleccionado = "n_Intermedio";
				}
				if (num == 20) {
					nivelSeleccionado = "n_Experto";
				}
				
				Date act = new Date(tiempoFinal);
				SimpleDateFormat formato = new SimpleDateFormat("mm:ss.SSS");
				String horaFormateada = formato.format(act);
				
				actualizarResultados(nivelSeleccionado,horaFormateada);
			}
			nuevoJuego();
		}
    }
    
    private void mostrarCasillas() {
		String linea = "";
    	for (int f = 0; f < num; f++) {
            for (int c = 0; c < num; c++) {
                botones[f][c].setText("");
				botones[f][c].setBackground(Color.lightGray);
				linea = linea + c + " ";
            }
			System.out.println(linea);
			linea = "";
        }
    }

	private static boolean seleccionarNivel() {
		int seleccion = -1;
		String seleccionado = "";
		if(num != 0) {
			String[] opciones = {"Nivel Principiante", "Nivel Intermedio", "Nivel Avanzado", "Mostrar Resultados"};
			seleccion = JOptionPane.showOptionDialog(
					null,
					"Seleccionar Opcion",
					"Seleccionar",
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					opciones,
					opciones[3]
			);
			
			if(seleccion >= 0) {
				seleccionado = opciones[seleccion];
			}
		}else {
			String[] opciones = {"Nivel Principiante", "Nivel Intermedio", "Nivel Avanzado"};
			seleccion = JOptionPane.showOptionDialog(
					null,
					"Seleccionar Nivel",
					"Seleccionar",
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					opciones,
					opciones[1]
			);
			if(seleccion >= 0) {
				seleccionado = opciones[seleccion];
			}
		}

		if (seleccion >= 0) {
			if (seleccionado.equalsIgnoreCase("Nivel Principiante")) {
				num = 8;
			}
			if (seleccionado.equalsIgnoreCase("Nivel Intermedio")) {
				num = 12;
			}
			if (seleccionado.equalsIgnoreCase("Nivel Avanzado")) {
				num = 20;
			}
			if (seleccionado.equalsIgnoreCase("Mostrar Resultados")) {
				num = 0;
			}
		} else {
			JOptionPane.showMessageDialog(null, "HASTA PRONTO!");
			Thread.currentThread().interrupt();
		}
		return (seleccion >= 0);
	}
	
	
	private void actualizarResultados(String nombreNivel, String result) {
		try {
			File file = new File("Proyecto_CeliaSaenzVillalta\\src\\Cliente\\resultados.xml");
			System.out.println(file.getAbsolutePath());
			if(!file.exists()) {
				file.createNewFile();
				FileOutputStream fout = new FileOutputStream(file);
				
				String escribir = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
						+ "<!DOCTYPE persona SYSTEM \"./resultados.dtd\">\r\n"
						+ "<persona name=\"\">\r\n"
						+ "	<n_Principiante/>\r\n"
						+ "	<n_Intermedio/>\r\n"
						+ "	<n_Experto/>\r\n"
						+ "</persona>";
				fout.write(escribir.getBytes());
				fout.flush();
				fout.close();
			}
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document documento = db.parse(file);
			
			//NODO RAÍZ PERSONA
			Element persona = documento.getDocumentElement();
			if(persona.getAttribute("name").isEmpty()) {
				String name = JOptionPane.showInputDialog("Introducir tu nombre");
				persona.setAttribute("name", name);
			}
			
			//NODOS NIVELES
			NodeList niveles = persona.getChildNodes();
			
			String texto;
			Element nivel = null;
			NodeList resultados = null;
			Element resultado = null;
			for(int i=0, tam=niveles.getLength(); i<tam ;i++) {
				if(	niveles.item(i).getNodeType() == Node.ELEMENT_NODE) {
					//NIVEL 
					if(niveles.item(i).getNodeName().equalsIgnoreCase(nombreNivel)) {
						resultados = niveles.item(i).getChildNodes();
						nivel = (Element) niveles.item(i);
						
						resultado = documento.createElement("resultado");
						resultado.setTextContent(result);
						nivel.appendChild(resultado);
						break;
					}
				}
			}
			
			documento.insertBefore(documento.createProcessingInstruction("DOCTYPE", "persona SYSTEM 'src\\Cliente\\resultados.dtd'"), documento.getDocumentElement());
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(documento);
			StreamResult resul = new StreamResult("Proyecto_CeliaSaenzVillalta\\src\\Cliente\\resultados.xml");
			transformer.transform(source, resul);
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private long stringToLong(String s) {
		return Long.parseLong(s);
	}
	private String longToString(long l) {
		return l + "";
	}
	
	private void mostrarResultados() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document documento = db.parse(new File("Proyecto_CeliaSaenzVillalta\\src\\Cliente\\resultados.xml"));
			
			//NODO RAÍZ PERSONA
			Element persona = documento.getDocumentElement();
			System.out.println("Cliente: " + persona.getAttribute("name"));
			
			//NODOS NIVELES
			NodeList niveles = persona.getChildNodes();
			
			Element nivel = null;
			NodeList resultados = null;
			Element resultado = null;
			for(int i=0, tam=niveles.getLength(); i<tam ;i++) {
				if(	niveles.item(i).getNodeType() == Node.ELEMENT_NODE) {
					//NIVEL PRINCIPIANTE
					if(niveles.item(i).getNodeName().equalsIgnoreCase("n_Principiante")) {
						System.out.println("Nivel Principiante: ");
						resultados = niveles.item(i).getChildNodes();
						for(int r=0, tamR=resultados.getLength(); r<tamR ;r++) {
							if(	resultados.item(r).getNodeType() == Node.ELEMENT_NODE &&
								resultados.item(r).getNodeName().equalsIgnoreCase("resultado")) {
								System.out.println("	" + r + ": " + resultados.item(r).getTextContent() + "s");
							}
						}
						System.out.println();
					}
					//NIVEL INTERMEDIO
					if(niveles.item(i).getNodeName().equalsIgnoreCase("n_Intermedio")) {
						System.out.println("Nivel Intermedio: ");
						resultados = niveles.item(i).getChildNodes();
						for(int r=0, tamR=resultados.getLength(); r<tamR ;r++) {
							if(	resultados.item(r).getNodeType() == Node.ELEMENT_NODE &&
								resultados.item(r).getNodeName().equalsIgnoreCase("resultado")) {
								System.out.println("	" + r + ": " + resultados.item(r).getTextContent() + "s");
							}
						}
						System.out.println();
					}
					//NIVEL EXPERTO
					if(niveles.item(i).getNodeName().equalsIgnoreCase("n_Experto")) {
						System.out.println("Nivel Experto: ");
						resultados = niveles.item(i).getChildNodes();
						for(int r=0, tamR=resultados.getLength(); r<tamR ;r++) {
							if(	resultados.item(r).getNodeType() == Node.ELEMENT_NODE &&
								resultados.item(r).getNodeName().equalsIgnoreCase("resultado")) {
								System.out.println("	" + r + ": " + resultados.item(r).getTextContent() + "s");
							}
						}
						System.out.println();
					}
				}
			}
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

				colocarBanderasButton.setBackground(Color.white);
				despejarCasillaButton.setBackground(Color.white);

				nuevoJuego();

				numModo = modo;
				despejarCasillaButton.setBackground(Color.magenta);

				nuevoJuegoButton.setBackground(Color.white);
				colocarBanderasButton.setBackground(Color.white);
    		}
    	}
    }
}
