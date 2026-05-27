package Vue;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class RoundedButton extends JButton {
    private final String imagePath;
    private int currentSize;
    private ImageIcon normalIcon;
    private ImageIcon hoverIcon;
    private ImageIcon pressedIcon;

    public RoundedButton(String imagePath, int size) {
        this.imagePath = imagePath;
        this.currentSize = size;

        try {
            this.normalIcon  = resizeIcon(imagePath, size,     size);
            this.hoverIcon   = resizeIcon(imagePath, size + 6, size + 6);
            this.pressedIcon = resizeIcon(imagePath, size - 4, size - 4);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setIcon(normalIcon);

        Dimension d = new Dimension(size + 8, size + 8);
        setPreferredSize(d);
        setMaximumSize(d);
        setMinimumSize(d);

        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e)  { setIcon(hoverIcon);   }
            @Override public void mouseExited(java.awt.event.MouseEvent e)   { setIcon(normalIcon);  }
            @Override public void mousePressed(java.awt.event.MouseEvent e)  { setIcon(pressedIcon); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { setIcon(hoverIcon);   }
        });
    }

    public void updateSize(int size) throws IOException {
        if (size == currentSize) return;
        this.currentSize = size;

        this.normalIcon  = resizeIcon(imagePath, size,     size);
        this.hoverIcon   = resizeIcon(imagePath, size + 6, size + 6);
        this.pressedIcon = resizeIcon(imagePath, size - 4, size - 4);

        setIcon(this.normalIcon);
        Dimension d = new Dimension(size + 8, size + 8);
        setPreferredSize(d);
        setMaximumSize(d);
        setMinimumSize(d);
        revalidate();
        repaint();
    }

    /**
     * Loads an image from the JAR (classpath) and scales it to the requested size.
     * The path should be relative to the classpath root, e.g.
     *   "res/images/Theme_principal/undo.png"
     */
    private static ImageIcon resizeIcon(String path, int width, int height) throws IOException {
        // Strip leading slash if present, then prepend one for getResourceAsStream
        String resourcePath = path.startsWith("/") ? path : "/" + path;
        InputStream is = RoundedButton.class.getResourceAsStream(resourcePath);

        if (is == null) {
            // Fallback: try without leading slash via system classloader
            is = ClassLoader.getSystemResourceAsStream(path);
        }
        if (is == null) {
            throw new IOException("Image resource not found: " + path);
        }

        BufferedImage original = ImageIO.read(is);
        is.close();

        // Actually scale the image (this was the bug — getScaledInstance result was discarded)
        Image scaled = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        // Wait for the scaling to complete by drawing into a BufferedImage
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = result.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(scaled, 0, 0, null);
        g2.dispose();

        return new ImageIcon(result);
    }
}