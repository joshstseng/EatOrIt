import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.IOException;

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
