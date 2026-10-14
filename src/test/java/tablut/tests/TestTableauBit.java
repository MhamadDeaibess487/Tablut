package tablut.tests;

import tablut.model.Coup;
import tablut.model.Jeu;
import tablut.model.Point;
import tablut.model.TableauBit;

public class TestTableauBit {
    public static void main(String[] args) {
        testerRoundTripGrilleInitiale();
        testerRoundTripApresCoup();
        System.out.println("TestTableauBit OK");
    }

    private static void testerRoundTripGrilleInitiale() {
        Jeu jeu = new Jeu();
        int[][] avant = copierGrille(jeu.getGrille());

        TableauBit tab = jeu.transformerGrilleEnLong();
        jeu.transformeLongEnGrille(tab);

        verifierGrillesEgales(avant, jeu.getGrille(), "grille initiale");
    }

    private static void testerRoundTripApresCoup() {
        Jeu jeu = new Jeu();
        Coup coup = premierCoupLegal(jeu, 1);
        if (coup == null) {
            throw new RuntimeException("Aucun coup legal trouve pour le joueur noir");
        }

        if (jeu.deplacer(coup.a, coup.b) == null) {
            throw new RuntimeException("Le coup legal detecte n'a pas pu etre joue");
        }

        int[][] avant = copierGrille(jeu.getGrille());
        TableauBit tab = jeu.transformerGrilleEnLong();
        jeu.transformeLongEnGrille(tab);

        verifierGrillesEgales(avant, jeu.getGrille(), "grille apres coup");
    }

    static Coup premierCoupLegal(Jeu jeu, int joueur) {
        int[][] grille = jeu.getGrille();

        for (int l = 0; l < grille.length; l++) {
            for (int c = 0; c < grille[l].length; c++) {
                int piece = grille[l][c];
                if ((joueur == 1 && piece != 1) || (joueur == 2 && piece != 2 && piece != 3)) {
                    continue;
                }

                java.util.ArrayList<Point> coups = jeu.listeCasesValides(l, c);
                if (!coups.isEmpty()) {
                    return new Coup(new Point(l, c), coups.get(0), joueur);
                }
            }
        }

        return null;
    }

    private static int[][] copierGrille(int[][] grille) {
        int[][] copie = new int[grille.length][grille[0].length];
        for (int l = 0; l < grille.length; l++) {
            for (int c = 0; c < grille[l].length; c++) {
                copie[l][c] = grille[l][c];
            }
        }
        return copie;
    }

    private static void verifierGrillesEgales(int[][] attendue, int[][] obtenue, String contexte) {
        if (attendue.length != obtenue.length) {
            throw new RuntimeException("Nombre de lignes different apres roundtrip: " + contexte);
        }

        for (int l = 0; l < attendue.length; l++) {
            if (attendue[l].length != obtenue[l].length) {
                throw new RuntimeException("Nombre de colonnes different apres roundtrip: " + contexte);
            }
            for (int c = 0; c < attendue[l].length; c++) {
                if (attendue[l][c] != obtenue[l][c]) {
                    throw new RuntimeException(
                        "Case differente apres roundtrip " + contexte + " en (" + l + ", " + c + ")"
                    );
                }
            }
        }
    }
}
