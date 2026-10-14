package tablut.tests;

import tablut.model.Jeu;
import tablut.model.Point;
import tablut.model.TableauBit;

public class TestModel {
    public static void main(String[] args) {
        Jeu jeu = new Jeu();

        if (jeu.getJoueur() != 1) {
            throw new RuntimeException("Erreur: joueur initial devrait être 1");
        }

        if (jeu.getGrille().length != 9) {
            throw new RuntimeException("Erreur: grille devrait être 9x9");
        }

        TableauBit tab = jeu.transformerGrilleEnLong();
        jeu.transformeLongEnGrille(tab);

        if (jeu.getGrille().length != 9) {
            throw new RuntimeException("Erreur après transformation TableauBit");
        }

        Point a = new Point(0, 3);
        Point b = new Point(1, 3);

        if (!jeu.CoupValide(a, b)) throw new RuntimeException("Expected legal black opening move");
        if (jeu.CoupValide(a, new Point(1, 2))) throw new RuntimeException("Diagonal move accepted");
        if (jeu.deplacer(a, b) == null || jeu.getGrille()[1][3] != 1 || jeu.getJoueur() != 2) {
            throw new RuntimeException("Opening move did not update board and active player");
        }

        System.out.println("TestModel OK");
    }
}
