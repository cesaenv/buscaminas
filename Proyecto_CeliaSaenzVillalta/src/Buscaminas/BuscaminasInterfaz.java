package Buscaminas;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BuscaminasInterfaz extends JFrame {
    private JButton[][] botones;
    private int num = 30;
    
    public BuscaminasInterfaz() {
        super("Buscaminas");
        
        // Inicializar tablero
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(10, 10));
        botones = new JButton[num][num];

        // Inicializar los botones del tablero
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                botones[i][j] = new JButton();
                botones[i][j].setMargin(new Insets(0, 0, 0, 0));
                add(botones[i][j]);
                botones[i][j].addActionListener(new ActionListener() {
                    @Override
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
