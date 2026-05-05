package Vue;

import Config.Config;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;

public class GrilleGraphique extends JPanel {

    private Image whiteImage;
    private Image blackImage;
    private Image kingImage;
    private Image throneImage;
    private Image backgroundImage;
    private Color entourageColor = new Color(200, 200, 200);

    public GrilleGraphique() {
        try {
            whiteImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/white.png"));
            blackImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/black.png"));
            kingImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/king.png"));
            throneImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/throne.png"));
            backgroundImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        int width = getWidth();
        int height = getHeight();

        // Dessiner 
    }
}
