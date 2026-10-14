package tablut;

import tablut.model.Jeu;
import tablut.ui.MenuGraphique;

import javax.swing.SwingUtilities;

public class Tablut {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Jeu jeu = new Jeu();
            new MenuGraphique(jeu);
        });
    }
}
