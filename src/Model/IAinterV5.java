package Model;

import java.util.*;

class IAinterV5 extends IA {

    // ─────────────────────────────────────────────────────────────────────────
    // Profondeur de recherche (augmenter = plus fort mais plus lent)
    // depth=2 : rapide, depth=3 : bon compromis, depth=4 : lent
    private static final int PROFONDEUR = 3;
    // ─────────────────────────────────────────────────────────────────────────

    public IAinterV5(Jeu j, int couleur) {
        this.j = j;
        this.couleur = couleur;
    }

    // ─── Utilitaires ─────────────────────────────────────────────────────────

    private Point posRoi(Jeu jeu) {
        for (int l = 0; l < 9; l++)
            for (int c = 0; c < 9; c++)
                if (jeu.grille[l][c] == 3)
                    return new Point(l, c);
        return null;
    }

    private boolean inBounds(int l, int c) {
        return l >= 0 && l <= 8 && c >= 0 && c <= 8;
    }

    private boolean estHostile(int l, int c) {
        return (l == 4 && c == 4)
            || (l == 0 && c == 0) || (l == 0 && c == 8)
            || (l == 8 && c == 0) || (l == 8 && c == 8);
    }

    private int countOpenPath(Jeu jeu, Point king, int dl, int dc) {
        int l = king.l + dl;
        int c = king.c + dc;
        int count = 0;
        while (inBounds(l, c)) {
            if (jeu.grille[l][c] != 0) return -1;
            count++;
            l += dl;
            c += dc;
        }
        return count;
    }

    private boolean estMonCoup(Jeu jeu, Coup c) {
        int src = jeu.grille[c.a.l][c.a.c];
        return (couleur == 1 && src == 1)
            || (couleur == 2 && (src == 2 || src == 3));
    }

    // ─── Copie de jeu avec joueur forcé ──────────────────────────────────────
    // on force Joueur_actif = joueur AVANT deplacer()
    // → changerJoueur() donnera toujours l'adversaire au niveau suivant
    // → plus jamais de décalage même si le contrôleur appelle joue() hors tour

    private Jeu copieAvecJoueur(Jeu src, int joueur) {
        Jeu copie = new Jeu(src);
        copie.Joueur_actif = joueur;
        return copie;
    }

    // ─── Estimation légère des captures (sans copie de plateau) ──────────────
    // Principe du sandwich : victime en (dst+dir), alliée/hostile en (dst+2*dir)

    private int capturesEstimees(Point src, Point dst, Jeu jeu) {
        int piece = jeu.grille[src.l][src.c];
        int captures = 0;
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};

