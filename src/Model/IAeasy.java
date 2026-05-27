package Model;

import java.util.*;

class IAeasy extends IA {

    // ─────────────────────────────────────────────────────────────────────────
    // Profondeur de recherche (augmenter = plus fort mais plus lent)
    // depth=2 : rapide, depth=3 : bon compromis, depth=4 : lent
    private static final int PROFONDEUR = 1;
    // ─────────────────────────────────────────────────────────────────────────

    public IAeasy(Jeu j, int couleur) {
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

        // Utilisé pour détecter les vrais sandwichs
    private boolean estHostilePour(Jeu jeu, int nl, int nc, int piece) {
        if (!inBounds(nl, nc)) return false;
        int v = jeu.grille[nl][nc];
        if (piece == 1) // Noir : ennemi = Blanc(2), Roi(3), Trône(4), coins
            return v == 2 || v == 3 || v == 4 || estHostile(nl, nc);
        else            // Blanc/Roi : ennemi = Noir(1), Trône(4), coins
            return v == 1 || v == 4 || estHostile(nl, nc);
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
                
                Jeu copie = copieAvecJoueur(jeu, 1); // Noir joue
                copie.deplacer(c.a, c.b);            // → changerJoueur() → Blanc
                best  = Math.max(best, alphabeta(copie, prof-1, alpha, beta, false));
                alpha = Math.max(alpha, best);
                if (beta <= alpha) break;             //  coupure β
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (Coup c : coups) {
                Jeu copie = copieAvecJoueur(jeu, 2); // Blanc joue
                copie.deplacer(c.a, c.b);            // → changerJoueur() → Noir
                best = Math.min(best, alphabeta(copie, prof-1, alpha, beta, true));
                beta = Math.min(beta, best);
                if (beta <= alpha) break;             //  coupure α
            }
            return best;
        }
    }

    // ─── Point d'entrée ───────────────────────────────────────────────────────

