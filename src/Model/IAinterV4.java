package Model;

import java.util.*;

class IAinterV4 extends IA {

    public IAinterV4(Jeu j, int couleur) {
        this.j = j;
        this.couleur = couleur;
    }

    // Si true : lorsque la recherche est faite à profondeur 1, jouer immédiatement
    // un coup qui donne la victoire (roi sur bord pour Blanc, roi capturé pour Noir).
    private boolean jouerVictoireImmed = true;

    public void setJouerVictoireImmed(boolean v){ this.jouerVictoireImmed = v; }

    private Point posRoi(Jeu jeu) {
        for (int l = 0; l < 9; l++)
            for (int c = 0; c < 9; c++)
                if (jeu.grille[l][c] == 3)
                    return new Point(l, c);
        return null;
    }
    // sur le plateau
    private boolean inBounds(int l, int c) {
        return l >= 0 && l <= 8 && c >= 0 && c <= 8;
    }

    // coins et trone 
    private boolean estHostile(int l, int c) {
        return (l == 4 && c == 4)
            || (l == 0 && c == 0) || (l == 0 && c == 8)
            || (l == 8 && c == 0) || (l == 8 && c == 8);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Estimation légère des captures produites par un coup
    // Sans copie de plateau → O(1) par direction
    //
    // Principe du sandwich : pièce ennemie en (dst + dir) capturée si
    // (dst + 2*dir) est une pièce amie OU une case hostile
    // ─────────────────────────────────────────────────────────────────────────

    private int capturesEstimees(Point src, Point dst, Jeu jeu) {
        int piece = jeu.grille[src.l][src.c];
        int captures = 0;
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};

        for (int[] d : dirs) {
            int ml = dst.l + d[0], mc = dst.c + d[1]; // case du milieu (victime potentielle)
            int bl = dst.l + 2*d[0], bc = dst.c + 2*d[1]; // case derrière

            if (!inBounds(ml, mc)) continue;
            int milieu = jeu.grille[ml][mc];
            if (milieu == 0) continue;

            // La victime est-elle une pièce ennemie ?
            boolean ennemi = (piece == 1) ? (milieu == 2 || milieu == 3)
                                           : (milieu == 1);
            if (!ennemi) continue;

            // Roi : capture seulement si entouré sur 3 autres côtés aussi,
            // ici on l'approxime comme une pièce normale pour le tri
            boolean derriereFriendly = inBounds(bl, bc)
                && ((piece == 1 && jeu.grille[bl][bc] == 1)
                 || (piece == 2 && (jeu.grille[bl][bc] == 2 || jeu.grille[bl][bc] == 3)));
            boolean derriereHostile = !inBounds(bl, bc) || estHostile(bl, bc);

            if (derriereFriendly || derriereHostile) captures++;
        }
        return captures;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tri des coups — améliore drastiquement l'élagage alpha-bêta
    //
    //   Priorité (décroissant) :
    //   1000  roi Blanc va sur le bord          → victoire immédiate
    //    500  capture d'une ou plusieurs pièces  → très fort
    //    100  roi Blanc se rapproche du bord     → important
    //     20  Noir se rapproche du roi           → menaçant
    //      0  tout le reste

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

        // Blanc : roi vers le bord
        if (!estmax && piece == 3) {
            int dist = Math.min(Math.min(dst.l, 8-dst.l), Math.min(dst.c, 8-dst.c));
            if (dist == 0) return 1000;            // victoire immédiate
            return 100 + (4 - dist) * 20;          // plus proche = plus prioritaire
        }
        
        // Captures (les deux camps)
        int cap = capturesEstimees(src, dst, jeu);
        if (cap > 0) return 500 + cap * 100;

        // Noir : rapprocher une pièce du roi
        if (estmax && king != null) {
            int dist = Math.abs(dst.l - king.l) + Math.abs(dst.c - king.c);
            if (dist == 1) {
                // Si la pièce se place directement au sud du roi, encore plus prioritaire
                if (dst.l == king.l + 1 && dst.c == king.c) return 800;
                return 400;
            }
            return Math.max(0, 20 - dist * 2);
        }

        return 0;
    }



