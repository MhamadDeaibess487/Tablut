package Vue;

import Config.Config;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TutorielPanel extends JPanel {
    private static final Color BG = new Color(10, 18, 32);
    private static final Color PANEL_BG = new Color(14, 24, 42);
    private static final Color CARD_BG = new Color(18, 29, 50);
    private static final Color GOLD = new Color(214, 164, 68);
    private static final Color GOLD_SOFT = new Color(240, 225, 185);
    private static final Color TEXT = new Color(235, 228, 214);
    private static final Color TEXT_MUTED = new Color(205, 190, 165);

    private final List<TutorialSlide> slides;
    private int pageActuelle = 0;

    private final JLabel titreLabel;
    private final JLabel compteurLabel;
    private final JPanel centrePanel;
    private final JButton precedent;
    private final JButton suivant;
    private final JButton retourMenu;

    public TutorielPanel(Runnable retourMenuAction) {
        setLayout(new BorderLayout(0, 24));
        setBackground(BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        slides = creerSlides();

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        titreLabel = new JLabel("Tutoriel");
        titreLabel.setFont(new Font("Serif", Font.BOLD, 40));
        titreLabel.setForeground(GOLD_SOFT);

        compteurLabel = new JLabel("", SwingConstants.RIGHT);
        compteurLabel.setFont(new Font("Serif", Font.BOLD, 18));
        compteurLabel.setForeground(GOLD);

        header.add(titreLabel, BorderLayout.WEST);
        header.add(compteurLabel, BorderLayout.EAST);

        centrePanel = new JPanel(new BorderLayout());
        centrePanel.setOpaque(false);

        precedent = creerBouton("Precedent");
        suivant = creerBouton("Suivant");
        retourMenu = creerBouton("Retour menu");

        precedent.addActionListener(e -> pagePrecedente());
        suivant.addActionListener(e -> pageSuivante());
        retourMenu.addActionListener(e -> {
            if (retourMenuAction != null) {
                retourMenuAction.run();
            }
        });

        JPanel panelBoutons = new JPanel();
        panelBoutons.setOpaque(false);
        panelBoutons.add(precedent);
        panelBoutons.add(Box.createHorizontalStrut(12));
        panelBoutons.add(suivant);
        panelBoutons.add(Box.createHorizontalStrut(12));
        panelBoutons.add(retourMenu);

        add(header, BorderLayout.NORTH);
        add(centrePanel, BorderLayout.CENTER);
        add(panelBoutons, BorderLayout.SOUTH);

        afficherSlide();
    }

    public static void ouvrirDialog(Component parent) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Tutoriel", Dialog.ModalityType.APPLICATION_MODAL);

        TutorielPanel tutoriel = new TutorielPanel(dialog::dispose);
        dialog.setContentPane(tutoriel);
        dialog.setSize(1700, 1000);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private void pageSuivante() {
        if (pageActuelle < slides.size() - 1) {
            pageActuelle++;
            afficherSlide();
        }
    }

    private void pagePrecedente() {
        if (pageActuelle > 0) {
            pageActuelle--;
            afficherSlide();
        }
    }

    private void afficherSlide() {
        centrePanel.removeAll();

        if (slides.isEmpty()) {
            centrePanel.add(creerPanelVide(), BorderLayout.CENTER);
            titreLabel.setText("Tutoriel");
            compteurLabel.setText("0 / 0");
        } else {
            TutorialSlide slide = slides.get(pageActuelle);
            centrePanel.add(new SlideView(slide), BorderLayout.CENTER);
            titreLabel.setText(slide.titre);
            compteurLabel.setText((pageActuelle + 1) + " / " + slides.size());
        }

        mettreAJourBoutons();
        centrePanel.revalidate();
        centrePanel.repaint();
    }

    private void mettreAJourBoutons() {
        boolean hasSlides = !slides.isEmpty();
        precedent.setEnabled(hasSlides && pageActuelle > 0);
        suivant.setEnabled(hasSlides && pageActuelle < slides.size() - 1);
        suivant.setText(hasSlides && pageActuelle == slides.size() - 1 ? "Fin" : "Suivant");
    }

    private JPanel creerPanelVide() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createLineBorder(GOLD, 2));

        JLabel message = new JLabel("Aucune page de tutoriel configuree.");
        message.setFont(new Font("Serif", Font.BOLD, 28));
        message.setForeground(GOLD_SOFT);
        panel.add(message);
        return panel;
    }

    private JButton creerBouton(String text) {
        JButton bouton = new JButton(text);
        bouton.setFont(new Font("Serif", Font.BOLD, 22));
        bouton.setForeground(GOLD_SOFT);
        bouton.setBackground(new Color(18, 28, 48));
        bouton.setFocusPainted(false);
        bouton.setOpaque(true);
        bouton.setContentAreaFilled(true);
        bouton.setBorder(BorderFactory.createLineBorder(GOLD, 2));
        bouton.setPreferredSize(new Dimension(220, 54));
        bouton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return bouton;
    }

    private List<TutorialSlide> creerSlides() {
        List<TutorialSlide> data = new ArrayList<>();

        ajouterSlide(
                data,
                "Deplacements",
                "Toutes les pieces se deplacent en ligne droite, horizontalement ou verticalement, "
                        + "sur autant de cases libres que voulu. Aucune piece ne saute par-dessus une autre.",
                Config.CHEMIN_TUTO + "/tuto4.png",
                Config.CHEMIN_IMAGES + "/selected.png"
        );

        ajouterSlide(
                data,
                "LA capture",
                "Un pion normal est capturé lorsqu’il est coincé entre deux pions adverses sur une même ligne ou une même colonne."

                        +"La capture se fait après le déplacement d’un pion ennemi",
                Config.CHEMIN_TUTO + "/tuto3.png",
                Config.CHEMIN_TUTO + "/tuto9.png"
        );


        ajouterSlide(
            data,
            "Objectif Des Blancs",
            "Les Blancs gagnent lorsque le roi atteint n'importe quelle case du bord. "
                + "Le roi part du trone au centre et doit s'echapper vers l'exterieur.",
            Config.CHEMIN_TUTO + "/tuto1.png",
            Config.CHEMIN_IMAGES + "/king.png"
        );

        ajouterSlide(
            data,
            "Objectif Des Noirs",
            "Les Noirs gagnent en capturant le roi avant sa fuite. "
                + "Ils doivent refermer les lignes, couper les couloirs et bloquer le centre.",
            Config.CHEMIN_TUTO + "/tuto2.png",
            Config.CHEMIN_IMAGES + "/black.png"
        );

        ajouterSlide(
                data,
                "Le Trone Et Les Cases",
                "Le trone reste une case speciale au centre. Il influence les captures et la circulation "
                        + "autour du roi. Les coins et certaines cases speciales sont decisifs pour la victoire.",
                Config.CHEMIN_IMAGES + "/throne.png",
                Config.CHEMIN_IMAGES + "/centrale.png"
        );

        ajouterSlide(
                data,
                "Capture du roi on throne",
                "Lorsque le roi se trouve sur le trône, il bénéficie d’une protection particulière.\n"
                        + "Pour le capturer, les Noirs doivent l’encercler sur ses quatre côtés :",
                Config.CHEMIN_TUTO + "/tuto5.png",null
        );


        ajouterSlide(
                data,
                "Capture du roi adjacent au throne",
                "il est capturé s’il est entouré sur 3 côtés"+" le trône comptant comme le 4ᵉ côté.",
                Config.CHEMIN_TUTO + "/tuto6.png",null
        );

        ajouterSlide(
                data,
                "Capture d'un pion avec throne",
                "        Le trône peut participer à la capture d’un pion normal." +
                        "Un pion est capturé lorsqu’il se retrouve coincé entre un pion adverse et le trône, sur une même ligne ou une même colonne",
                Config.CHEMIN_TUTO + "/tuto7.png",null
        );

        ajouterSlide(
                data,
                "Draw",
                "Si la même position de jeu se répète trois fois, la partie est déclarée nulle (draw)"
                ,
                Config.CHEMIN_IMAGES + "/draw.png",
                null
        );
        ajouterSlide(
                data,
                "roi hors throne",
                "Une fois que le roi a quitté le trône, il ne peut plus y revenir pendant la partie."
                ,
                Config.CHEMIN_TUTO + "/tuto8.png",
                null
        );

        ajouterSlide(
            data,
            "Fin De Partie",
            "Une partie peut se terminer par victoire des Noirs, victoire des Blancs ou nulle(draw) "
                + "si une repetition detectee est atteinte(une configuration de jeu est repetee 3 fois).",
            Config.CHEMIN_IMAGES + "/blackWin.png",
            Config.CHEMIN_IMAGES + "/whiteWin.png"
        );

        return data;
    }

    private void ajouterSlide(List<TutorialSlide> data, String titre, String description, String imageGauche, String imageDroite) {
        data.add(new TutorialSlide(titre, description, imageGauche, imageDroite));
    }

    private static class TutorialSlide {
        private final String titre;
        private final String description;
        private final String imageGauche;
        private final String imageDroite;

        private TutorialSlide(String titre, String description, String imageGauche, String imageDroite) {
            this.titre = titre;
            this.description = description;
            this.imageGauche = imageGauche;
            this.imageDroite = imageDroite;
        }
    }

    private static class SlideView extends JPanel {
        private SlideView(TutorialSlide slide) {
            setOpaque(false);
            setLayout(new BorderLayout(0, 22));

            JPanel imageZone = new JPanel(new GridLayout(1, slide.imageDroite == null ? 1 : 2, 18, 0));
            imageZone.setOpaque(false);
            imageZone.add(creerImageCard(slide.imageGauche));
            if (slide.imageDroite != null) {
                imageZone.add(creerImageCard(slide.imageDroite));
            }

            JPanel texteZone = new JPanel(new BorderLayout());
            texteZone.setBackground(PANEL_BG);
            texteZone.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 2),
                new EmptyBorder(24, 28, 24, 28)
            ));

            JLabel titre = new JLabel(slide.titre, SwingConstants.CENTER);
            titre.setFont(new Font("Serif", Font.BOLD, 42));
            titre.setForeground(GOLD);

            JTextArea texte = new JTextArea(slide.description);
            texte.setWrapStyleWord(true);
            texte.setLineWrap(true);
            texte.setEditable(false);
            texte.setOpaque(false);
            texte.setForeground(TEXT);
            texte.setFont(new Font("Serif", Font.PLAIN, 30));
            texte.setBorder(null);
            texte.setFocusable(false);

            texteZone.add(titre, BorderLayout.NORTH);
            texteZone.add(texte, BorderLayout.CENTER);

            add(imageZone, BorderLayout.CENTER);
            add(texteZone, BorderLayout.SOUTH);
        }

        private JPanel creerImageCard(String chemin) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 2),
                new EmptyBorder(14, 14, 14, 14)
            ));

            BufferedImage image = chargerImage(chemin);
            if (image == null) {
                JLabel vide = new JLabel("Image introuvable", SwingConstants.CENTER);
                vide.setForeground(TEXT_MUTED);
                vide.setFont(new Font("Serif", Font.BOLD, 24));
                card.add(vide, BorderLayout.CENTER);
                return card;
            }

            card.add(new ImagePreviewPanel(image), BorderLayout.CENTER);
            return card;
        }

        private BufferedImage chargerImage(String chemin) {
            if (chemin == null || chemin.isBlank()) {
                return null;
            }

            try {
                return ImageIO.read(new File(chemin));
            } catch (IOException e) {
                return null;
            }
        }
    }

    private static class ImagePreviewPanel extends JPanel {
        private final BufferedImage image;

        private ImagePreviewPanel(BufferedImage image) {
            this.image = image;
            setOpaque(false);
            setPreferredSize(new Dimension(540, 360));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int panelW = getWidth();
            int panelH = getHeight();
            int imgW = image.getWidth();
            int imgH = image.getHeight();

            double scale = Math.min((double) panelW / imgW, (double) panelH / imgH);
            int drawW = (int) (imgW * scale);
            int drawH = (int) (imgH * scale);
            int x = (panelW - drawW) / 2;
            int y = (panelH - drawH) / 2;

            g2.drawImage(image, x, y, drawW, drawH, null);
            g2.dispose();
        }
    }
}
