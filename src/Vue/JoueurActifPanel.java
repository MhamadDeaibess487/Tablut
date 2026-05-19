package Vue;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Hashtable;

import Controleur.Controle;
public class JoueurActifPanel extends JPanel {
    private static final Dimension SIDEBAR_SIZE = new Dimension(300, 0);
    private static final Color BG_DARK = new Color(18, 22, 32);
    private static final Color CARD_ACTIVE_BG = new Color(28, 34, 52);
    private static final Color CARD_INACTIVE_BG = new Color(16, 18, 26);
    private static final Color GOLD = new Color(200, 160, 80);
    private static final Color GOLD_DIM = new Color(80, 65, 35);
    private static final Color TEXT_ACTIVE = new Color(230, 220, 200);
    private static final Color TEXT_INACTIVE = new Color(90, 85, 75);
    // IA désactivée → rouge bordeaux (danger)
    private static final Color IA_OFF = new Color(120, 30, 30);   // rouge foncé
    private static final Color IA_OFF_TEXT = new Color(255, 120, 120); // rouge clair

    // IA activée → vert foncé (actif)
    private static final Color IA_ON  = new Color(30, 90, 50);    // vert foncé
    private static final Color IA_ON_TEXT = new Color(100, 220, 130); // vert clair
    private JPanel joueur1Card;
    private JPanel joueur2Card;
    private JLabel joueur1Image;
    private JLabel joueur2Image;
    private JLabel joueur1Titre;
    private JLabel joueur2Titre;
    private JLabel joueur1Camp;
    private JLabel joueur2Camp;
    private JLabel joueur1Pion;
    private JLabel joueur2Pion;
    private JLabel activeHeader1; // "JOUEUR ACTIF" label
    private JLabel activeHeader2; // "JOUEUR ACTIF" label

    private ImageIcon joueur1Normal;
    private ImageIcon joueur1Highlight;
    private ImageIcon joueur2Normal;
    private ImageIcon joueur2Highlight;
    private JButton joueur1Button;
    private JButton joueur2Button;
    private boolean joueur1_IA;
    private boolean joueur2_IA;
    private int joueur_actif;
    Controle controle;

    public JoueurActifPanel(String j1NormalPath, String j1HighlightPath,
                             String j2NormalPath, String j2HighlightPath,Controle controle) {
        this.controle = controle;

        this.joueur_actif= (controle.getJeu()).getJoueur();
        setOpaque(true);
        setBackground(BG_DARK);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(24, 14, 24, 14));
        setPreferredSize(SIDEBAR_SIZE);
        this.joueur1_IA = false;
        this.joueur2_IA = false;
        int imgW = 68, imgH = 68;
        joueur1Normal    = scaleIcon(new ImageIcon(j1NormalPath),    imgW, imgH);
        joueur1Highlight = scaleIcon(new ImageIcon(j1HighlightPath), imgW, imgH);
        joueur2Normal    = scaleIcon(new ImageIcon(j2NormalPath),    imgW, imgH);
        joueur2Highlight = scaleIcon(new ImageIcon(j2HighlightPath), imgW, imgH);

        // "JOUEUR ACTIF" floating header
        activeHeader1 = new JLabel("JOUEUR ACTIF", SwingConstants.CENTER);
        activeHeader1.setFont(new Font("SansSerif", Font.BOLD, 11));
        activeHeader1.setForeground(GOLD);
        activeHeader1.setAlignmentX(Component.CENTER_ALIGNMENT);
        activeHeader1.setMaximumSize(new Dimension(240, 22));

        activeHeader2 = new JLabel("JOUEUR ACTIF", SwingConstants.CENTER);
        activeHeader2.setFont(new Font("SansSerif", Font.BOLD, 11));
        activeHeader2.setForeground(GOLD);
        activeHeader2.setAlignmentX(Component.CENTER_ALIGNMENT);
        activeHeader2.setMaximumSize(new Dimension(240, 22));
        joueur1Image = new JLabel(joueur1Highlight); // starts active
        joueur2Image = new JLabel(joueur2Normal);
        joueur1Button = new JButton("IA/OFF");
        CustomSlider slider1 = new CustomSlider(0, 2, 1);
        CustomSlider slider2 = new CustomSlider(0, 2, 1);



// labels personnalisés
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(0, new JLabel("Facile"));
        labels.put(1, new JLabel("Moyen"));
        labels.put(2, new JLabel("Difficile"));
        slider1.setLabelTable(labels);
        slider1.setVisible(false);
        joueur2Button =  new JButton("IA/OFF");

        slider2.setVisible(false);
        slider1.setVisible(false);

        joueur1Card = buildCard("JOUEUR 1", "Noirs", joueur1Image, true, joueur1Button,joueur1_IA,slider1);
        joueur2Card = buildCard("JOUEUR 2", "Blancs",  joueur2Image, false,joueur2Button,joueur2_IA,slider2);

