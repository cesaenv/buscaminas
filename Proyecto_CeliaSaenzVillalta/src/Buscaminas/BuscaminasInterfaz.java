package Buscaminas;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BuscaminasInterfaz extends JFrame {
    private JButton[][] botones;
    private int num = 4;
    
    public BuscaminasInterfaz() {
        super("Buscaminas");
        
        // Inicializar tablero
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));
        botones = new JButton[num][num];

        // Inicializar los botones del tablero
        for (int f = 0; f < num; f++) {
            for (int c = 0; c < num; c++) {
                botones[f][c] = new JButton();
                botones[f][c].setMargin(new Insets(0, 0, 0, 0));
                
                add(botones[f][c]);
                botones[f][c].addActionListener(new ActionListener() {
                    int fila, columna;
                	
                    public void actionPerformed(ActionEvent e) {
                        //aquí operacion lógica que se haría al pulsar
                    	//seguramente tenga que hacer una clase a parte
                    	
                    	System.out.println("hola");
                    }
                });
            }
        }
        setVisible(true);
    }
    
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BuscaminasInterfaz();
            }
        });
    }
}
