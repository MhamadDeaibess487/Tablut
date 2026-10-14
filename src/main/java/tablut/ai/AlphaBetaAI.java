package tablut.ai;

import tablut.model.*;
import java.util.*;

/** Shared minimax/alpha-beta traversal; concrete strategies retain their own evaluation. */
abstract class AlphaBetaAI extends IA {
    private final int profondeur;
    private final boolean victoireTerminaleDirecte;

    protected AlphaBetaAI(Jeu jeu, int couleur, int profondeur, boolean victoireTerminaleDirecte) {
        this.j = jeu;
        this.couleur = couleur;
        this.profondeur = profondeur;
        this.victoireTerminaleDirecte = victoireTerminaleDirecte;
    }

    protected abstract int heuristique(Jeu jeu);

    protected Point posRoi(Jeu jeu) {
        for (int l = 0; l < 9; l++)
            for (int c = 0; c < 9; c++)
                if (jeu.getGrille()[l][c] == 3)
                    return new Point(l, c);
        return null;
    }

    protected boolean inBounds(int l, int c) {
        return l >= 0 && l <= 8 && c >= 0 && c <= 8;
    }

    protected boolean estHostile(int l, int c) {
        return (l == 4 && c == 4)
            || (l == 0 && c == 0) || (l == 0 && c == 8)
            || (l == 8 && c == 0) || (l == 8 && c == 8);
    }

    protected boolean estMonCoup(Jeu jeu, Coup c) {
        int src = jeu.getGrille()[c.a.l][c.a.c];
        return (couleur == 1 && src == 1)
            || (couleur == 2 && (src == 2 || src == 3));
    }

    protected Jeu copieAvecJoueur(Jeu src, int joueur) {
        Jeu copie = new Jeu(src);
        copie.setJouerActif(joueur);
        return copie;
    }

    protected int capturesEstimees(Point src, Point dst, Jeu jeu) {
        int piece = jeu.getGrille()[src.l][src.c];
        int captures = 0;
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};

        for (int[] d : dirs) {
            int ml = dst.l + d[0], mc = dst.c + d[1]; // victime potentielle
            int bl = dst.l + 2*d[0], bc = dst.c + 2*d[1]; // derrière

            if (!inBounds(ml, mc)) continue;
            int milieu = jeu.getGrille()[ml][mc];
            if (milieu == 0 || milieu == 4) continue;

            boolean ennemi = (piece == 1) ? (milieu == 2 || milieu == 3)
                                           : (milieu == 1);
            if (!ennemi) continue;

            boolean derriereFriendly = inBounds(bl, bc)
                && ((piece == 1 && jeu.getGrille()[bl][bc] == 1)
                 || (piece != 1 && (jeu.getGrille()[bl][bc] == 2 || jeu.getGrille()[bl][bc] == 3)));
            boolean derriereHostile = !inBounds(bl, bc) || estHostile(bl, bc);

            if (derriereFriendly || derriereHostile) captures++;
        }
        return captures;
    }

    protected List<Coup> trierCoups(List<Coup> coups, Jeu jeu, boolean estmax) {
        Point king = posRoi(jeu);
        List<Coup> liste = new ArrayList<>(coups);
        liste.sort((a, b) ->
            priorite(b, jeu, estmax, king) - priorite(a, jeu, estmax, king)
        );
        return liste;
    }

    protected int priorite(Coup c, Jeu jeu, boolean estmax, Point king) {
        Point src = c.a, dst = c.b;
        int piece = jeu.getGrille()[src.l][src.c];

        // Blanc : roi vers le bord (victoire potentielle)
        if (!estmax && piece == 3) {
            int dist = Math.min(Math.min(dst.l, 8-dst.l), Math.min(dst.c, 8-dst.c));
            if (dist == 0) return 1000;          // bord = victoire immédiate
            return 100 + (4 - dist) * 20;        // plus proche = plus urgent
        }

        // Les deux camps : captures en priorité
        int cap = capturesEstimees(src, dst, jeu);
        if (cap > 0) return 500 + cap * 100;

        // Noir : rapprocher du roi
        if (estmax && king != null) {
            int dist = Math.abs(dst.l - king.l) + Math.abs(dst.c - king.c);
            return Math.max(0, 20 - dist * 2);
        }

        return 0;
    }

    public int alphabeta(Jeu jeu, int prof, int alpha, int beta, boolean estmax) {
        if (Thread.currentThread().isInterrupted()) throw new java.util.concurrent.CancellationException();
        if (prof == 0 || jeu.getFini())
            return heuristique(jeu);

        // Joueur_actif est garanti correct ici grâce à copieAvecJoueur()
        List<Coup> coups = trierCoups(
            jeu.getLegalMoves(jeu.getJoueur()), jeu, estmax
        );

        if (coups.isEmpty()) return heuristique(jeu);

        if (estmax) {
            int best = Integer.MIN_VALUE;
            for (Coup c : coups) {

                Jeu copie = copieAvecJoueur(jeu, 1); // Noir joue
                copie.deplacer(c.a, c.b);
                if (victoireTerminaleDirecte && copie.getFini()) return 100000;
                best  = Math.max(best, alphabeta(copie, prof-1, alpha, beta, false));
                alpha = Math.max(alpha, best);
                if (beta <= alpha) break;             //  coupure β
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (Coup c : coups) {
                Jeu copie = copieAvecJoueur(jeu, 2); // Blanc joue
                copie.deplacer(c.a, c.b);
                if (victoireTerminaleDirecte && copie.getFini()) return -100000;
                best = Math.min(best, alphabeta(copie, prof-1, alpha, beta, true));
                beta = Math.min(beta, best);
                if (beta <= alpha) break;             //  coupure α
            }
            return best;
        }
    }

    protected Coup calculCoup() {
        System.out.println(getClass().getSimpleName() + " réfléchit...");
        boolean noir = (couleur == 1);
        int bestScore = noir ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        List<Coup> exAequo = new ArrayList<>();

        // Génère les coups de NOTRE couleur (indépendant de Joueur_actif)
        List<Coup> tousLesCoups = new ArrayList<>(j.getLegalMoves(couleur));
        tousLesCoups.removeIf(c -> !estMonCoup(j, c));
        if (tousLesCoups.isEmpty()) return null;

        for (Coup c : tousLesCoups) {

            Jeu copie = copieAvecJoueur(j, couleur);
            copie.deplacer(c.a, c.b);
            // Ici copie.getJoueur() == adversaire

            // Victoire immédiate : jouer sans hésiter
            Point roiApres = posRoi(copie);
            if (couleur == 2 && roiApres != null
                && (roiApres.l==0||roiApres.l==8||roiApres.c==0||roiApres.c==8))
                return c;
            if (couleur == 1 && copie.getFini())
                return c;

            // L'adversaire commence dans l'arbre → !noir
            int score = alphabeta(
                copie, profondeur,
                Integer.MIN_VALUE, Integer.MAX_VALUE,
                !noir
            );

            if (noir) {
                if (score > bestScore) { bestScore = score; exAequo.clear(); exAequo.add(c); }
                else if (score == bestScore) exAequo.add(c);
            } else {
                if (score < bestScore) { bestScore = score; exAequo.clear(); exAequo.add(c); }
                else if (score == bestScore) exAequo.add(c);
            }
        }

        if (exAequo.isEmpty()) return tousLesCoups.get(0);
        return exAequo.get((int)(Math.random() * exAequo.size()));
    }
}
