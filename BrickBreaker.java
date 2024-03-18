import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class MapGenerator {

    public int map[][];
    public int brickWidth;
    public int brickHeight;

    public MapGenerator(int row, int col) {
        map = new int[row][col];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = 1;
            }
        }

        brickWidth = 540 / col;
        brickHeight = 150 / row;
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    // Change brick color here
                    g.setColor(new Color(0XFF8787)); // Brick color
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);

                    g.setStroke(new BasicStroke(4));
                    g.setColor(Color.BLACK);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
        }
    }

    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }
}

class GamePlay extends JPanel implements KeyListener, ActionListener {
    private boolean play = true;
    private int score = 0;

    private int totalBricks = 21;

    private Timer timer;
    private int delay;

    private int playerX = 310;

    private int ballposX = 120;
    private int ballposY = 350;
    private int ballXdir = -1;
    private int ballYdir = -2;

    private MapGenerator map;

    public GamePlay(int row, int col, int difficulty) {
        map = new MapGenerator(row, col);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        // Set the delay based on difficulty
        switch (difficulty) {
            case 1: // Easy
                delay = 8;
                break;
            case 2: // Medium
                delay = 4;
                break;
            case 3: // Hard
                delay = 2;
                break;
            default:
                delay = 8; // Default to medium
        }

        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        super.paint(g);
        // Change background color here
        g.setColor(Color.DARK_GRAY);
        g.fillRect(1, 1, 692, 592);

        map.draw((Graphics2D) g);

        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        g.setColor(Color.BLUE);
        g.fillRect(playerX, 550, 100, 12);

        g.setColor(Color.RED);
        g.fillOval(ballposX, ballposY, 20, 20);

        g.setColor(Color.WHITE);
        g.setFont(new Font("MV Boli", Font.BOLD, 25));
        g.drawString("Score: " + score, 520, 30);

        if (totalBricks <= 0) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(new Color(0XFF6464));
            g.setFont(new Font("MV Boli", Font.BOLD, 30));
            g.drawString("You Won, Score: " + score, 190, 300);

            g.setFont(new Font("MV Boli", Font.BOLD, 20));
            g.drawString("Press Enter to Restart.", 230, 350);
        }

        if (ballposY > 570) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.BLACK);
            g.setFont(new Font("MV Boli", Font.BOLD, 30));
            g.drawString("Game Over, Score: " + score, 190, 300);

            g.setFont(new Font("MV Boli", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        timer.start();
        if (play) {
            if (new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYdir = -ballYdir;
            }
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
                        Rectangle brickRect = rect;

                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;

                            if (ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width)
                                ballXdir = -ballXdir;
                            else {
                                ballYdir = -ballYdir;
                            }
                        }
                    }
                }
            }

            ballposX += ballXdir;
            ballposY += ballYdir;
            if (ballposX < 0) {
                ballXdir = -ballXdir;
            }
            if (ballposY < 0) {
                ballYdir = -ballYdir;
            }
            if (ballposX > 670) {
                ballXdir = -ballXdir;
            }
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent arg0) {}

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) {
                playerX = 600;
            } else {
                moveRight();
            }
        }
        if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }

        if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ballposX = 120;
                ballposY = 350;
                ballXdir = -1;
                ballYdir = -2;
                score = 0;
                totalBricks = 21;
                // For simplicity, always restart with the same initial difficulty
                map = new MapGenerator(3, 7);
                repaint();
            }
        }
    }

    public void moveRight() {
        play = true;
        playerX += 50;
    }

    public void moveLeft() {
        play = true;
        playerX -= 50;
    }

    @Override
    public void keyReleased(KeyEvent arg0) {}

}

class DifficultySelectionPanel extends JPanel implements ActionListener {
    private JPanel cardsPanel; // Panel to hold cards
    private CardLayout cardLayout; // Card layout for switching between panels

    public DifficultySelectionPanel(JPanel cardsPanel) {
        this.cardsPanel = cardsPanel;
        this.cardLayout = (CardLayout) cardsPanel.getLayout();
        setLayout(new GridLayout(3, 1));

        JButton easyButton = new JButton("Easy");
        easyButton.addActionListener(this);
        add(easyButton);

        JButton mediumButton = new JButton("Medium");
        mediumButton.addActionListener(this);
        add(mediumButton);

        JButton hardButton = new JButton("Hard");
        hardButton.addActionListener(this);
        add(hardButton);
    }

    public void actionPerformed(ActionEvent e) {
        String difficulty = e.getActionCommand();
        if (difficulty.equals("Easy")) {
            cardLayout.show(cardsPanel, "gamePlayEasy");
            cardsPanel.getComponent(1).requestFocusInWindow(); // Request focus for GamePlay panel
        } else if (difficulty.equals("Medium")) {
            cardLayout.show(cardsPanel, "gamePlayMedium");
            cardsPanel.getComponent(2).requestFocusInWindow(); // Request focus for GamePlay panel
        } else if (difficulty.equals("Hard")) {
            cardLayout.show(cardsPanel, "gamePlayHard");
            cardsPanel.getComponent(3).requestFocusInWindow(); // Request focus for GamePlay panel
        }
    }
}

class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setBounds(10, 10, 700, 600);
        frame.setTitle("Brick Breaker");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a container panel to hold both panels
        JPanel cardsPanel = new JPanel(new CardLayout());

        // Create the difficulty selection panel
        DifficultySelectionPanel difficultyPanel = new DifficultySelectionPanel(cardsPanel);
        cardsPanel.add(difficultyPanel, "difficultySelection");

        // Create game panels for different difficulty levels
        GamePlay gamePlayEasy = new GamePlay(3, 7, 1); // Easy
        GamePlay gamePlayMedium = new GamePlay(4, 8, 2); // Medium
        GamePlay gamePlayHard = new GamePlay(5, 9, 3); // Hard

        // Add game panels to the cardsPanel
        cardsPanel.add(gamePlayEasy, "gamePlayEasy");
        cardsPanel.add(gamePlayMedium, "gamePlayMedium");
        cardsPanel.add(gamePlayHard, "gamePlayHard");

        // Add the cardsPanel to the frame
        frame.add(cardsPanel);

        // Set focus on the first game panel initially
        gamePlayEasy.requestFocusInWindow();

        frame.setVisible(true);
    }
}
