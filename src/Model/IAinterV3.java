package Model;

import java.util.*;

class IAinterV3 extends IA {

    public IAinterV3(Jeu j, int couleur) {
        this.j = j;
        this.couleur = couleur;
    }



    private Point posRoi(Jeu jeu) {
        for (int l = 0; l < 9; l++)
            for (int c = 0; c < 9; c++)
                if (jeu.grille[l][c] == 3)
                    return new Point(l, c);
        return null;
    }


    private List<Coup> trierCoups(List<Coup> coups, Jeu jeu, boolean estmax) {
        List<Coup> liste = new ArrayList<>(coups);
        Point king = posRoi(jeu);
        liste.sort((a, b) -> priorite(b, jeu, estmax, king)
                           - priorite(a, jeu, estmax, king));
        return liste;
    }

    private int priorite(Coup c, Jeu jeu, boolean estmax, Point king) {
        Point dst = c.b;
        if (!estmax) {
            // Blanc : le roi vers le bord = priorité maximale
            if (jeu.grille[c.a.l][c.a.c] == 3) {
                int distBord = Math.min(
                    Math.min(dst.l, 8 - dst.l),
                    Math.min(dst.c, 8 - dst.c)
                );
                return (distBord == 0) ? 1000 : (8 - distBord) * 10;
            }
        } else {
            // Noir : coups vers le roi = priorité
            if (king != null) {
                int dist = Math.abs(dst.l - king.l) + Math.abs(dst.c - king.c);
                return Math.max(0, 20 - dist * 2);
            }
        }
        return 0;
    }



