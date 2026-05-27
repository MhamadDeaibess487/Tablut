import Model.Jeu;
import Vue.MenuGraphique;

import javax.swing.SwingUtilities;

public class Tablut {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Jeu jeu = new Jeu();
            new MenuGraphique(jeu);
        });
    }
}