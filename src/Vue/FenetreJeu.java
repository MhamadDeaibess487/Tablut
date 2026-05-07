package Vue;

import Controleur.Controle;
import Model.BaseJeu;
import Model.Jeu;

import javax.swing.*;
import java.awt.*;

public class FenetreJeu extends JFrame {
    BaseJeu jeu;
    public FenetreJeu(BaseJeu jeu) {

        setTitle("Tablut");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        this.jeu= jeu;

        GrilleGraphique grille = new GrilleGraphique(jeu);

        Controle controle = new Controle(jeu, grille,this);
        GrilleButtons buttons = new GrilleButtons(jeu, grille, controle);

        add(grille, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        setVisible(true);
    }
    public void retourMenu() {
        dispose();
        new MenuGraphique(jeu);
    }
}