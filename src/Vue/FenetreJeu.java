package Vue;

import Controleur.Controle;
import Model.Jeu;
import Config.Config;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

/* Faire la transit propre entre le cancel du choix pseudo vers le menuPrincipale*/
public class FenetreJeu extends JFrame {
    Jeu jeu;
    private JoueurActifPanel joueurActifPanel;
    private JPanel victoryButtonsPanel;
    private Timer victoryTimer;
    private GrilleGraphique grille;

    private Timer resizeTimer;
    private JButton victoryNouvellePartie;
    private JButton victoryVoirPlateau;
    private JButton victoryQuitter;
    private JButton victoryVoirImage;
    private JPanel victoryButtonsBox;

    public FenetreJeu(Jeu jeu) {
        setTitle("Tablut");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setMinimumSize(new Dimension(900, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        this.jeu = jeu;
        String[] pseudo = new String[2];
        if(!jeu.isLoaded){
            pseudo = openMenuPseudo();
            if(pseudo == null){
                retourMenu();
                return;
            }
        }
        grille = new GrilleGraphique(jeu);
        Controle controle = new Controle(jeu, grille, this);
        if(!jeu.isLoaded){
            controle.setPseudo(1,pseudo[0]);
            controle.setPseudo(2,pseudo[1]);
        }
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
                        try{
                            ((RoundedButton) buttons.undo).updateSize(btnSize);
                            ((RoundedButton) buttons.redo).updateSize(btnSize);
                            ((RoundedButton) buttons.parametres).updateSize((int)(btnSize * 1.1));
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        joueurActifPanel.resizePanel(w, h);

                        revalidate();
                        repaint();
                    }
                });
                resizeTimer.setRepeats(false);
                resizeTimer.start();

                int w = getWidth();
                int h = getHeight();

                int btnSize = (int) (Math.min(w, h) * 0.10);

                try{
                    ((RoundedButton) buttons.undo).updateSize(btnSize);
                    ((RoundedButton) buttons.redo).updateSize(btnSize);
                    ((RoundedButton) buttons.parametres).updateSize((int)(btnSize * 1.1));
                }
                catch (IOException p) {
                    p.printStackTrace();
                }


                joueurActifPanel.resizePanel(w, h);
                resizeVictoryButtons();

                revalidate();
                repaint();

            }
        });

        victoryTimer = new Timer(200, e -> {
            boolean fini = jeu.getFini();
            victoryButtonsPanel.setVisible(fini);

            if (fini) {
                resizeVictoryButtons();
            }

            victoryButtonsPanel.revalidate();
            victoryButtonsPanel.repaint();
        });
        victoryTimer.start();

        setVisible(true);
    }
    
    private String[] openMenuPseudo(){
        String[] ps = new String[2];
        for(int i =1;i<=2;i++){
            String tag = JOptionPane.showInputDialog("Pseudo Joueur "+i);
            if(tag == null)
                return null;
            if(tag.length() == 0)
                tag = "Joueur " + i;
            ps[i-1] = tag;
        }
        return ps;
    }

    // Removed image-based button helpers
    private JPanel createVictoryButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(null);

        victoryButtonsBox = new JPanel();
        victoryButtonsBox.setOpaque(false);
        victoryButtonsBox.setLayout(new BoxLayout(victoryButtonsBox, BoxLayout.X_AXIS));

        victoryNouvellePartie = victoryButton("Nouvelle Partie");
        victoryVoirPlateau = victoryButton("Voir Plateau");
        victoryQuitter = victoryButton("Quitter");
        victoryVoirImage = victoryButton("Voir Image");

        victoryVoirImage.setVisible(false);

        victoryNouvellePartie.addActionListener(e -> {
            dispose();
            new FenetreJeu(new Jeu());
        });

        victoryVoirPlateau.addActionListener(e -> {
            grille.cacherImageVictoire();
            victoryVoirPlateau.setVisible(false);
            victoryVoirImage.setVisible(true);
            resizeVictoryButtons();
            repaint();
        });

        victoryVoirImage.addActionListener(e -> {
            grille.afficherImageVictoire();
            victoryVoirImage.setVisible(false);
            victoryVoirPlateau.setVisible(true);
            resizeVictoryButtons();
            repaint();
        });

        victoryQuitter.addActionListener(e -> {
            dispose();
            new MenuGraphique(new Model.Jeu());
        });

        victoryButtonsBox.add(victoryNouvellePartie);
        victoryButtonsBox.add(Box.createHorizontalStrut(15));
        victoryButtonsBox.add(victoryVoirPlateau);
        victoryButtonsBox.add(Box.createHorizontalStrut(15));
        victoryButtonsBox.add(victoryVoirImage);
        victoryButtonsBox.add(Box.createHorizontalStrut(15));
        victoryButtonsBox.add(victoryQuitter);

        panel.add(victoryButtonsBox);
        return panel;
    }

    private JButton victoryButton(String text) {
        JButton b = new JButton(text);

        b.setFont(new Font("Serif", Font.BOLD, 22));
        b.setForeground(new Color(240, 225, 185));
        b.setBackground(new Color(18, 22, 32));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorder(BorderFactory.createLineBorder(new Color(200, 160, 80), 2));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return b;
    }

    private void resizeVictoryButtons() {
        if (victoryButtonsBox == null || victoryButtonsPanel == null) {
            return;
        }

        int panelW = grille.getWidth();
        int panelH = grille.getHeight();

        if (panelW <= 0 || panelH <= 0) {
            return;
        }

        int btnW = Math.max(170, (int) (panelW * 0.17));
        int btnH = Math.max(45, (int) (panelH * 0.055));
        int gap = Math.max(10, (int) (panelW * 0.012));

        Dimension size = new Dimension(btnW, btnH);

        JButton[] buttons = {
            victoryNouvellePartie,
            victoryVoirPlateau,
            victoryVoirImage,
            victoryQuitter
        };

        for (JButton b : buttons) {
            b.setPreferredSize(size);
            b.setMinimumSize(size);
            b.setMaximumSize(size);
            b.setFont(new Font("Serif", Font.BOLD, Math.max(16, btnH / 2)));
        }

        int visibleButtonCount = victoryVoirImage.isVisible() ? 3 : 3;
        int totalW = visibleButtonCount * btnW + (visibleButtonCount - 1) * gap;

        int x = (panelW - totalW) / 2;
        int y = 0;

        victoryButtonsBox.setBounds(x, y, totalW, btnH);

        victoryButtonsBox.revalidate();
        victoryButtonsBox.repaint();
    }
    public void setJoueurActif(int joueur) {
        joueurActifPanel.setJoueurActif(joueur);
    }

    public void retourMenu() {
        dispose();
        new MenuGraphique(jeu);
    }
}
