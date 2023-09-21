import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class Main {

    public class Food {

        private int posX;
        private int posY;

        public Food() {
            posX = generatePos(Graphics.WIDTH);
            posY = generatePos(Graphics.HEIGHT);
        }

        public Food(int[][] obstacleMap) {

            posX = generatePos(Graphics.WIDTH);
            posY = generatePos(Graphics.HEIGHT);

            while (obstacleMap[posX][posY] == 1) {
                posX = generatePos(Graphics.WIDTH);
                posY = generatePos(Graphics.HEIGHT);
            }
        }

        private int generatePos(int size) {
            Random random = new Random();
            return random.nextInt(size / Graphics.TICK_SIZE) * Graphics.TICK_SIZE;
        }

        public int getPosX() {
            return posX;
        }

        public int getPosY() {
            return posY;
        }
    }

    public class Game extends JFrame {

        public Game() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
            this.add(new Graphics());
            this.setTitle("Eat or It");
            this.pack();
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setResizable(false);
            this.setVisible(true);
            this.setLocationRelativeTo(null);
        }
    }

    public class Sound {

        private Clip clip;

        public Sound(String fileName) {
            // specify the sound to play
            // (assuming the sound can be played by the audio system)
            // from a wave File
            try {
                URL soundURL = getClass().getResource(fileName);
                // File file = new File(fileName);
                //if (file.exists()) {
                AudioInputStream sound = AudioSystem.getAudioInputStream(soundURL);
                // load the sound into memory (a Clip)
                clip = AudioSystem.getClip();
                clip.open(sound);
                //}
                //else {
                //throw new RuntimeException("Sound: file not found: " + fileName);
                //}
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException("Sound: Malformed URL: " + e);
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
                throw new RuntimeException("Sound: Unsupported Audio File: " + e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Sound: Input/Output Error: " + e);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
                throw new RuntimeException("Sound: Line Unavailable Exception Error: " + e);
            }

            // play, stop, loop the sound clip
        }

        public void play() {
            clip.setFramePosition(0);  // Must always rewind!
            clip.start();
        }

        public void loop() {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }

        public void stop() {
            clip.stop();
        }

    }

    public class Graphics extends JPanel implements ActionListener {

        final Font font = new Font("TimesRoman", Font.BOLD, 30);

        int selection;
        int winner; // test
        static final int WINNING_SCORE = 10;
        // static final int SCORE_CHANGE_RATE = 2;
        static final int WIDTH = 1200;
        static final int HEIGHT = 600;
        static int TICK_SIZE = 25; // changing this changes square size
        static final int BOARD_SIZE = (WIDTH * HEIGHT) / TICK_SIZE;
        static final int HUD_HEIGHT = HEIGHT + TICK_SIZE * 9;
        static int gameNumber = 0;
        String[] options = {"Large", "Normal", "Small"};

        int[] tickOptions = {15, 25, 50};

        // int deathDelay = 500;
        // Timer deathTimer = new Timer(500, this);

    /*
    static final int SLOW_TIME = 300;
    static final int NORMAL_TIME = 150;
    static final int FAST_TIME = 100;
    static final int EXTREME_TIME = 50; */

        //final Timer moveTimer = new Timer()

        int[][] obstacleMap = new int[WIDTH][HEIGHT];

        int bigScore1 = 0;
        int bigScore2 = 0;

        int[] snakePosX = new int[BOARD_SIZE];
        int[] snakePosY = new int[BOARD_SIZE];
        int snakeLength;

        int[] snakePosX2 = new int[BOARD_SIZE];
        int[] snakePosY2 = new int[BOARD_SIZE];
        int snakeLength2;

        Food food;
        int foodEaten;

        char direction = 'R';
        char direction2 = 'L';
        int timerStartDelay = 400;
        int timerDelay = timerStartDelay;
        int setDelay = 150; // change this to change game speed - 150 original
        String[] speedOptions = {"Fast", "Normal", "Slow"};
        int[] speeds = {75, 150, 250};
        final Timer timer = new Timer(setDelay, this); // original delay = 150
        boolean isMoving = false;
        int isIt;
        int ran = (int) (Math.random() * 10);

        int score1 = 0;
        int score2 = 0;

        Random random = new Random();
        String[] songList = {"Vampire", "HowMuch", "UpsideDown"};

        // Sounds
        Sound chompSound = new Sound("Chomp.wav");
        Sound song;
        Sound chompSound2 = new Sound("Chomp2.wav");

        public Graphics() throws LineUnavailableException, IOException {

            timer.stop();

            this.setPreferredSize(new Dimension(WIDTH, HUD_HEIGHT));
            this.setBackground(Color.WHITE);
            this.setFocusable(true);

            this.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (isMoving) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_LEFT:
                                if (direction != 'R') {
                                    direction = 'L';
                                }
                                break;
                            case KeyEvent.VK_RIGHT:
                                if (direction != 'L') {
                                    direction = 'R';
                                }
                                break;
                            case KeyEvent.VK_UP:
                                if (direction != 'D') {
                                    direction = 'U';
                                }
                                break;
                            case KeyEvent.VK_DOWN:
                                if (direction != 'U') {
                                    direction = 'D';
                                }
                                break;
                            // Snake 2
                            case KeyEvent.VK_A:
                                if (direction2 != 'R') {
                                    direction2 = 'L';
                                }
                                break;
                            case KeyEvent.VK_D:
                                if (direction2 != 'L') {
                                    direction2 = 'R';
                                }
                                break;
                            case KeyEvent.VK_W:
                                if (direction2 != 'D') {
                                    direction2 = 'U';
                                }
                                break;
                            case KeyEvent.VK_S:
                                if (direction2 != 'U') {
                                    direction2 = 'D';
                                }
                                break;
                        }
                    } else {
                        start();
                    }
                }
            });


            start();
        }

        protected void start() {

            // TODO
            selection = JOptionPane.showOptionDialog(null, "Select arena size", "Arena Size", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, 1);
            if (selection == 0) {
                TICK_SIZE = tickOptions[0];
            } else if (selection == 1) {
                TICK_SIZE = tickOptions[1];
            } else if (selection == 2) {
                TICK_SIZE = tickOptions[2];
            }

            selection = JOptionPane.showOptionDialog(null, "Select game speed", "Game Speed", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, speedOptions, 1);
            if (selection == 0) {
                setDelay = speeds[0];
            } else if (selection == 1) {
                setDelay = speeds[1];
            } else if (selection == 2) {
                setDelay = speeds[2];
            }
            timer.setDelay(setDelay);

            if ((ran % 2) == 0) {
                isIt = 1;
            } else {
                isIt = 2;
            }
            gameNumber++;
            // timerDelay = timerStartDelay / gameNumber;

            obstacleMap = new int[WIDTH][HEIGHT];
            // obstacle gen - no more than 2% of the map is theoretically covered randomly
            // the first TICK_SIZE * __ is not covered for spawn protection
            for (int i = TICK_SIZE * 7; i < WIDTH - (TICK_SIZE * 7); i += TICK_SIZE) {
                for (int j = TICK_SIZE; j < HEIGHT; j += TICK_SIZE) {
                    double randNum = Math.random();
                    if (randNum < 0.02) {
                        obstacleMap[i][j] = 1;
                    }
                }
            }

            snakePosX = new int[BOARD_SIZE];
            snakePosY = new int[BOARD_SIZE];
            snakeLength = 5;
            direction = 'R';

            snakePosX2 = new int[BOARD_SIZE];
            snakePosY2 = new int[BOARD_SIZE];
            snakePosX2[0] = WIDTH - TICK_SIZE;
            snakePosY2[0] = HEIGHT - TICK_SIZE;

            snakeLength2 = 5;
            direction2 = 'L';

            foodEaten = 0;
            score1 = 0;
            score2 = 0;

            spawnFood();
            // timer.setDelay(timerDelay);
            isMoving = true;
            timer.start();

            int randNum = (random.nextInt(songList.length));
            song = new Sound(songList[randNum] + ".wav");
            song.play();
            song.loop();
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);

            // draw arena
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            // draw hud
            g.setFont(font);
            g.setColor(Color.BLACK);
            g.fillRect(0, HEIGHT, WIDTH, HUD_HEIGHT);

            String scoreStr1 = String.format("%d", bigScore1);
            String scoreStr2 = String.format("%d", bigScore2);

            if (selection == 1) {
                g.setColor(Color.WHITE);
                g.drawString("Score:", TICK_SIZE, HUD_HEIGHT - 7 * TICK_SIZE);
                g.setColor(Color.GREEN);
                g.drawString(scoreStr1, TICK_SIZE, HUD_HEIGHT - 5 * TICK_SIZE);
                g.setColor(Color.RED);
                g.drawString(scoreStr2, TICK_SIZE, HUD_HEIGHT - 3 * TICK_SIZE);
                g.setColor(Color.WHITE);
                g.drawString(String.format("Round %d", gameNumber), TICK_SIZE, HUD_HEIGHT - TICK_SIZE);
            } else if (selection == 0) { // large
                g.setColor(Color.WHITE);
                g.drawString("Score:", TICK_SIZE, HEIGHT + 2 * TICK_SIZE);
                g.setColor(Color.GREEN);
                g.drawString(scoreStr1, TICK_SIZE, HEIGHT + 5 * TICK_SIZE);
                g.setColor(Color.RED);
                g.drawString(scoreStr2, TICK_SIZE, HEIGHT + 8 * TICK_SIZE);
                g.setColor(Color.WHITE);
                g.drawString(String.format("Round %d", gameNumber), TICK_SIZE, HEIGHT + 11 * TICK_SIZE);
            } else if (selection == 2) { // small
                g.setColor(Color.WHITE);
                g.drawString("Score:", TICK_SIZE, HEIGHT + TICK_SIZE);
                g.setColor(Color.GREEN);
                g.drawString(scoreStr1, TICK_SIZE, HEIGHT + 2 * TICK_SIZE);
                g.setColor(Color.RED);
                g.drawString(scoreStr2, TICK_SIZE, HEIGHT + 3 * TICK_SIZE);
                g.setColor(Color.WHITE);
                g.drawString(String.format("Round %d", gameNumber), TICK_SIZE, HUD_HEIGHT - TICK_SIZE / 2);
            }


            // obstacles generation
            g.setColor(Color.PINK);
            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    if (obstacleMap[i][j] == 1) {
                        g.fillRect(i, j, TICK_SIZE, TICK_SIZE);
                    }
                }
            }


            if (isMoving) {
                g.setColor(Color.BLUE);
                g.fillOval(food.getPosX(), food.getPosY(), TICK_SIZE, TICK_SIZE);
                for (int i = 0; i < snakeLength; i++) {

                    if (i == 0) {
                        g.setColor(Color.YELLOW);
                        g.fillRect(snakePosX[i], snakePosY[i], TICK_SIZE, TICK_SIZE);
                    } else {
                        g.setColor(Color.GREEN);
                        g.fillRect(snakePosX[i], snakePosY[i], TICK_SIZE, TICK_SIZE);
                    }

                    if (i == 0) {
                        g.setColor(Color.YELLOW);
                        g.fillRect(snakePosX2[i], snakePosY2[i], TICK_SIZE, TICK_SIZE);
                    } else {
                        g.setColor(Color.RED);
                        g.fillRect(snakePosX2[i], snakePosY2[i], TICK_SIZE, TICK_SIZE);
                    }

                    String color = "red";
                    if (isIt == 1) {
                        color = "GREEN";
                        //g.setColor(Color.GREEN);
                    } else {
                        color = "RED";
                        //g.setColor(Color.RED);
                    }

                    /**
                     HUD - bottom center below arena
                     **/
                    // isIT
                    String isItText = String.format("%s is it!", color);
                    g.setColor(Color.WHITE);
                    g.setFont(font);
                    g.drawString(isItText, (WIDTH - getFontMetrics(g.getFont()).stringWidth(isItText)) / 2, HUD_HEIGHT - 150);

                    // score
                    String score1Text = String.format("Green: %d", score1);
                    g.setColor(Color.GREEN);
                    g.drawString(score1Text, (WIDTH - getFontMetrics(g.getFont()).stringWidth(isItText)) / 2, HUD_HEIGHT - 100);

                    String score2Text = String.format("Red: %d", score2);
                    g.setColor(Color.RED);
                    g.drawString(score2Text, (WIDTH - getFontMetrics(g.getFont()).stringWidth(isItText)) / 2, HUD_HEIGHT - 50);

                }
            } else {
                String winText;
                String pressText = "Press any key to play again";
                g.setFont(font);
                ran = (int) (Math.random() * 10);

                if (winner == 1) {
                    bigScore1++;
                    g.setColor(Color.GREEN);
                    winText = String.format("Green wins!");
                    g.drawString(winText, (WIDTH - getFontMetrics(g.getFont()).stringWidth(winText)) / 2, HEIGHT / 2);
                    g.setColor(Color.WHITE);
                    g.drawString(pressText, (WIDTH - getFontMetrics(g.getFont()).stringWidth(pressText)) / 2, (HEIGHT / 2) + 50);


                } else {
                    bigScore2++;
                    g.setColor(Color.RED);
                    winText = String.format("Red wins!");
                    g.drawString(winText, (WIDTH - getFontMetrics(g.getFont()).stringWidth(winText)) / 2, HEIGHT / 2);
                    g.setColor(Color.WHITE);
                    g.drawString(pressText, (WIDTH - getFontMetrics(g.getFont()).stringWidth(pressText)) / 2, (HEIGHT / 2) + 50);
                }


                //start();

            /*
            String scoreText = String.format("The End...Score %d...Press any key to play again!", foodEaten);
            g.setColor(Color.BLACK);
            g.setFont(font);
            g.drawString(scoreText, (WIDTH - getFontMetrics(g.getFont()).stringWidth(scoreText)) / 2, HEIGHT / 2);
             */
            }
        }

        protected void move() {
            for (int i = snakeLength; i > 0; i--) {
                snakePosX[i] = snakePosX[i - 1];
                snakePosY[i] = snakePosY[i - 1];

                snakePosX2[i] = snakePosX2[i - 1];
                snakePosY2[i] = snakePosY2[i - 1];
            }
            if (isIt == 1) {
                //score2++;
            } else {
                //score1++;
            }

            if (score1 == WINNING_SCORE) {
                isMoving = false;
                winner = 1;
            } else if (score2 == WINNING_SCORE) {
                isMoving = false;
                winner = 2;
            } else {
                // nothing
            }

            switch (direction) {
                case 'U' -> snakePosY[0] -= TICK_SIZE;
                case 'D' -> snakePosY[0] += TICK_SIZE;
                case 'L' -> snakePosX[0] -= TICK_SIZE;
                case 'R' -> snakePosX[0] += TICK_SIZE;
            }

            switch (direction2) {
                case 'U' -> snakePosY2[0] -= TICK_SIZE;
                case 'D' -> snakePosY2[0] += TICK_SIZE;
                case 'L' -> snakePosX2[0] -= TICK_SIZE;
                case 'R' -> snakePosX2[0] += TICK_SIZE;
            }
        }

        protected void spawnFood() {
            food = new Food(obstacleMap);
        }

        protected void eatFood() throws LineUnavailableException, IOException {
            if ((snakePosX[0] == food.getPosX()) && (snakePosY[0] == food.getPosY())) {
                //snakeLength++;
                //foodEaten++;
                spawnFood();

                if (isIt == 2) {
                    score1++;
                    chompSound.play();
                }
            }

            if ((snakePosX2[0] == food.getPosX()) && (snakePosY2[0] == food.getPosY())) {
                //snakeLength++;
                //foodEaten++;
                spawnFood();
                if (isIt == 1) {
                    score2++;
                    chompSound2.play();
                }
            }

        }

        protected void collisionTest() {
            for (int i = snakeLength; i > 0; i--) {
                if ((snakePosX[0] == snakePosX[i]) && (snakePosY[0] == snakePosY[i])) {
                    winner = 2;
                    isMoving = false;
                    break;
                }
                if ((snakePosX2[0] == snakePosX2[i]) && (snakePosY2[0] == snakePosY2[i])) {
                    winner = 1;
                    isMoving = false;
                    break;
                }

                // test snakes run into each other
                if (isIt == 1) {
                    if ((snakePosX[0] == snakePosX2[i]) && (snakePosY[0] == snakePosY2[i])) {
                        isIt = 2;
                        break;
                    }
                } else {
                    if ((snakePosX2[0] == snakePosX[i]) && (snakePosY2[0] == snakePosY[i])) {
                        isIt = 1;
                        break;
                    }
                }
            }

            if (snakePosX[0] < 0 || snakePosX[0] > WIDTH - TICK_SIZE || snakePosY[0] < 0 || snakePosY[0] > HEIGHT - TICK_SIZE) {
                winner = 2;
                isMoving = false;
            }
            if (snakePosX2[0] < 0 || snakePosX2[0] > WIDTH - TICK_SIZE || snakePosY2[0] < 0 || snakePosY2[0] > HEIGHT - TICK_SIZE) {
                winner = 1;
                isMoving = false;
            }

            // test obstacle collision
            for (int i = 0; i < WIDTH; i += TICK_SIZE) {
                for (int j = 0; j < HEIGHT; j += TICK_SIZE) {
                    if (obstacleMap[i][j] == 1) {
                        if ((snakePosX[0] == i) && (snakePosY[0] == j)) {
                            winner = 2;
                            isMoving = false;
                        }
                        if ((snakePosX2[0] == i) && (snakePosY2[0] == j)) {
                            winner = 1;
                            isMoving = false;
                        }
                    }
                }
            }

            if (!isMoving) {
                song.stop();
                timer.stop();
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (isMoving) {
                move();
                collisionTest();
                try {
                    eatFood();
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            repaint();
        }
    }

    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        new Game();
    }
}
