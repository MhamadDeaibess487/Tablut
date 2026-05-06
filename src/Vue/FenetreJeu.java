package Vue;

import Controleur.Controle;
import Model.BaseJeu;
import Model.Jeu;

import javax.swing.*;
import java.awt.*;

public class FenetreJeu extends JFrame {

    public FenetreJeu() {
        setTitle("Tablut");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        BaseJeu jeu = new Jeu();

        GrilleGraphique grille = new GrilleGraphique(jeu);
        Controle controle = new Controle(jeu, grille);
        GrilleButtons buttons = new GrilleButtons(jeu, grille, controle);

        add(grille, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        setVisible(true);
    }
}