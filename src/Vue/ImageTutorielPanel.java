package Vue;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ImageTutorielPanel extends JPanel {

    private Image image;

    public ImageTutorielPanel(String cheminImage) {
        setBackground(new Color(16, 20, 30));

        try {
            image = ImageIO.read(new File(cheminImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        int panelW = getWidth();
        int panelH = getHeight();

        int imgW = image.getWidth(this);
        int imgH = image.getHeight(this);

        double ratioImage = (double) imgW / imgH;

        int largeurMax = (int) (panelW * 1);
        int hauteurMax = (int) (panelH * 1);

        int largeur = largeurMax;
        int hauteur = (int) (largeur / ratioImage);

        if (hauteur > hauteurMax) {
            hauteur = hauteurMax;
            largeur = (int) (hauteur * ratioImage);
        }

        int x = (panelW - largeur) / 2;
        int y = (panelH - hauteur) / 2;

        g2.drawImage(image, x, y, largeur, hauteur, this);
    }
}