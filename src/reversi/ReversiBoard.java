package reversi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class ReversiBoard extends JPanel {

    private final int ROWS = 8, COLS = 8;

    int [] data = new int[64];

    // GUI elements
    JLabel darkScoreLbl, lightScoreLbl, currentPlayerLbl;
    JButton btnPass;
    JPanel boardPanel;

    // logical board
    Move[][] board = new Move[ROWS][COLS];

    // current moving piece
    private Move move_piece;

    boolean validMovesAvailable;

    int darkScore = 2, lightScore = 2, counter = 0;

    public ReversiBoard() {
        super(new BorderLayout());
        setBorder(new TitledBorder("Display Area"));
        setOpaque(true);    // content pane must be opaque

        JPanel topPanel = new JPanel(new FlowLayout());

        JButton btnNewGame = new JButton("Start New Game");
        btnNewGame.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                newGame();
            }
        });

        currentPlayerLbl = new JLabel("Current Player : DARK", JLabel.RIGHT);
        currentPlayerLbl.setFont(new Font("Lucida Calligraphy", Font.BOLD, 18));

        topPanel.add(btnNewGame);
        topPanel.add(currentPlayerLbl);

        add(topPanel, BorderLayout.NORTH);
        // Board Panel
        boardPanel = new JPanel(new GridLayout(8,8));
        for(int row = 0; row < 8; row++) {
            for(int col=0; col < 8; col++) {
                JPanel cell = new JPanel(new BorderLayout());
                cell.setSize(70, 70);
                cell.setBackground(new Color(188, 222, 255));
                cell.setBorder(BorderFactory.createLineBorder(Color.gray));
                boardPanel.add(cell);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        // bottom score panel
        JPanel scorePanel = new JPanel(new FlowLayout());
        
        // Creating score Labels
        darkScoreLbl = new JLabel("Dark : " + darkScore);
        lightScoreLbl = new JLabel("Light: " + lightScore);
        
        // Pass Button
        btnPass = new JButton("Pass Turn");
        btnPass.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                swapTurns(move_piece);
            }    
        });
        
        scorePanel.add(darkScoreLbl);
        scorePanel.add(btnPass);
        scorePanel.add(lightScoreLbl);

        add(scorePanel, BorderLayout.SOUTH);

        newGame();
    }

    public void newGame() {
        for(int row = 0; row < ROWS; row++) { for(int col = 0; col < COLS; col++) { board[row][col] = Move.EMPTY;}}
        move_piece = Move.DARK; // starting from a black player        
        placePiece(3,3, Move.LIGHT); updateGUI();
        placePiece(3,4, Move.DARK); updateGUI();
        placePiece(4,4, Move.LIGHT); updateGUI();
        placePiece(4,3, Move.DARK); updateGUI();
           
        boardPanel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                int row = e.getX() / 74;
                int col = e.getY() / 74;
                if(doFlip(row, col, move_piece, false)) {
                    doFlip(row, col, move_piece, true);
                    placePiece(row, col, move_piece);
                    updateGUI();
                    swapTurns(move_piece);
                }
            }
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });
    }

    public void placePiece(int row, int col, Move color) { // placing piece in the logical board
        if(board[row][col] == Move.EMPTY)
            board[row][col] = color;
    }

    public void updateGUI() {
        darkScore = 0; lightScore = 0;
        for(int row = 0; row < 8; row ++) { for(int col = 0; col < 8; col++) { JPanel panel = (JPanel)boardPanel.getComponent(coordToindex(row, col)); panel.removeAll();}}
        for(int row = 0; row < 8; row ++) {
            for(int col = 0; col < 8; col++) {
                if(doFlip(row, col, move_piece, false))
                    addRespectivePics(row, col, "transparent");
                
                if(board[row][col] == Move.DARK) {
                    addRespectivePics(row, col, Move.DARK.toString().toLowerCase());
                    darkScore++;
                    darkScoreLbl.setText("Dark : " + darkScore);
                }
                 if(board[row][col] == Move.LIGHT) {
                    addRespectivePics(row, col, Move.LIGHT.toString().toLowerCase());
                    lightScore++;
                    lightScoreLbl.setText("Light : " + lightScore);
                }          
                checkWin(darkScore, lightScore);
            }
        }
    }

    public boolean doFlip(int row, int col, Move piece, boolean putDown) {
        boolean isValid = false;
        for(int dX = -1; dX < 2; dX++) {
            for(int dY = -1; dY < 2; dY ++) {
                if(dX == 0 && dY == 0) { continue; } // if it is checking itself
             // check the surrounding pieces
             int checkRow = row + dX;
             int checkCol = col + dY;
             // our board is a 8x8 board ... cells like (0,0) don't have all surrounding pieces.
                if(checkRow >= 0 && checkCol >= 0 && checkRow < 8 && checkCol < 8) {
                    if(board[checkRow][checkCol] == (piece == Move.DARK ? Move.LIGHT : Move.DARK)) { // if your piece is white, check for black and vice versa
                        for(int distance = 0; distance < 8; distance++) {   // keep track of the distance
                            int minorCheckRow = row+distance*dX;
                            int minorCheckCol = col+distance*dY;
                            if(minorCheckRow < 0 || minorCheckCol < 0  || minorCheckRow > 7 || minorCheckCol > 7) continue; // if going furhter than the borad length, ignore
                            if(board[minorCheckRow][minorCheckCol] == piece) {
                                if(putDown) {
                                    for(int distance2 = 1; distance2 < distance; distance2 ++) {
                                        int flipRow = row+distance2*dX;
                                        int flipCol = col+distance2*dY;
                                        board[flipRow][flipCol] = piece;
                                    }
                                }
                                isValid = true; break;
                            }
                        }
                    }
                }
             }     
          }
      return isValid;
    }

    public void addRespectivePics(int row, int col, String colorName) {
        ImageIcon picture = createImageIcon("images/" + colorName + ".png");
        JLabel picLbl = new JLabel(picture);
        JPanel panel = (JPanel)boardPanel.getComponent(coordToindex(row, col));
        panel.removeAll();
        panel.add(picLbl);
        boardPanel.updateUI();
    }

    private void swapTurns(Move piece) {    // swap turns
        move_piece = (move_piece == Move.DARK ? Move.LIGHT : Move.DARK);
        updateGUI();
        currentPlayerLbl.setText("Current Player :" + move_piece.toString());
    }

    private int coordToindex(int row, int col) {     // convert 2D array to 1D array index
        return (col * 8) + row;
    }

    private void checkWin(int totalDark, int totalLight) {     // counter the scores and return the winner
        if(totalDark + totalLight == 64 && totalDark > totalLight)
            GUIConsole.display("Black Player Wins!");
        if(totalDark + totalLight == 64 && totalLight > totalDark)
            GUIConsole.display("White Player Wins!");
        if(totalDark + totalLight == 64 && totalLight == totalDark)
            GUIConsole.display("It's a Tie!");
    }

    protected ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if(imgURL != null) return new ImageIcon(imgURL);
        else {
            System.err.println("Couldn't find the file: " + path);
            return null;
        }
    }
}
