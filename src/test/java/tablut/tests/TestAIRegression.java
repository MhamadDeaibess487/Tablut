package tablut.tests;

import tablut.ai.IA;
import tablut.model.*;
import java.lang.reflect.Method;
import static tablut.tests.TestSupport.*;

public class TestAIRegression {
    public static void main(String[] args) throws Exception {
        for (int level = 0; level < 3; level++) {
            Jeu game = kingCapture();
            IA strategy = IA.creer(game, level, 1);
            TableauBit before = game.transformerGrilleEnLong();
            Coup choice = strategy.choisirCoup();
            check(choice != null && game.CoupValide(choice.a,choice.b), "AI returned invalid move");
            check(before.compareTo(game.transformerGrilleEnLong()) == 0 && game.getUndo().isEmpty(), "Search mutated game");
            strategy.joue();
            check(game.getFini() && game.getResultatPartie() == BaseJeu.RESULTAT_NOIRS, "AI missed immediate king capture");
            check(game.getUndo().size() == 1, "Search polluted history");
            strategy.joue();
            check(game.getUndo().size() == 1, "AI moved after game over");

            Jeu escape = board(2,new int[]{1,2,3},new int[]{5,5,1});
            IA.creer(escape,level,2).joue();
            check(escape.getFini() && escape.getResultatPartie() == BaseJeu.RESULTAT_BLANCS, "AI missed immediate king escape");
        }
        compareAlphaBetaWithMinimax();
        try { IA.creer(new Jeu(),42,1); throw new AssertionError("Invalid level accepted"); }
        catch (IllegalArgumentException expected) { }
        System.out.println("TestAIRegression OK (wins, search isolation, alpha-beta vs exhaustive minimax)");
    }

    private static void compareAlphaBetaWithMinimax() throws Exception {
        // Both camps, two plies. No stochastic easy evaluation is used here.
        for (int player : new int[]{1,2}) {
            Jeu game = board(player,new int[]{4,4,3},new int[]{2,2,1},new int[]{6,6,1},new int[]{3,4,2});
            IA strategy = IA.creer(game,1,player);
            Method search = strategy.getClass().getSuperclass().getDeclaredMethod("alphabeta",Jeu.class,int.class,int.class,int.class,boolean.class);
            Method evaluate = strategy.getClass().getDeclaredMethod("heuristique",Jeu.class);
            search.setAccessible(true);
            evaluate.setAccessible(true);
            int actual = (int)search.invoke(strategy,game,2,Integer.MIN_VALUE,Integer.MAX_VALUE,player==1);
            int expected = minimax(strategy,evaluate,game,2);
            check(actual==expected,"Alpha-beta score differs from exhaustive minimax");
        }
    }

    private static int minimax(IA strategy,Method evaluate,Jeu game,int depth) throws Exception {
        if (depth==0 || game.getFini() || game.getLegalMoves(game.getJoueur()).isEmpty()) return (int)evaluate.invoke(strategy,game);
        boolean max = game.getJoueur()==1;
        int best = max ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Coup move : game.getLegalMoves(game.getJoueur())) {
            Jeu child = new Jeu(game);
            check(child.deplacer(move.a,move.b)!=null,"Generated illegal move");
            int score = minimax(strategy,evaluate,child,depth-1);
            best = max ? Math.max(best,score) : Math.min(best,score);
        }
        return best;
    }
}
