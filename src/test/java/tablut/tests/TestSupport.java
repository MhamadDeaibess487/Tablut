package tablut.tests;

import tablut.model.*;
import java.util.Arrays;

final class TestSupport {
    private TestSupport() {}

    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    static Jeu board(int player, int[]... pieces) {
        Jeu game = new Jeu();
        for (int[] row : game.getGrille()) Arrays.fill(row, 0);
        for (int[] piece : pieces) game.getGrille()[piece[0]][piece[1]] = piece[2];
        game.transformeLongEnGrille(game.transformerGrilleEnLong());
        game.setJouerActif(player);
        return game;
    }

    static Jeu kingCapture() {
        return board(1, new int[]{4,4,3}, new int[]{3,4,1}, new int[]{5,4,1},
                new int[]{4,3,1}, new int[]{4,7,1});
    }

    static void move(Jeu game, int l1, int c1, int l2, int c2) {
        check(game.deplacer(new Point(l1, c1), new Point(l2, c2)) != null, "Expected legal move");
    }
}
