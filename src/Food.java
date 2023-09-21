import java.util.Random;

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