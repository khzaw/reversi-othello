package reversi;

import java.awt.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ReversiGUI extends JFrame {

    private static JButton btnStart, btnPass;
    private static JLabel darkScore, lightScore;

    private static JFrame game;

    private static JPanel pnlLeft;
    public ReversiGUI() {
        super("Othello");
        setLayout(new BorderLayout());

        pnlLeft = new ReversiBoard();
        add(pnlLeft, BorderLayout.CENTER);
        
        setBounds(200, 50, 580, 700);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new ReversiGUI();
    }
}

    