        for (int[] d : dirs) {
            int ml = dst.l + d[0], mc = dst.c + d[1]; // victime potentielle
            int bl = dst.l + 2*d[0], bc = dst.c + 2*d[1]; // derrière

            if (!inBounds(ml, mc)) continue;
            int milieu = jeu.grille[ml][mc];
            if (milieu == 0 || milieu == 4) continue;

            boolean ennemi = (piece == 1) ? (milieu == 2 || milieu == 3)
                                           : (milieu == 1);
            if (!ennemi) continue;

            boolean derriereFriendly = inBounds(bl, bc)
                && ((piece == 1 && jeu.grille[bl][bc] == 1)
                 || (piece != 1 && (jeu.grille[bl][bc] == 2 || jeu.grille[bl][bc] == 3)));
            boolean derriereHostile = !inBounds(bl, bc) || estHostile(bl, bc);

            if (derriereFriendly || derriereHostile) captures++;
        }
        return captures;
    }

    // ─── Tri des coups ────────────────────────────────────────────────────────

        // Détecte si, dans la position donnée (le joueur courant est celui
    // qui joue maintenant), l'adversaire possède un coup qui termine
    // immédiatement la partie (capture du roi ou victoire).
    private boolean roiPeutEtreCaptureImmediat(Jeu jeu) {
        // Si la position est déjà terminale, considérer comme vulnérable
        if (jeu.getFini()) return true;

        Point king = posRoi(jeu);
        if (king == null) return true;

        int adv = jeu.Joueur_actif; // joueur qui va jouer maintenant
        List<Coup> coups = jeu.CoupJouable(adv).Liste;
        for (Coup c : coups) {
            Jeu copie = copieAvecJoueur(jeu, adv);
            copie.deplacer(c.a, c.b);
            if (copie.getFini()) return true;
        }
        return false;
    }

    // Meilleurs coups explorés en premier → alpha-bêta élague plus tôt

    private List<Coup> trierCoups(List<Coup> coups, Jeu jeu, boolean estmax) {
        Point king = posRoi(jeu);
        List<Coup> liste = new ArrayList<>(coups);
        liste.sort((a, b) ->
            priorite(b, jeu, estmax, king) - priorite(a, jeu, estmax, king)
        );
        return liste;
    }

    private int priorite(Coup c, Jeu jeu, boolean estmax, Point king) {
        Point src = c.a, dst = c.b;
        int piece = jeu.grille[src.l][src.c];

        // // Si le mouvement est un déplacement du roi, vérifier si après
        // // ce coup l'adversaire peut capturer le roi immédiatement.
        // // On pénalise fortement ces coups pour éviter que l'IA mette
        // // le roi à portée d'une capture immédiate.
        // if (!estmax && piece == 3) {
        //     Jeu copie = copieAvecJoueur(jeu, jeu.Joueur_actif);
        //     copie.deplacer(src, dst);
        //     if (roiPeutEtreCaptureImmediat(copie)) return -100000;
        // }

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

    // ─── Alpha-Bêta ───────────────────────────────────────────────────────────
    // estmax = true  → Noir joue  (cherche MAX)
    // estmax = false → Blanc joue (cherche MIN)
    // Joueur_actif est maintenu par changerJoueur() dans Jeu.deplacer()

    public int alphabeta(Jeu jeu, int prof, int alpha, int beta, boolean estmax) {
        if (prof == 0 || jeu.getFini())
            return heuristique(jeu);

        // Joueur_actif est garanti correct ici grâce à copieAvecJoueur()
        List<Coup> coups = trierCoups(
            jeu.CoupJouable(jeu.Joueur_actif).Liste, jeu, estmax
        );

        if (coups.isEmpty()) return heuristique(jeu);

        if (estmax) {
            int best = Integer.MIN_VALUE;
            for (Coup c : coups) {
                // Joueur_actif actuel est estmax ? 1 : 2, on le force pour être sûr
                Jeu copie = copieAvecJoueur(jeu, 1); // Noir joue
                copie.deplacer(c.a, c.b);            // → changerJoueur() → Blanc
                best  = Math.max(best, alphabeta(copie, prof-1, alpha, beta, false));
                alpha = Math.max(alpha, best);
                if (beta <= alpha) break;             // ✂ coupure β
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (Coup c : coups) {
                Jeu copie = copieAvecJoueur(jeu, 2); // Blanc joue
                copie.deplacer(c.a, c.b);            // → changerJoueur() → Noir
                best = Math.min(best, alphabeta(copie, prof-1, alpha, beta, true));
                beta = Math.min(beta, best);
                if (beta <= alpha) break;             // ✂ coupure α
            }
            return best;
        }
    }

    // ─── Point d'entrée ───────────────────────────────────────────────────────

    @Override
    Coup calculCoup() {
        System.out.println("IAinterV5 réfléchit...");
        boolean noir = (couleur == 1);
        int bestScore = noir ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        List<Coup> exAequo = new ArrayList<>();

        // Génère les coups de NOTRE couleur (indépendant de Joueur_actif)
        List<Coup> tousLesCoups = new ArrayList<>(j.CoupJouable(couleur).Liste);
        tousLesCoups.removeIf(c -> !estMonCoup(j, c));
        if (tousLesCoups.isEmpty()) return null;

        for (Coup c : tousLesCoups) {
            // Force Joueur_actif = couleur AVANT deplacer
            // → changerJoueur() donnera l'adversaire, l'arbre est toujours en sync
            Jeu copie = copieAvecJoueur(j, couleur);
            copie.deplacer(c.a, c.b);
            // Ici copie.Joueur_actif == adversaire ✓

            // Victoire immédiate : jouer sans hésiter
            Point roiApres = posRoi(copie);
            if (couleur == 2 && roiApres != null
                && (roiApres.l==0||roiApres.l==8||roiApres.c==0||roiApres.c==8))
                return c;
            if (couleur == 1 && copie.getFini())
                return c;

            // L'adversaire commence dans l'arbre → !noir
            int score = alphabeta(
                copie, PROFONDEUR,
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

    // ─── Heuristique ─────────────────────────────────────────────────────────
    // score > 0 → bon pour Noir  (attaquant)
    // score < 0 → bon pour Blanc (défenseur)

    private int heuristique(Jeu jeu) {
        Point king = posRoi(jeu);

        // ── 1. États terminaux ──────────────────────────────────────────────
        if (king == null)                                    return +100000; // roi capturé
        if (king.l==0||king.l==8||king.c==0||king.c==8)     return -100000; // roi échappé

        int score = 0;
        int[] dl = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        // ── 2. Matériel ─────────────────────────────────────────────────────
        score += (jeu.ListePointN.totale - jeu.ListePointB.totale) * 500;

        // ── 3. Suédois en danger (Noir adjacent → peut être sandwiché) ──────
        int enDanger = 0;
        for (int l = 0; l < 9; l++) {
            for (int c = 0; c < 9; c++) {
                if (jeu.grille[l][c] != 2) continue;
                for (int i = 0; i < 4; i++) {
                    int nl = l + dl[i], nc = c + dc[i];
                    if (inBounds(nl, nc) && jeu.grille[nl][nc] == 1) { enDanger++; break; }
                }
            }
        }
        score += enDanger * 40;

        // ── 4. Distance roi au bord ─────────────────────────────────────────
        int distBord = Math.min(
            Math.min(king.l, 8 - king.l),
            Math.min(king.c, 8 - king.c)
        );
        score -= (8 - distBord) * 40;

        // ── 4.5 Évaluation des corridors d'évasion ───────────────────────────
        int openN = countOpenPath(jeu, king, -1, 0);
        int openS = countOpenPath(jeu, king, 1, 0);
        int openE = countOpenPath(jeu, king, 0, 1);
        int openO = countOpenPath(jeu, king, 0, -1);

        if (openN >= 0) score -= 800 + openN * 10;
        if (openS >= 0) score -= 800 + openS * 10;
        if (openE >= 0) score -= 800 + openE * 10;
        if (openO >= 0) score -= 800 + openO * 10;

        if ((openN >= 0 && openS >= 0) || (openE >= 0 && openO >= 0)) {
            score -= 3000; // fork : deux couloirs opposés
        }

        // ── 5. Menace sur le roi — Noirs ou trône adjacents (non-linéaire) ──
        int menace = 0;
        for (int i = 0; i < 4; i++) {
            int nl = king.l + dl[i], nc = king.c + dc[i];
            if (!inBounds(nl, nc)) continue;
            int v = jeu.grille[nl][nc];
            if (v == 1 || v == 4) menace++;
        }
        // 3 adj = capture certaine au prochain coup si Blanc ne réagit pas
        score += new int[]{0, 80, 350, 8000}[Math.min(menace, 3)];

        // ── 6. Protection du roi — Blancs adjacents ─────────────────────────
        int protection = 0;
        for (int i = 0; i < 4; i++) {
            int nl = king.l + dl[i], nc = king.c + dc[i];
            if (inBounds(nl, nc) && jeu.grille[nl][nc] == 2) protection++;
        }
        score -= protection * 15;

        // ── 7. Routes d'évasion ─────────────────────────────────────────────
        boolean routeN = openN >= 0, routeS = openS >= 0, routeE = openE >= 0, routeO = openO >= 0;

        // Fork : deux routes opposées libres → Blanc ne peut pas être bloqué
        if ((routeN && routeS) || (routeE && routeO))
            score -= 4000;
        else {
            if (routeN) score -= 200;
            if (routeS) score -= 200;
            if (routeE) score -= 200;
            if (routeO) score -= 200;
        }

        // ── 8. Mobilité globale ─────────────────────────────────────────────
        //score += (jeu.CoupJouable(1).Liste.size()
        //        - jeu.CoupJouable(2).Liste.size()) * 2;

        return score;
    }
}