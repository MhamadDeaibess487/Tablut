package Vue;

import java.io.File;
import Model.BaseJeu;
import Model.Jeu;
import Reseau.ServerTablut;
import Config.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import static Config.Config.CHEMIN_SAUVEGARDES;

public class MenuGraphique extends JFrame {

    private BaseJeu jeu;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private int nbPartie;


    public MenuGraphique(BaseJeu jeu) {
        this.jeu = jeu;
        setTitle("Menu Principal - Tablut");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(creerMenuPrincipal(), "MENU");
        mainPanel.add(creerMenuOptions(), "OPTIONS");

        add(mainPanel);

        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(esc, "MENU");
        getRootPane().getActionMap().put("MENU", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "MENU");
            }
        });

        setVisible(true);
    }

    private JPanel creerMenuPrincipal() {
        BackgroundPanel panel = new BackgroundPanel(Config.CHEMIN_IMAGES + "/background_new.png");
        panel.setLayout(new BorderLayout());

        JPanel menuBox = new JPanel();
        menuBox.setOpaque(false);
        menuBox.setLayout(new BoxLayout(menuBox, BoxLayout.Y_AXIS));
        menuBox.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 120));

        JButton nouvellePartie = imageButton("btn_new_game.png", "btn_new_game_hover.png");
        JButton online = imageButton("btn_online.png", "btn_online_hover.png");
        JButton chargerPartie = imageButton("btn_load.png", "btn_load_hover.png");
        JButton options = imageButton("btn_options.png", "btn_options_hover.png");
        JButton quitter = imageButton("btn_quit.png", "btn_quit_hover.png");

        JPanel titleBlock = buildTitleBlock();
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeMenuLayout(
                        panel,
                        menuBox,
                        titleBlock,
                        nouvellePartie,
                        online,
                        chargerPartie,
                        options,
                        quitter
                );
            }
        });
        nouvellePartie.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                ((Model.Jeu)jeu).nouvellePartie();
                FenetreJeu fenetreJeu = new FenetreJeu(jeu);
                fenetreJeu.setVisible(true);
            }
        });

        online.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ouvrirMenuMultijoueur();
            }
        });

        chargerPartie.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chargerPartie();
            }
        });

        options.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "OPTIONS");
            }
        });

        quitter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuBox.add(Box.createVerticalGlue());
        menuBox.add(titleBlock);
        menuBox.add(Box.createVerticalStrut(35));
        menuBox.add(nouvellePartie);
        menuBox.add(Box.createVerticalStrut(12));
        menuBox.add(online);
        menuBox.add(Box.createVerticalStrut(12));
        menuBox.add(chargerPartie);
        menuBox.add(Box.createVerticalStrut(12));
        menuBox.add(options);
        menuBox.add(Box.createVerticalStrut(12));
        menuBox.add(quitter);
        menuBox.add(Box.createVerticalGlue());

        panel.add(menuBox, BorderLayout.EAST);
        return panel;
    }

    private JPanel creerMenuOptions() {
        BackgroundPanel panel = new BackgroundPanel(Config.CHEMIN_IMAGES + "/background_new.png");
        panel.setLayout(new GridBagLayout());

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        JLabel titre = new JLabel("OPTIONS");
        titre.setFont(new Font("Serif", Font.BOLD, 72));
        titre.setForeground(new Color(240, 225, 185));
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton modeTuto = textButton("Mode Tutoriel On/Off");
        JButton changerTheme = textButton("Changer thème");
        JButton retour = textButton("Retour");

        retour.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "MENU");
            }
        });

        box.add(titre);
        box.add(Box.createVerticalStrut(40));
        box.add(modeTuto);
        box.add(Box.createVerticalStrut(15));
        box.add(changerTheme);
        box.add(Box.createVerticalStrut(30));
        box.add(retour);

        panel.add(box);
        return panel;
    }

    private JButton imageButton(String normalName, String hoverName) {
        JButton button = new JButton();

        resizeImageButton(button, normalName, hoverName, getWidth(), getHeight());

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorder(null);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JButton textButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 22));
        b.setPreferredSize(new Dimension(320, 60));
        b.setMaximumSize(new Dimension(320, 60));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        return b;
    }

    private ImageIcon scaledIcon(String path, int w, int h) {
        Image img = new ImageIcon(path).getImage();
        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0, w, h, null);
        g2.dispose();

        return new ImageIcon(scaled);
    }

    private void ouvrirMenuMultijoueur() {
        String[] choix = {"Host", "join"};

        int rep = JOptionPane.showOptionDialog(
                this,
                "Choisissez le mode réseau",
                "Multijoueur",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                choix,
                choix[0]
        );

        if (rep == 0) {
            new Thread(new Runnable() {
                public void run() {
                    ServerTablut.main(new String[0]);
                }
            }).start();

            Timer timer = new Timer(800, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    lancerClientReseau("localhost");
                }
            });
            timer.setRepeats(false);
            timer.start();
        }

        else if (rep == 1) {
            String ip = JOptionPane.showInputDialog(
                    this,
                    "Adresse IP / hostname du serveur :",
                    "localhost"
            );

            if (ip != null && !ip.trim().isEmpty()) {
                lancerClientReseau(ip.trim());
            }
        }
    }
    // Improved title block with subtitle, dark translucent background, and gold lines
    private JPanel buildTitleBlock() {
        JPanel block = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setColor(new Color(0, 0, 0, 140));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.setColor(new Color(212, 175, 90));
                g2.drawLine(20, 3, getWidth() - 20, 3);
                g2.drawLine(20, getHeight() - 4, getWidth() - 20, getHeight() - 4);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setBorder(BorderFactory.createEmptyBorder(14, 32, 14, 32));
        block.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("JEU DE STRATÉGIE NORDIQUE");
        subtitle.setFont(new Font("Serif", Font.PLAIN, 16));
        subtitle.setForeground(new Color(212, 175, 90));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("TABLUT");
        title.setFont(new Font("Serif", Font.BOLD, 78));
        title.setForeground(new Color(240, 225, 185));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        block.add(subtitle);
        block.add(title);

        return block;
    }

    private void lancerClientReseau(String ip) {
        try {
            dispose();
            new FenetreJeuReseau(new Jeu(), ip);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erreur réseau : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void chargerPartie() {
        File dossier = new File(CHEMIN_SAUVEGARDES);
        File[] fichiers = dossier.listFiles();

        if (fichiers == null || fichiers.length == 0) {
            JOptionPane.showMessageDialog(this, "Aucune sauvegarde trouvée.");
            return;
        }

        String[] noms = new String[fichiers.length];

        for (int i = 0; i < fichiers.length; i++) {
            noms[i] = fichiers[i].getName();
        }

        String choix = (String) JOptionPane.showInputDialog(
                this,
                "Choisissez une sauvegarde :",
                "Charger Partie",
                JOptionPane.PLAIN_MESSAGE,
                null,
                noms,
                noms[0]
        );

        if (choix != null) {
            dispose();
            new FenetreJeu(new Jeu(choix));
        }
    }

    private static class BackgroundPanel extends JPanel {
        private Image background;

        public BackgroundPanel(String path) {
            background = new ImageIcon(path).getImage();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

            Graphics2D g2 = (Graphics2D) g;
            GradientPaint shade = new GradientPaint(
                    getWidth() * 0.45f, 0,
                    new Color(0, 0, 0, 0),
                    getWidth(), 0,
                    new Color(0, 0, 0, 150)
            );

            g2.setPaint(shade);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    private void resizeTitleBlock(JPanel titleBlock, int cardWidth, int cardHeight) {
        int titleW = (int) (0.32 * cardWidth);
        int titleH = (int) (0.16 * cardHeight);

        Dimension size = new Dimension(titleW, titleH);

        titleBlock.setPreferredSize(size);
        titleBlock.setMinimumSize(size);
        titleBlock.setMaximumSize(size);

        int paddingY = (int) (0.015 * cardHeight);
        int paddingX = (int) (0.015 * cardWidth);

        titleBlock.setBorder(BorderFactory.createEmptyBorder(
                paddingY,
                paddingX,
                paddingY,
                paddingX
        ));

        for (Component c : titleBlock.getComponents()) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;

                if (label.getText().equals("TABLUT")) {
                    int fontSize = Math.max(32, (int) (0.085 * cardHeight));
                    label.setFont(new Font("Serif", Font.BOLD, fontSize));
                } else {
                    int fontSize = Math.max(10, (int) (0.018 * cardHeight));
                    label.setFont(new Font("Serif", Font.PLAIN, fontSize));
                }
            }
        }

        titleBlock.revalidate();
        titleBlock.repaint();
    }

    private void resizeImageButton(
            JButton button,
            String normalName,
            String hoverName,
            int cardWidth,
            int cardHeight
    ) {
        int buttonW = (int) (0.32 * cardWidth);
        int buttonH = (int) (0.13 * cardHeight);

        Dimension size = new Dimension(buttonW, buttonH);

        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);

        String normalPath = Config.CHEMIN_IMAGES + "/" + normalName;
        String hoverPath = Config.CHEMIN_IMAGES + "/" + hoverName;

        button.setIcon(scaledIcon(normalPath, buttonW, buttonH));
        button.setRolloverIcon(scaledIcon(hoverPath, buttonW, buttonH));

        button.revalidate();
        button.repaint();
    }
    private void resizeMenuLayout(
            JPanel panel,
            JPanel menuBox,
            JPanel titleBlock,
            JButton nouvellePartie,
            JButton online,
            JButton chargerPartie,
            JButton options,
            JButton quitter
    ) {
        int w = panel.getWidth();
        int h = panel.getHeight();

        if (w <= 0 || h <= 0) return;

        int rightMargin = (int) (w * 0.08);
        int leftMargin = (int) (w * 0.02);
        int topBottomMargin = (int) (h * 0.05);

        menuBox.setBorder(BorderFactory.createEmptyBorder(
                topBottomMargin,
                leftMargin,
                topBottomMargin,
                rightMargin
        ));

        resizeImageButton(nouvellePartie, "btn_new_game.png", "btn_new_game_hover.png", w, h);
        resizeImageButton(online, "btn_online.png", "btn_online_hover.png", w, h);
        resizeImageButton(chargerPartie, "btn_load.png", "btn_load_hover.png", w, h);
        resizeImageButton(options, "btn_options.png", "btn_options_hover.png", w, h);
        resizeImageButton(quitter, "btn_quit.png", "btn_quit_hover.png", w, h);

        resizeTitleBlock(titleBlock, w, h);

        menuBox.revalidate();
        menuBox.repaint();
    }



}