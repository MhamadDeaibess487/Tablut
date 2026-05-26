package Vue;

import Controleur.Controle;
import Model.BaseJeu;
import Config.Config;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FenetreJeu extends JFrame {
    BaseJeu jeu;
    private JoueurActifPanel joueurActifPanel;
    private JPanel victoryButtonsPanel;
    private Timer victoryTimer;
    private GrilleGraphique grille;
    private Timer resizeTimer;

    public FenetreJeu(BaseJeu jeu) {
        setTitle("Tablut");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setMinimumSize(new Dimension(900, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        this.jeu = jeu;
        grille = new GrilleGraphique(jeu);
        Controle controle = new Controle(jeu, grille, this);
        GrilleButtons buttons = new GrilleButtons(grille, controle);

        joueurActifPanel = new JoueurActifPanel(
                Config.CHEMIN_IMAGES + "/nap_on.png",
                Config.CHEMIN_IMAGES + "/nap_on.png",
                Config.CHEMIN_IMAGES + "/julien.png",
                Config.CHEMIN_IMAGES + "/julien.png",
                controle);



// CENTER — grille + boutons autour
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        JPanel southBox = new JPanel();
        southBox.setLayout(new BoxLayout(southBox, BoxLayout.X_AXIS));
        southBox.setOpaque(false);
        southBox.add(Box.createHorizontalGlue());
        southBox.add(buttons.undo);
        southBox.add(Box.createHorizontalStrut(10));
        southBox.add(buttons.redo);
        southBox.add(Box.createHorizontalGlue());

        JPanel northBox = new JPanel();
        northBox.setLayout(new BoxLayout(northBox, BoxLayout.X_AXIS));
        northBox.setOpaque(false);
        northBox.add(Box.createHorizontalGlue());
        northBox.add(buttons.parametres);

        JPanel grilleWrapper = new JPanel();
        grilleWrapper.setLayout(new OverlayLayout(grilleWrapper));
        grilleWrapper.setOpaque(false);

        victoryButtonsPanel = createVictoryButtonsPanel();
        victoryButtonsPanel.setVisible(false);

        grille.setAlignmentX(0.5f);
        grille.setAlignmentY(0.5f);
        victoryButtonsPanel.setAlignmentX(0.5f);
        victoryButtonsPanel.setAlignmentY(0.78f);

        grilleWrapper.add(victoryButtonsPanel);
        grilleWrapper.add(grille);

        centerPanel.add(grilleWrapper, BorderLayout.CENTER);
        centerPanel.add(southBox, BorderLayout.SOUTH);
        centerPanel.add(northBox, BorderLayout.NORTH);

        add(joueurActifPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        getContentPane().setBackground(new Color(18, 22, 32));

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (resizeTimer != null && resizeTimer.isRunning()) {
                    resizeTimer.restart();
                    return;
                }

                resizeTimer = new Timer(80, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ev) {
                        int w = getWidth();
                        int h = getHeight();

                        int btnSize = (int) (Math.min(w, h) * 0.10);

                        ((RoundedButton) buttons.undo).updateSize(btnSize);
                        ((RoundedButton) buttons.redo).updateSize(btnSize);
                        ((RoundedButton) buttons.parametres).updateSize((int)(btnSize * 1.1));

                        joueurActifPanel.resizePanel(w, h);

                        revalidate();
                        repaint();
                    }
                });
                resizeTimer.setRepeats(false);
                resizeTimer.start();
            }
        });

        victoryTimer = new Timer(200, e -> {
            boolean fini = jeu.getFini();
            victoryButtonsPanel.setVisible(fini);
            victoryButtonsPanel.revalidate();
            victoryButtonsPanel.repaint();
        });
        victoryTimer.start();

        setVisible(true);
    }

    private JPanel createVictoryButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton nouvellePartie = new JButton("Nouvelle partie");
        JButton voirPlateau = new JButton("Voir plateau");
        JButton quitter = new JButton("Quitter");

        nouvellePartie.setFont(new Font("SansSerif", Font.BOLD, 22));
        voirPlateau.setFont(new Font("SansSerif", Font.BOLD, 22));
        quitter.setFont(new Font("SansSerif", Font.BOLD, 22));

        nouvellePartie.addActionListener(e -> {
            dispose();
            new FenetreJeu(new Model.Jeu());
        });

        voirPlateau.addActionListener(e -> {
            grille.cacherImageVictoire();
            victoryButtonsPanel.setVisible(false);
        });

        quitter.addActionListener(e -> {
            dispose();
            new MenuGraphique(new Model.Jeu());
        });

        panel.add(nouvellePartie);
        panel.add(voirPlateau);
        panel.add(quitter);

        return panel;
    }

    public void setJoueurActif(int joueur) {
        joueurActifPanel.setJoueurActif(joueur);
    }

    public void retourMenu() {
        dispose();
        new MenuGraphique(jeu);
    }
}
