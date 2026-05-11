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

        joueurActifPanel = new JoueurActifPanel(
            Config.CHEMIN_IMAGES + "/nap_on.png",
            Config.CHEMIN_IMAGES + "/nap_on.png",
            Config.CHEMIN_IMAGES + "/julien.png",
            Config.CHEMIN_IMAGES + "/julien.png"
        );

        GrilleGraphique grille = new GrilleGraphique(jeu);

        Controle controle = new Controle(jeu, grille, this);
        GrilleButtons buttons = new GrilleButtons(jeu, grille, controle);
        //joueurActifPanel.setAiControlsPanel(buttons.getAiControlsPanel());

        add(grille, BorderLayout.CENTER);
        add(joueurActifPanel, BorderLayout.WEST);
        add(buttons, BorderLayout.EAST);
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