        // grab the text labels from cards for later updates
        joueur1Titre = findTitre(joueur1Card);
        joueur1Camp  = findCamp(joueur1Card);
        joueur1Pion  = findPion(joueur1Card);
        joueur2Titre = findTitre(joueur2Card);
        joueur2Camp  = findCamp(joueur2Card);
        joueur2Pion  = findPion(joueur2Card);
        joueur_actif= (controle.getJeu()).getJoueur();

        slider1.addChangeListener(e -> {
            if (!slider1.getValueIsAdjusting()) {
                int niveau = slider1.getValue(); // 0, 1 ou 2
                controle.setNiveauIANoir(niveau);
            }
        });
        slider2.addChangeListener(e -> {
            if (!slider2.getValueIsAdjusting()) {
                int niveau = slider2.getValue(); // 0, 1 ou 2
                controle.setNiveauIABlanc(niveau);
            }
        });
        joueur1Button.addActionListener(e -> {
            joueur1_IA = !joueur1_IA;

            if (joueur1_IA) {
                joueur1Button.setText("IA/ON");
                joueur1Button.setBackground(IA_ON);
                joueur1Button.setForeground(IA_ON_TEXT);
                slider1.setVisible(true);
            } else {
                joueur1Button.setText("IA/OFF");
                joueur1Button.setBackground(IA_OFF);
                joueur1Button.setForeground(IA_OFF_TEXT);
                slider1.setVisible(false);
            }
            joueur1Card.revalidate();
            joueur1Card.repaint();

            controle.set_IA_noir(joueur1_IA);
        });
        joueur2Button.addActionListener(e -> {
            joueur2_IA = !joueur2_IA;

            if (joueur2_IA) {
                joueur2Button.setText("IA/ON");
                joueur2Button.setBackground(IA_ON);
                joueur2Button.setForeground(IA_ON_TEXT);
                slider2.setVisible(true);
            } else {
                joueur2Button.setText("IA/OFF");
                joueur2Button.setBackground(IA_OFF);
                joueur2Button.setForeground(IA_OFF_TEXT);
                slider2.setVisible(false);
            }
            joueur2Card.revalidate();
            joueur2Card.repaint();

            controle.set_IA_blanc(joueur2_IA);
        });

