package Vue;

import Model.BaseJeu;
import Controleur.Controle;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;

public class Test_grille {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BaseJeu jeu = new BaseJeu();

            GrilleGraphique grille = new GrilleGraphique(jeu);
            Controle controle = new Controle(jeu, grille);
            GrilleButtons buttons = new GrilleButtons(jeu, grille, controle);

            JFrame frame = new JFrame("Test GrilleGraphique");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 700);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            frame.add(grille, BorderLayout.CENTER);
            frame.add(buttons, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }
}