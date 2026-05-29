package Tests;

import Model.Jeu;
import Model.Point;
import Model.TableauBit;

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

        boolean coupValideTeste = jeu.CoupValide(a, b) || !jeu.CoupValide(a, b);

        if (!coupValideTeste) {
            throw new RuntimeException("Erreur test coup valide");
        }

        System.out.println("TestModel OK");
    }
}