    public int alphabeta(Jeu jeu, int prof, int alpha, int beta, boolean estmax) {
        if (prof == 0 || jeu.getFini())
            return heuristique(jeu);

        int joueur = estmax ? 1 : 2;
        List<Coup> coups = trierCoups(jeu.CoupJouable(joueur).Liste, jeu, estmax);

        if (coups.isEmpty())
            return heuristique(jeu);

        if (estmax) {
            int best = Integer.MIN_VALUE;
            for (Coup c : coups) {
                Jeu copie = new Jeu(jeu);
                copie.deplacer(c.a, c.b);
                best  = Math.max(best, alphabeta(copie, prof - 1, alpha, beta, false));
                alpha = Math.max(alpha, best);
                if (beta <= alpha) break; 
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (Coup c : coups) {
                Jeu copie = new Jeu(jeu);
                copie.deplacer(c.a, c.b);
                best = Math.min(best, alphabeta(copie, prof - 1, alpha, beta, true));
                beta = Math.min(beta, best);
                if (beta <= alpha) break;  
            }
            return best;
        }
    }

    @Override
    Coup calculCoup() {
        boolean noir = (couleur == 1);   // Noir = MAX, Blanc = MIN
        int bestScore = noir ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        List<Coup> exAequo = new ArrayList<>();

        for (Coup c : j.CoupJouable(couleur).Liste) {
            Jeu copie = new Jeu(j);
            copie.deplacer(c.a, c.b);

            // Après notre coup → adversaire joue en premier dans l'arbre
            int score = alphabeta(copie, 2,Integer.MIN_VALUE, Integer.MAX_VALUE, !noir);

            if (noir) {
                if (score > bestScore) {
                    bestScore = score;
                    exAequo.clear();
                    exAequo.add(c);
                } else if (score == bestScore) {
                    exAequo.add(c);
                }
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    exAequo.clear();
                    exAequo.add(c);
                } else if (score == bestScore) {
                    exAequo.add(c);
                }
            }
        }

        // Pioche aléatoire parmi les coups de score égal → variété de jeu
        if (exAequo.isEmpty()) return null;
        return exAequo.get((int)(Math.random() * exAequo.size()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Heuristique
    //
    //   score > 0  → bon pour Noir  (attaquant)
    //   score < 0  → bon pour Blanc (défenseur)
    //
    // Critères par ordre de priorité :
    //   1. États terminaux        ±10000
    //   2. Matériel               (noirs - blancs) × 80
    //   3. Distance roi au bord   (8 - dist) × 40  [négatif]
    //   4. Menace sur roi         non-linéaire      [positif]
    //   5. Protection roi         blancs adj × 60   [négatif]
    //   6. Routes d'évasion       fork -4000, simple -200
    //   7. Mobilité globale       (mobN - mobB) × 2
    // ─────────────────────────────────────────────────────────────────────────

    private int heuristique(Jeu jeu) {
        Point king = posRoi(jeu);

        // ── 1. États terminaux ──────────────────────────────────────────────
        if (king == null)
            return +10000;   // roi capturé → Noir gagne
        if (king.l == 0 || king.l == 8 || king.c == 0 || king.c == 8)
            return -10000;   // roi sur le bord → Blanc gagne

        int score = 0;

        // ── 2. Matériel ─────────────────────────────────────────────────────
        score += (jeu.ListePointN.totale - jeu.ListePointB.totale) * 80;

        // ── 3. Distance roi au bord ─────────────────────────────────────────
        // 0 = sur le bord (état terminal capturé ci-dessus), 4 = centre
        int distBord = Math.min(
            Math.min(king.l, 8 - king.l),
            Math.min(king.c, 8 - king.c)
        );
        score -= (8 - distBord) * 40;   // négatif : bon pour Blanc

        // ── 4. Menace sur le roi (Noirs adjacents) — courbe non-linéaire ────
        int menace = 0;
        if (king.l > 0 && jeu.grille[king.l - 1][king.c] == 1) menace++;
        if (king.l < 8 && jeu.grille[king.l + 1][king.c] == 1) menace++;
        if (king.c > 0 && jeu.grille[king.l][king.c - 1] == 1) menace++;
        if (king.c < 8 && jeu.grille[king.l][king.c + 1] == 1) menace++;
        // 3 noirs adjacents = capture certaine si Blanc ne réagit pas
        score += new int[]{0, 80, 350, 5000}[menace];

        // ── 5. Protection du roi (Blancs adjacents = bouclier) ──────────────
        int protection = 0;
        if (king.l > 0 && jeu.grille[king.l - 1][king.c] == 2) protection++;
        if (king.l < 8 && jeu.grille[king.l + 1][king.c] == 2) protection++;
        if (king.c > 0 && jeu.grille[king.l][king.c - 1] == 2) protection++;
        if (king.c < 8 && jeu.grille[king.l][king.c + 1] == 2) protection++;
        score -= protection * 60;   // négatif : bon pour Blanc

        // ── 6. Routes d'évasion ─────────────────────────────────────────────
        // Un fork (2 routes opposées libres) = l'adversaire ne peut pas
        // bloquer les deux en un seul coup
        boolean routeN = true, routeS = true, routeE = true, routeO = true;

        for (int i = king.l - 1; i >= 0; i--)
            if (jeu.grille[i][king.c] != 0) { routeN = false; break; }
        for (int i = king.l + 1; i <= 8; i++)
            if (jeu.grille[i][king.c] != 0) { routeS = false; break; }
        for (int i = king.c + 1; i <= 8; i++)
            if (jeu.grille[king.l][i] != 0) { routeE = false; break; }
        for (int i = king.c - 1; i >= 0; i--)
            if (jeu.grille[king.l][i] != 0) { routeO = false; break; }

        if ((routeN && routeS) || (routeE && routeO)) {
            score -= 4000; 
        } else {
            if (routeN) score -= 200;
            if (routeS) score -= 200;
            if (routeE) score -= 200;
            if (routeO) score -= 200;
        }

        // ── 7. Mobilité globale ─────────────────────────────────────────────
        // Plus le camp a de coups disponibles, mieux il se porte
        int mobNoir  = jeu.CoupJouable(1).Liste.size();
        int mobBlanc = jeu.CoupJouable(2).Liste.size();
        score += (mobNoir - mobBlanc) * 2;

        return score;
    }
}