        activeHeader1.setVisible(true);
        activeHeader2.setVisible(false);
        add(activeHeader1);
        add(joueur1Card);
        add(Box.createVerticalGlue()); // ← pousse joueur2 tout en bas
        add(activeHeader2);
        add(joueur2Card);
        setVisible(true);

    }



    public void setJoueurActif(int joueur) {
        if (joueur == 1) {
            activeHeader1.setVisible(true);
            activeHeader2.setVisible(false);
            joueur1Image.setIcon(joueur1Highlight);

            joueur2Image.setIcon(joueur2Normal);
            applyActiveStyle(joueur1Card, joueur1Titre, joueur1Camp, joueur1Pion, true);
            applyActiveStyle(joueur2Card, joueur2Titre, joueur2Camp, joueur2Pion, false);
        } else {
           activeHeader1.setVisible(false);
           activeHeader2.setVisible(true);
            joueur1Image.setIcon(joueur1Normal);

            joueur2Image.setIcon(joueur2Highlight);
            applyActiveStyle(joueur1Card, joueur1Titre, joueur1Camp, joueur1Pion, false);
            applyActiveStyle(joueur2Card, joueur2Titre, joueur2Camp, joueur2Pion, true);
        }
        repaint();
    }

    // ── private helpers ────────────────────────────────────────────────

    private JPanel buildCard(String titre, String camp, JLabel imgLabel, boolean active,JButton IA,boolean Ia_active,JSlider slider1) {
        // Circular image wrapper
        JPanel imgWrapper = new CircleImagePanel(imgLabel, active);
        imgWrapper.setPreferredSize(new Dimension(76, 76));
        imgWrapper.setMinimumSize(new Dimension(76, 76));
        imgWrapper.setMaximumSize(new Dimension(76, 76));

        JLabel titreLabel = new JLabel(titre);
        titreLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        titreLabel.setForeground(active ? TEXT_ACTIVE : TEXT_INACTIVE);
        titreLabel.putClientProperty("role", "titre");

        // Pion color circle
        JLabel pionCircle = new JLabel();
        pionCircle.setPreferredSize(new Dimension(14, 14));
        pionCircle.setMinimumSize(new Dimension(14, 14));
        pionCircle.setMaximumSize(new Dimension(14, 14));
        pionCircle.setOpaque(false);
        pionCircle.putClientProperty("role", "pion");

        JLabel campLabel = new JLabel(camp);
        campLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campLabel.setForeground(active ? new Color(200, 195, 185) : new Color(70, 65, 55));
        campLabel.putClientProperty("role", "camp");

        JPanel campRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        campRow.setOpaque(false);
        campRow.add(pionCircle);
        campRow.add(campLabel);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(titreLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(campRow);

        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setOpaque(true);
        card.setBackground(active ? CARD_ACTIVE_BG : CARD_INACTIVE_BG);
        card.setBorder(buildCardBorder(active));
        card.setPreferredSize(new Dimension(272, 165));
        card.setMinimumSize(new Dimension(272, 165));
        card.setMaximumSize(new Dimension(272, 190));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton IA_button = IA;
        IA_button.setBackground(Ia_active ? IA_ON : IA_OFF);
        IA_button.setForeground(Ia_active ? IA_ON_TEXT : IA_OFF_TEXT);
        IA_button.setFont(new Font("SansSerif", Font.BOLD, 12));
        IA_button.setOpaque(true);
        IA_button.setContentAreaFilled(true);  // ajouter juste après setFont
        IA_button.setPreferredSize(new Dimension(100, 28));
        IA_button.setMaximumSize(new Dimension(200, 28));
        IA_button.setMinimumSize(new Dimension(80, 28));
        IA.setAlignmentX(Component.LEFT_ALIGNMENT);  // aligne à gauche comme les labels
        textPanel.add(Box.createVerticalStrut(6));    // petit espace avant le bouton
        textPanel.add(IA);
        textPanel.add(slider1);

        JPanel imgPad = new JPanel(new GridBagLayout());
        imgPad.setOpaque(false);
        imgPad.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 0)); // ← marge
        imgPad.add(imgWrapper);
        card.add(imgPad, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private void applyActiveStyle(JPanel card, JLabel titre, JLabel camp, JLabel pion, boolean active) {
        card.setBackground(active ? CARD_ACTIVE_BG : CARD_INACTIVE_BG);
        card.setBorder(buildCardBorder(active));

        titre.setForeground(active ? TEXT_ACTIVE : TEXT_INACTIVE);
        camp.setForeground(active ? new Color(200, 195, 185) : new Color(70, 65, 55));

        // update the circle wrapper ring color
        Component west = ((BorderLayout) card.getLayout()).getLayoutComponent(BorderLayout.WEST);
        if (west instanceof JPanel) {
            for (Component c : ((JPanel) west).getComponents()) {
                if (c instanceof CircleImagePanel) {
                    ((CircleImagePanel) c).setActive(active);
                }
            }
        }
        card.repaint();
    }

    private Border buildCardBorder(boolean active) {
        Color borderColor = active ? GOLD : GOLD_DIM;
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, active ? 2 : 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
    }

    private JLabel findTitre(JPanel card) {
        Component center = ((BorderLayout) card.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (center instanceof JPanel) {
            for (Component c : ((JPanel) center).getComponents()) {
                if (c instanceof JLabel && "titre".equals(((JLabel) c).getClientProperty("role")))
                    return (JLabel) c;
            }
        }
        return new JLabel();
    }

    private JLabel findCamp(JPanel card) {
        Component center = ((BorderLayout) card.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (center instanceof JPanel) {
            for (Component c : ((JPanel) center).getComponents()) {
                if (c instanceof JPanel) {
                    for (Component inner : ((JPanel) c).getComponents()) {
                        if (inner instanceof JLabel && "camp".equals(((JLabel) inner).getClientProperty("role")))
                            return (JLabel) inner;
                    }
                }
            }
        }
        return new JLabel();
    }

    private JLabel findPion(JPanel card) {
        Component center = ((BorderLayout) card.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (center instanceof JPanel) {
            for (Component c : ((JPanel) center).getComponents()) {
                if (c instanceof JPanel) {
                    for (Component inner : ((JPanel) c).getComponents()) {
                        if (inner instanceof JLabel && "pion".equals(((JLabel) inner).getClientProperty("role")))
                            return (JLabel) inner;
                    }
                }
            }
        }
        return new JLabel();
    }

    private ImageIcon scaleIcon(ImageIcon icon, int w, int h) {
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // ── Inner class: circular clipped image with ring ──────────────────

    private static class CircleImagePanel extends JPanel {
        private final JLabel imgLabel;
        private boolean active;

        CircleImagePanel(JLabel imgLabel, boolean active) {
            this.imgLabel = imgLabel;
            this.active = active;
            setOpaque(false);
            setLayout(new GridBagLayout());
            add(imgLabel);
        }

        public void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int d = Math.min(getWidth(), getHeight()) - 4;
            int x = (getWidth() - d) / 2;
            int y = (getHeight() - d) / 2;

            // Dark circle background
            g2.setColor(active ? new Color(30, 36, 55) : new Color(14, 16, 22));
            g2.fillOval(x, y, d, d);

            // Draw image clipped to circle
            Icon icon = imgLabel.getIcon();
            if (icon != null) {
                int iw = icon.getIconWidth();
                int ih = icon.getIconHeight();
                int ix = (getWidth() - iw) / 2;   // ← centré horizontalement
                int iy = (getHeight() - ih) / 2;  // ← centré verticalement

                Shape clip = new java.awt.geom.Ellipse2D.Float(x + 3, y + 3, d - 6, d - 6);
                g2.setClip(clip);
                icon.paintIcon(this, g2, ix, iy);  // ← ix, iy au lieu de x+3, y+3
                g2.setClip(null);
            }

            // Gold ring
            g2.setColor(active ? new Color(200, 160, 80) : new Color(70, 58, 30));
            g2.setStroke(new BasicStroke(active ? 2.5f : 1.5f));
            g2.drawOval(x, y, d, d);

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() { return new Dimension(76, 76); }
    }


}