    @Override
    Coup calculCoup() {
        System.out.println("IAeasy réfléchit...");
        boolean noir = (couleur == 1);
        int bestScore = noir ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        List<Coup> exAequo = new ArrayList<>();

        // Génère les coups de NOTRE couleur (indépendant de Joueur_actif)
        List<Coup> tousLesCoups = new ArrayList<>(j.CoupJouable(couleur).Liste);
        tousLesCoups.removeIf(c -> !estMonCoup(j, c));
        if (tousLesCoups.isEmpty()) return null;

        for (Coup c : tousLesCoups) {
   
            Jeu copie = copieAvecJoueur(j, couleur);
            copie.deplacer(c.a, c.b);
            // Ici copie.Joueur_actif == adversaire 

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

        // // ── 2. Matériel ─────────────────────────────────────────────────────
        // score += (jeu.ListePointN.totale - jeu.ListePointB.totale) * 500;

        // // ── 3 & 4. Pièces réellement en danger (demi-sandwich) ──────────────
        // // On détecte maintenant un vrai demi-sandwich : ennemi sur UN côté
        // // ET ennemi/hostile sur le côté OPPOSÉ → capture possible au prochain coup
        // //
        // // Axes à vérifier : N-S et E-O
        // int[][] axes = {{-1,0, 1,0}, {0,-1, 0,1}};
 
        // int suedoisMenaces = 0, noirsMenaces = 0;
 
        // for (int l = 0; l < 9; l++) {
        //     for (int c = 0; c < 9; c++) {
        //         int piece = jeu.grille[l][c];
        //         if (piece != 1 && piece != 2) continue; // Noirs et Suédois seulement
 
        //         for (int[] ax : axes) {
        //             int l1 = l + ax[0], c1 = c + ax[1]; // côté A
        //             int l2 = l + ax[2], c2 = c + ax[3]; // côté B
 
        //             boolean coteA = estHostilePour(jeu, l1, c1, piece);
        //             boolean coteB = estHostilePour(jeu, l2, c2, piece);
 
        //             if (coteA && coteB) {
        //                 // Sandwich complet : pièce déjà capturée (ne devrait pas arriver)
        //                 if (piece == 2) suedoisMenaces += 3;
        //                 else            noirsMenaces  += 3;
        //             } else if (coteA || coteB) {
        //                 // Demi-sandwich : un adversaire peut compléter la capture
        //                 if (piece == 2) suedoisMenaces++;
        //                 else            noirsMenaces++;
        //             }
        //         }
        //     }
        // }
 
        // score += suedoisMenaces * 35;   // bon pour Noir  (Suédois menacés)
        // score -= noirsMenaces  * 35;    // bon pour Blanc (Noirs menacés)

        // ── 4. Distance roi au bord ─────────────────────────────────────────
        // int distBord = Math.min(
        //     Math.min(king.l, 8 - king.l),
        //     Math.min(king.c, 8 - king.c)
        // );
        // score -= (8 - distBord) * 40;


        // ── 6. Couloirs d'évasion du roi ─────────────────────────────────────
        // le bonus de couloir partiel est réduit (était +350, maintenant +120)
        // pour ne pas valoir plus que la pièce sacrifiée (150)
        boolean[] routeLibre = new boolean[4];
        int[][] dirVec = {{-1,0},{1,0},{0,1},{0,-1}};
 
        for (int dir = 0; dir < 4; dir++) {
            int ddl = dirVec[dir][0], ddc = dirVec[dir][1];
            int l = king.l + ddl, c = king.c + ddc;
            int nNoirs = 0;
            boolean bloqueParBlanc = false;
 
            while (inBounds(l, c)) {
                int v = jeu.grille[l][c];
                if (v == 2) { bloqueParBlanc = true; break; } // Suédois ferme la route
                if (v == 1) nNoirs++;                          // Noir dans le couloir
                l += ddl; c += ddc;
            }
 
            routeLibre[dir] = !bloqueParBlanc;
 
            if (!bloqueParBlanc) {
                if (nNoirs == 0) {
                    score -= 700;     // couloir totalement libre : fort bonus Blanc
                } else {
                    // chaque Noir dans le couloir vaut ~120 pts pour Noir
                    // (inférieur à la valeur d'une pièce = 150, donc pas rentable de suicide)
                    score -= Math.max(0, 700 - nNoirs * 120);
                }
            }
        }
 
        // // Fork : deux routes opposées sans Suédois bloquant
        // boolean forkNS = routeLibre[0] && routeLibre[1];
        // boolean forkEO = routeLibre[2] && routeLibre[3];
        // if (forkNS || forkEO)
        //     score -= 4000;

        // // ── 7. Pre-fork : roi à 1 case d'une position de fork ───────────────
        // // Si le roi peut se déplacer vers une case depuis laquelle
        // // il aurait deux routes opposées libres, c'est presque aussi bon.
        // outerLoop:
        // for (int dir2 = 0; dir2 < 4; dir2++) {
        //     int nl = king.l + dirVec[dir2][0];
        //     int nc = king.c + dirVec[dir2][1];
        //     if (!inBounds(nl, nc) || jeu.grille[nl][nc] != 0) continue;
        //     // Simuler le roi ici et vérifier fork
        //     boolean[] testRoutes = new boolean[4];
        //     for (int d3 = 0; d3 < 4; d3++) {
        //         int l2 = nl + dirVec[d3][0], c2 = nc + dirVec[d3][1];
        //         boolean libre = true;
        //         while (inBounds(l2, c2)) {
        //             int v = jeu.grille[l2][c2];
        //             if (v == 3) { l2 += dirVec[d3][0]; c2 += dirVec[d3][1]; continue; } // ignore roi source
        //             if (v != 0 && v != 4) { libre = false; break; }
        //             l2 += dirVec[d3][0]; c2 += dirVec[d3][1];
        //         }
        //         testRoutes[d3] = libre;
        //     }
        //     if ((testRoutes[0] && testRoutes[1]) || (testRoutes[2] && testRoutes[3])) {
        //         score -= 1500;  // pre-fork : Blanc peut créer un fork au prochain coup
        //         break outerLoop;
        //     }
        // }

        // ── 8. Menace sur le roi (Noirs ou trône adjacents) ─────────────────
        // int menace = 0;
        // for (int i = 0; i < 4; i++) {
        //     int nl = king.l + dl[i], nc = king.c + dc[i];
        //     if (!inBounds(nl, nc)) continue;
        //     int v = jeu.grille[nl][nc];
        //     if (v == 1 || v == 4) menace++;
        // }
        // score += new int[]{0, 80, 350, 8000}[Math.min(menace, 3)];
 
        // ── 9. Protection du roi (Blancs adjacents = bouclier) ───────────────
        int protection = 0;
        for (int i = 0; i < 4; i++) {
            int nl = king.l + dl[i], nc = king.c + dc[i];
            if (inBounds(nl, nc) && jeu.grille[nl][nc] == 2) protection++;
        }
        score -= protection * 15;

        // ── 10. Pression globale Noirs → roi (distance de Manhattan) ─────────
        // Plus les Noirs sont proches du roi globalement, plus la menace est forte
        // Remplace partiellement la mobilité (coûteuse) par une mesure positionnelle rapide
        int sommeDist = 0;
        int nbNoirs = 0;
        for (int l = 0; l < 9; l++) {
            for (int c = 0; c < 9; c++) {
                if (jeu.grille[l][c] == 1) {
                    sommeDist += Math.abs(l - king.l) + Math.abs(c - king.c);
                    nbNoirs++;
                }
            }
        }
        // Moyenne des distances : plus petite = Noirs mieux positionnés
        if (nbNoirs > 0) {
            int moyDist = sommeDist / nbNoirs;
            score += (8 - moyDist) * 15; // 0 à +120 selon la proximité moyenne
        }


        return score;
    }
}