    //a modifier, faire annule coup apres avoir jouer le coup pour ne pas faire de copie de plateau à chaque fois
    public int alphabeta(Jeu jeu,int prof, int alpha, int beta, boolean estmax) {
        if (prof == 0 || jeu.getFini()){
            return heuristique(jeu);
        }

        //int joueur = estmax ? 1 : 2; // PQ ?? ON A DEJA LA COULEUR DE LIA DANS IA.JAVA
//        List<Coup> coups = trierCoups(jeu.CoupJouable(joueur).Liste, jeu, estmax);
        List<Coup> coups = jeu.CoupJouable(jeu.Joueur_actif).Liste;

        if (coups.isEmpty())
            return heuristique(jeu);

        if (estmax) {
            int best = Integer.MIN_VALUE;
            for (Coup c : coups) {
                Jeu copie = new Jeu(jeu);
                copie.deplacer(c.a, c.b);
                best = Math.max(best, alphabeta(copie, prof-1, alpha, beta, false)); // estmax doit etre false pour le coup suivant 
//                if(best > 5000){
//                   System.err.println("Score " + best);
//                }
                alpha = Math.max(alpha, best);
                if (beta <= alpha || alpha >= 100000) break;   // coupure β
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (Coup c : coups) {
                Jeu copie = new Jeu(jeu);
                copie.deplacer(c.a, c.b);
                best = Math.min(best, alphabeta(copie, prof-1, alpha, beta, true)); // estmax doit etre true pour le coup suivant
                beta = Math.min(beta, best);
                if (beta <= alpha || beta <= -100000) break;   // coupure α
            }
            return best;
        }
    }


    @Override
    Coup calculCoup() {
        System.out.println("IA INTER CALCUL COUP");
        boolean noir = (couleur == 1);
        int bestScore = noir ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        List<Coup> exAequo = new ArrayList<>();
        List<Coup> tousLesCoups = j.CoupJouable(couleur).Liste;
        
        //si aucun coup disponible, la partie est terminée
        if (tousLesCoups.isEmpty()) return null;

        int profondeurRecherche = 1; // profondeur utilisée pour l'appel alphabeta
        for (Coup c : tousLesCoups) {

            // Vérification : la pièce source appartient bien à notre couleur
            // int pieceSource = j.grille[c.a.l][c.a.c];
            // boolean bonCamp = (couleur == 1) ? (pieceSource == 1) : (pieceSource == 2 || pieceSource == 3);
            // if (!bonCamp) {
            //     System.err.println("COUP INVALIDE : pièce " + pieceSource
            //         + " pour couleur " + couleur + " en (" + c.a.l + "," + c.a.c + ")");
            //     continue;  // ignore ce coup
            // }

            Jeu copie = new Jeu(j);
            copie.deplacer(c.a, c.b);

            // ============== Optimisation pour coup gagnat immédiat ( et probleme de score supérieur pour profondeur 2 ou 3) ============== 
            // Si détection immédiate d'un coup gagnant lorsque la recherche se fait à profondeur 1 (prochain coup).
            if (jouerVictoireImmed) {
                Point roiApres = posRoi(copie);

                // Blanc gagne si le roi atteint un bord
                if (couleur == 2 && roiApres != null &&
                    (roiApres.l == 0 || roiApres.l == 8 || roiApres.c == 0 || roiApres.c == 8)) {
                    System.out.println("Coup gagnant immédiat détecté ! (BLANC)");
                    return c;
                }

                // Noir gagne si le roi n'existe plus (capturé)
                if (couleur == 1 && copie.getFini()) {
                    System.out.println("Coup gagnant immédiat détecté ! (NOIR)");
                    return c;
                }
            }
            // ======================= //

            int score = alphabeta(copie, profondeurRecherche, Integer.MIN_VALUE, Integer.MAX_VALUE, !noir);
//            if(score > 5000){
//                System.err.println("Score " + score);
//                System.err.println(c.a.l+" " + c.a.c + " " + c.b.l + " " + c.b.c);
//            }
            if (noir) {
                if (score > bestScore) { bestScore = score; exAequo.clear(); exAequo.add(c); }
                else if (score == bestScore) exAequo.add(c);
            } else {
                if (score < bestScore) { bestScore = score; exAequo.clear(); exAequo.add(c); }
                else if (score == bestScore) exAequo.add(c);
            }
        }

        //fallback sur le premier coup si exAequo vide (ne devrait pas arriver) -- tirage au sort parmi meilleur coups
        if (exAequo.isEmpty()) return tousLesCoups.get(0);
        return exAequo.get((int)(Math.random() * exAequo.size()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Heuristique
    //
    //   score > 0 → bon pour Noir  (attaquant)
    //   score < 0 → bon pour Blanc (défenseur)
    //
    //   Critères :
    //   1. États terminaux            ±10000
    //   2. Matériel                   (noirs - blancs) × 100
    //   3. Suédois en danger          nombre de Suédois avec Noir adjacent × 40
    //   4. Distance roi au bord       (8 - dist) × 40  [négatif = bon Blanc]
    //   5. Menace sur roi             non-linéaire      [positif = bon Noir]
    //   6. Protection roi             Blancs adj × 60   [négatif = bon Blanc]
    //   7. Routes d'évasion           fouchette -4000, simple -200
    //   8. Mobilité                   (mobN - mobB) × 2
    // ─────────────────────────────────────────────────────────────────────────

    private int heuristique(Jeu jeu) {
        Point king = posRoi(jeu); // On peut stcoker la pos du roi dans basejeu => plus optimiser

        // ── 1. États terminaux ──────────────────────────────────────────────
        if (king == null)
            return +100000;
        if (king.l==0 || king.l==8 || king.c==0 || king.c==8)
            return -100000;

        int score = 0;

        // ── 2. Matériel ─────────────────────────────────────────────────────
        score += (jeu.ListePointN.totale - jeu.ListePointB.totale) * 500;

        // ── 3. Suédois en danger (Noir adjacent → peut être capturé) ────────
        // FIX bug 1 : ce critère manquait, l'IA sacrifiait des pièces sans le voir
        int enDanger = 0;
        for (int l = 0; l < 9; l++) {
            for (int c = 0; c < 9; c++) {
                if (jeu.grille[l][c] != 2) continue;  // seulement les Suédois
                int noirAdj = 0;
                if (l > 0 && jeu.grille[l-1][c] == 1) noirAdj++;
                if (l < 8 && jeu.grille[l+1][c] == 1) noirAdj++;
                if (c > 0 && jeu.grille[l][c-1] == 1) noirAdj++;
                if (c < 8 && jeu.grille[l][c+1] == 1) noirAdj++;
                if (noirAdj >= 1) enDanger++;
            }
        }
        score += enDanger * 40;   // positif : mauvais pour Blanc

        // ── 4. Distance roi au bord ─────────────────────────────────────────
        int distBord = Math.min(
            Math.min(king.l, 8 - king.l),
            Math.min(king.c, 8 - king.c)
        );
        score -= (8 - distBord) * 40;

        // ── 5. Menace sur le roi (Noirs adjacents) — non-linéaire ───────────
        int menace = 0;
        if (king.l < 3 || king.l > 5 && king.c < 3 || king.c > 5){
            if(jeu.grille[king.l-1][king.c] == 1){
                if(jeu.grille[king.l+1][king.c] == 1)
                    menace=3;
                menace++;
            }
            if(jeu.grille[king.l][king.c-1] == 1){
                if(jeu.grille[king.l][king.c+1] == 1)
                    menace=3;
                menace++;
            }
            if(jeu.grille[king.l+1][king.c] == 1){
                if(jeu.grille[king.l-1][king.c] == 1)
                    menace=3;
                menace++;
            }
            if(jeu.grille[king.l][king.c+1] == 1){
                if(jeu.grille[king.l][king.c-1] == 1)
                    menace=3;
                menace++;
            }
        }
        else{
            if (jeu.grille[king.l-1][king.c] == 1|| jeu.grille[king.l-1][king.c]==4) menace++;
            if (jeu.grille[king.l+1][king.c] == 1|| jeu.grille[king.l-1][king.c]==4) menace++;
            if (jeu.grille[king.l][king.c-1] == 1|| jeu.grille[king.l-1][king.c]==4) menace++;
            if (jeu.grille[king.l][king.c+1] == 1|| jeu.grille[king.l-1][king.c]==4) menace++;
        }
        score += new int[]{0, 80, 350, 100000}[menace>3?3:menace];
    
        // ── 6. Protection du roi (Blancs adjacents = bouclier) ──────────────
        int protection = 0;
        if (king.l > 0 && jeu.grille[king.l-1][king.c] == 2) protection++;
        if (king.l < 8 && jeu.grille[king.l+1][king.c] == 2) protection++;
        if (king.c > 0 && jeu.grille[king.l][king.c-1] == 2) protection++;
        if (king.c < 8 && jeu.grille[king.l][king.c+1] == 2) protection++;
        score -= protection * 60;

        // ── 6.5 Potentiel de capture au prochain tour (si Noir peut capturer le roi)
        // On simule rapidement tous les coups noirs possibles et on compte
        // ceux qui terminent la partie (capture du roi)
        // if (king != null) { // trop cher a calculer
        //     int potCap = 0;
        //     List<Coup> coupsNoirs = jeu.CoupJouable(1).Liste;
        //     for (Coup mv : coupsNoirs) {
        //         Jeu sim = new Jeu(jeu);
        //         sim.deplacer(mv.a, mv.b);
        //         if (sim.getFini()) potCap++;
        //     }
        //     score += potCap * 8000; // gros poids : capture prochaine vaut presque une victoire
        // }

        // ── 7. Routes d'évasion ─────────────────────────────────────────────
        boolean routeN = true, routeS = true, routeE = true, routeO = true;
        for (int i = king.l-1; i >= 0; i--)
            if (jeu.grille[i][king.c] != 0) { routeN = false; break; }
        for (int i = king.l+1; i <= 8; i++)
            if (jeu.grille[i][king.c] != 0) { routeS = false; break; }
        for (int i = king.c+1; i <= 8; i++)
            if (jeu.grille[king.l][i] != 0) { routeE = false; break; }
        for (int i = king.c-1; i >= 0; i--)
            if (jeu.grille[king.l][i] != 0) { routeO = false; break; }

        if ((routeN && routeS) || (routeE && routeO)) {
            score -= 4000;   // fork : victoire quasi-certaine Blanc
        } else {
            if (routeN) score -= 200;
            if (routeS) score -= 200;
            if (routeE) score -= 200;
            if (routeO) score -= 200;
        }

        // ── 8. Mobilité globale ─────────────────────────────────────────────
        score += (jeu.CoupJouable(1).Liste.size()
                - jeu.CoupJouable(2).Liste.size()) * 2;
        
        return score;
    }
}