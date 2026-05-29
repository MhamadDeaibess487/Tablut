package Tests;

import Model.IA;
import Model.Jeu;
import Model.TableauBit;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

final class TestAIUtils {
    private static final int MAX_COUPS = 500;

    private TestAIUtils() {}

    static void executerTest(String nomTest, int niveau) {
        executerTest(nomTest, niveau, new Jeu(), MAX_COUPS, true);
    }

    static void executerTest(String nomTest, int niveau, Jeu jeuDepart, int maxCoups) {
        executerTest(nomTest, niveau, jeuDepart, maxCoups, true);
    }

    static void executerTest(String nomTest, int niveau, Jeu jeuDepart, int maxCoups, boolean requireCompletion) {
        testerPremierCoup(niveau);
        jouerPartieComplete(jeuDepart, niveau, maxCoups, requireCompletion);
        System.out.println(nomTest + " OK");
    }

    private static void testerPremierCoup(int niveau) {
        Jeu jeu = new Jeu();
        IA noir = IA.creer(jeu, niveau, 1);

        TableauBit avant = jeu.transformerGrilleEnLong();
        int joueurAvant = jeu.getJoueur();

        executerSilencieusement(noir::joue);

        TableauBit apres = jeu.transformerGrilleEnLong();
        if (avant.compareTo(apres) == 0) {
            throw new RuntimeException("L'IA n'a pas joue de coup au premier tour");
        }

        if (!jeu.getFini() && jeu.getJoueur() == joueurAvant) {
            throw new RuntimeException("Le joueur actif n'a pas change apres le coup de l'IA");
        }

        if (jeu.getUndo().isEmpty()) {
            throw new RuntimeException("Le coup joue par l'IA n'a pas ete sauvegarde pour l'undo");
        }
    }

    private static void jouerPartieComplete(Jeu jeu, int niveau, int maxCoups, boolean requireCompletion) {
        IA noir = IA.creer(jeu, niveau, 1);
        IA blanc = IA.creer(jeu, niveau, 2);
        boolean partieTerminee = false;
        boolean progressionObservee = false;

        for (int i = 0; i < maxCoups; i++) {
            if (jeu.PlusDeCoup()) {
                jeu.terminerPartie();
                partieTerminee = true;
                break;
            }

            TableauBit avant = jeu.transformerGrilleEnLong();
            int joueurAvant = jeu.getJoueur();

            IA joueurCourant = joueurAvant == 1 ? noir : blanc;
            executerSilencieusement(joueurCourant::joue);

            TableauBit apres = jeu.transformerGrilleEnLong();
            boolean plateauChange = avant.compareTo(apres) != 0;
            progressionObservee |= plateauChange;

            if (!plateauChange && !jeu.getFini()) {
                if (jeu.PlusDeCoup()) {
                    jeu.terminerPartie();
                    partieTerminee = true;
                    break;
                }
                throw new RuntimeException("L'IA n'a pas fait progresser la partie");
            }

            if (!jeu.getFini() && jeu.PlusDeCoup()) {
                jeu.terminerPartie();
                partieTerminee = true;
            }

            if (!jeu.getFini() && jeu.draw()) {
                partieTerminee = true;
                break;
            }

            if (jeu.getFini()) {
                partieTerminee = true;
                break;
            }
        }

        if (!jeu.getFini() && !partieTerminee && requireCompletion) {
            throw new RuntimeException("La partie IA vs IA n'a pas termine en " + maxCoups + " coups");
        }

        if (!progressionObservee) {
            throw new RuntimeException("La simulation IA n'a montre aucune progression");
        }
    }

    private static void executerSilencieusement(Runnable action) {
        PrintStream sortieOriginale = System.out;
        ByteArrayOutputStream tampon = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(tampon));
            action.run();
        } finally {
            System.setOut(sortieOriginale);
        }
    }
}
