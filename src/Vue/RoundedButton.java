package Vue;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;

public class RoundedButton extends JButton {
    private final String imagePath;
    private int currentSize;
    private ImageIcon normalIcon;
    private ImageIcon hoverIcon;
    private ImageIcon pressedIcon;

    public RoundedButton(String imagePath,int size) {
        this.imagePath = imagePath;
        this.currentSize = size;

        try{
            this.normalIcon = resizeIcon(imagePath, size, size);
            this.hoverIcon = resizeIcon(imagePath, size + 6, size + 6);
            this.pressedIcon = resizeIcon(imagePath, size - 4, size - 4);
        }
        catch (IOException e) {
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
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setIcon(normalIcon);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                setIcon(pressedIcon);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                setIcon(hoverIcon);
            }
        });
    }


    public void updateSize(int size) throws IOException {
        if (size == currentSize) return;
        this.currentSize = size;

        // mettre à jour les 3 icônes
        this.normalIcon  = resizeIcon(imagePath, size, size);
        this.hoverIcon   = resizeIcon(imagePath, size + 6, size + 6);
        this.pressedIcon = resizeIcon(imagePath, size - 4, size - 4);

        setIcon(this.normalIcon); // ← utilise l'attribut
        Dimension d = new Dimension(size + 8, size + 8);
        setPreferredSize(d);
        setMaximumSize(d);
        setMinimumSize(d);
        revalidate();
        repaint();
    }
    private ImageIcon resizeIcon(String path, int width, int height) throws IOException{
        Image img = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream(path));
        img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}