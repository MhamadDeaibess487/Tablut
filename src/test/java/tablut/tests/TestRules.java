package tablut.tests;

import tablut.model.*;
import static tablut.tests.TestSupport.*;

public class TestRules {
    public static void main(String[] args) {
        initialAndInvalidMoves();
        throneAndCaptures();
        kingWinAndHistory();
        repetition();
        System.out.println("TestRules OK (movement, captures, king, throne, history, repetition)");
    }

    private static void initialAndInvalidMoves() {
        Jeu game = new Jeu();
        check(game.getPieces(1).size() == 16 && game.getPieces(2).size() == 9, "Initial army sizes");
        TableauBit initial = game.transformerGrilleEnLong();
        Point[][] invalid = {
            {new Point(0,3), new Point(-1,3)}, {new Point(-1,3), new Point(1,3)},
            {new Point(0,3), new Point(9,3)}, {new Point(1,1), new Point(1,2)},
            {new Point(0,3), new Point(1,2)}, {new Point(0,3), new Point(0,4)},
            {new Point(0,3), new Point(0,3)}, {new Point(2,4), new Point(2,3)},
            {null, new Point(1,3)}, {new Point(0,3), null}
        };
        for (Point[] move : invalid) {
            check(game.deplacer(move[0], move[1]) == null, "Invalid move accepted");
            check(initial.compareTo(game.transformerGrilleEnLong()) == 0 && game.getUndo().isEmpty(), "Rejected move changed state");
        }
        check(game.listeCasesValides(1,1).isEmpty(), "Empty square has moves");
        game.terminerPartie();
        check(game.deplacer(new Point(0,3), new Point(1,3)) == null, "Finished game accepts moves");
    }

    private static void throneAndCaptures() {
        Jeu throne = board(2, new int[]{4,4,3}, new int[]{0,3,1});
        move(throne,4,4,4,5);
        throne.setJouerActif(2);
        check(!throne.CoupValide(new Point(4,5),new Point(4,4)), "King returned to throne");
        check(!throne.CoupValide(new Point(4,5),new Point(4,3)), "King crossed throne");

        Jeu normal = board(1, new int[]{2,0,1}, new int[]{2,3,1}, new int[]{2,2,2}, new int[]{4,4,3});
        move(normal,2,0,2,1);
        check(normal.getGrille()[2][2] == 0 && normal.getPieces(2).size() == 1, "Defender not captured");
        Jeu byThrone = board(1, new int[]{4,0,1},new int[]{4,3,2},new int[]{2,2,3});
        move(byThrone,4,0,4,2);
        check(byThrone.getGrille()[4][3] == 0, "Throne sandwich failed");

        Jeu adjacent = board(1,new int[]{4,3,3},new int[]{3,3,1},new int[]{5,3,1},new int[]{4,0,1});
        move(adjacent,4,0,4,2);
        check(adjacent.getFini() && adjacent.getResultatPartie() == BaseJeu.RESULTAT_NOIRS, "King capture adjacent to throne failed");
        Jeu outside = board(1,new int[]{2,2,3},new int[]{2,1,1},new int[]{2,5,1});
        move(outside,2,5,2,3);
        check(outside.getFini() && outside.getGrille()[2][2] == 0, "King sandwich outside central 3x3 failed");

        Jeu central = board(1,new int[]{3,3,3},new int[]{3,2,1},new int[]{3,6,1});
        move(central,3,6,3,4);
        check(!central.getFini(), "King in central 3x3 must need four hostile neighbours");
    }

    private static void kingWinAndHistory() {
        Jeu capture = kingCapture();
        TableauBit before = capture.transformerGrilleEnLong();
        move(capture,4,7,4,5);
        check(capture.getFini() && capture.getResultatPartie() == BaseJeu.RESULTAT_NOIRS, "Capture result missing");
        check(capture.getGrille()[4][4] == 4 && capture.getPieces(2).isEmpty(), "Captured king remains in model");
        check(new Jeu(capture).getResultatPartie() == BaseJeu.RESULTAT_NOIRS, "Copy loses result");
        check(capture.annuler() && !capture.getFini() && before.compareTo(capture.transformerGrilleEnLong()) == 0, "Undo capture failed");
        check(capture.refaire() && capture.getFini() && capture.getResultatPartie() == BaseJeu.RESULTAT_NOIRS, "Redo loses terminal result");
        Jeu escape = board(2,new int[]{1,2,3},new int[]{5,5,1});
        move(escape,1,2,0,2);
        check(escape.getResultatPartie() == BaseJeu.RESULTAT_BLANCS, "King escape failed");
        check(!escape.draw() && escape.getResultatPartie() == BaseJeu.RESULTAT_BLANCS, "Draw overrides win");
        escape.nouvellePartie();
        check(!escape.getFini() && escape.getUndo().isEmpty() && escape.getJoueur() == 1 && !escape.isLoaded, "New game retains old state");
    }

    private static void repetition() {
        Jeu game = new Jeu();
        for (int cycle = 0; cycle < 2; cycle++) {
            move(game,0,3,1,3); move(game,2,4,2,3);
            move(game,1,3,0,3); move(game,2,3,2,4);
        }
        check(game.draw() && game.getResultatPartie() == BaseJeu.RESULTAT_NULLE, "Threefold board repetition not detected");
        check(game.annuler() && !game.getFini(), "Undo draw failed");
        check(game.refaire() && game.getFini() && game.getResultatPartie() == BaseJeu.RESULTAT_NULLE,
                "Redo did not restore repetition draw");
    }
}
