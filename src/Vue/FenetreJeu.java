package Vue;

import Controleur.Controle;
import Model.BaseJeu;
import Config.Config;
import javax.swing.*;
import java.awt.*;

public class FenetreJeu extends JFrame {
    BaseJeu jeu;
    private JoueurActifPanel joueurActifPanel;

    public FenetreJeu(BaseJeu jeu) {
        setTitle("Tablut");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        this.jeu = jeu;
        GrilleGraphique grille = new GrilleGraphique(jeu);
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

        centerPanel.add(grille, BorderLayout.CENTER);
        centerPanel.add(southBox, BorderLayout.SOUTH);
        centerPanel.add(northBox, BorderLayout.NORTH);

        add(joueurActifPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        getContentPane().setBackground(new Color(18, 22, 32));


        setVisible(true);
    }

    public void setJoueurActif(int joueur) {
        joueurActifPanel.setJoueurActif(joueur);
    }

    public void retourMenu() {
        dispose();
        new MenuGraphique(jeu);
    }
}
