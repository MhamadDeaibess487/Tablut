package Vue;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.util.Hashtable;

public class CustomSlider extends JSlider {

    public CustomSlider(int min, int max, int value) {
        super(min, max, value);

        setOpaque(false);
        setFocusable(false);

        setMajorTickSpacing(1);
        setPaintTicks(true);
        setPaintLabels(false);
        setSnapToTicks(true);



        setUI(new CustomSliderUI(this));

        setPreferredSize(new Dimension(180, 60));
        setMaximumSize(new Dimension(180, 60));
        setMinimumSize(new Dimension(180, 60));
    }



    private static class CustomSliderUI extends BasicSliderUI {

        private final Color trackColor = new Color(35, 45, 70);
        private final Color progressColor = new Color(190, 145, 70);
        private final Color thumbColor = new Color(230, 200, 120);

        public CustomSliderUI(JSlider slider) {
            super(slider);
        }

        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int cy = trackRect.y + trackRect.height / 2;
            int h = 8;

            // barre complète
            g2.setColor(trackColor);
            g2.fillRoundRect(trackRect.x, cy - h / 2, trackRect.width, h, h, h);

            // partie remplie
            int valueX = thumbRect.x + thumbRect.width / 2;

            g2.setColor(progressColor);
            g2.fillRoundRect(trackRect.x, cy - h / 2,
                    valueX - trackRect.x, h, h, h);

            // tirets / repères des 3 niveaux
            g2.setColor(new Color(255, 230, 150));
            g2.setStroke(new BasicStroke(2.2f));

            for (int i = slider.getMinimum(); i <= slider.getMaximum(); i++) {
                int x = xPositionForValue(i);

                g2.drawLine(
                        x,
                        cy - 10,
                        x,
                        cy + 10
                );
            }

            g2.dispose();
        }
        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int size = 22;
            int x = thumbRect.x + (thumbRect.width - size) / 2;
            int y = thumbRect.y + (thumbRect.height - size) / 2;

            g2.setColor(thumbColor);
            g2.fillOval(x, y, size, size);

            g2.setColor(new Color(90, 65, 30));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x, y, size, size);

            g2.dispose();
        }

        @Override
        protected Dimension getThumbSize() {
            return new Dimension(28, 28);
        }
    }
}