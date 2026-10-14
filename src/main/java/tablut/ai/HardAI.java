package tablut.ai;

import tablut.model.*;


class HardAI extends AlphaBetaAI {
    // Recursive depth AFTER the candidate move; total horizon is depth + 1 plies.
    private static final int PROFONDEUR = 3;

    public HardAI(Jeu j, int couleur) {
        super(j, couleur, PROFONDEUR, true);
    }

        // Utilisé pour détecter les vrais sandwichs
    private boolean estHostilePour(Jeu jeu, int nl, int nc, int piece) {
        if (!inBounds(nl, nc)) return false;
        int v = jeu.getGrille()[nl][nc];
        if (piece == 1) // Noir : ennemi = Blanc(2), Roi(3), Trône(4), coins
            return v == 2 || v == 3 || v == 4 || estHostile(nl, nc);
        else            // Blanc/Roi : ennemi = Noir(1), Trône(4), coins
            return v == 1 || v == 4 || estHostile(nl, nc);
    }

    @Override
    protected int heuristique(Jeu jeu) {
        Point king = posRoi(jeu);
        if (king == null)                                    return +100000; // roi capturé
        if (king.l==0||king.l==8||king.c==0||king.c==8)     return -100000; // roi échappé

        int score = 0;
        int[] dl = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        score += (jeu.getPieces(1).size() - jeu.getPieces(2).size()) * 500;
        // On détecte maintenant un vrai demi-sandwich : ennemi sur UN côté
        // ET ennemi/hostile sur le côté OPPOSÉ → capture possible au prochain coup
        //
        // Axes à vérifier : N-S et E-O
        int[][] axes = {{-1,0, 1,0}, {0,-1, 0,1}};

        int suedoisMenaces = 0, noirsMenaces = 0;

        for (int l = 0; l < 9; l++) {
            for (int c = 0; c < 9; c++) {
                int piece = jeu.getGrille()[l][c];
                if (piece != 1 && piece != 2) continue; // Noirs et Suédois seulement

                for (int[] ax : axes) {
                    int l1 = l + ax[0], c1 = c + ax[1]; // côté A
                    int l2 = l + ax[2], c2 = c + ax[3]; // côté B

                    boolean coteA = estHostilePour(jeu, l1, c1, piece);
                    boolean coteB = estHostilePour(jeu, l2, c2, piece);

                    if (coteA && coteB) {
                        // Sandwich complet : pièce déjà capturée (ne devrait pas arriver)
                        if (piece == 2) suedoisMenaces += 3;
                        else            noirsMenaces  += 3;
                    } else if (coteA || coteB) {
                        // Demi-sandwich : un adversaire peut compléter la capture
                        if (piece == 2) suedoisMenaces++;
                        else            noirsMenaces++;
                    }
                }
            }
        }

        score += suedoisMenaces * 35;   // bon pour Noir  (Suédois menacés)
        score -= noirsMenaces  * 35;    // bon pour Blanc (Noirs menacés)
        int distBord = Math.min(
            Math.min(king.l, 8 - king.l),
            Math.min(king.c, 8 - king.c)
        );
        score -= (8 - distBord) * 40;
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
                int v = jeu.getGrille()[l][c];
                if (v == 2) { bloqueParBlanc = true; break; } // Suédois ferme la route
                if (v == 1) nNoirs++;                          // Noir dans le couloir
                l += ddl; c += ddc;
            }

            routeLibre[dir] = !bloqueParBlanc;

            if (!bloqueParBlanc) {
                if (nNoirs == 0) {
                    score -= 700;     // couloir totalement libre : fort bonus Blanc
                } else {
                    // bonus réduit pour couloir partiellement bloqué
                    // chaque Noir dans le couloir vaut ~120 pts pour Noir
                    // (inférieur à la valeur d'une pièce = 150, donc pas rentable de suicide)
                    score -= Math.max(0, 700 - nNoirs * 120);
                }
            }
        }

        // Fork : deux routes opposées sans Suédois bloquant
        boolean forkNS = routeLibre[0] && routeLibre[1];
        boolean forkEO = routeLibre[2] && routeLibre[3];
        if (forkNS || forkEO)
            score -= 4000;
        // Si le roi peut se déplacer vers une case depuis laquelle
        // il aurait deux routes opposées libres, c'est presque aussi bon.
        outerLoop:
        for (int dir2 = 0; dir2 < 4; dir2++) {
            int nl = king.l + dirVec[dir2][0];
            int nc = king.c + dirVec[dir2][1];
            if (!inBounds(nl, nc) || jeu.getGrille()[nl][nc] != 0) continue;
            // Simuler le roi ici et vérifier fork
            boolean[] testRoutes = new boolean[4];
            for (int d3 = 0; d3 < 4; d3++) {
                int l2 = nl + dirVec[d3][0], c2 = nc + dirVec[d3][1];
                boolean libre = true;
                while (inBounds(l2, c2)) {
                    int v = jeu.getGrille()[l2][c2];
                    if (v == 3) { l2 += dirVec[d3][0]; c2 += dirVec[d3][1]; continue; } // ignore roi source
                    if (v != 0 && v != 4) { libre = false; break; }
                    l2 += dirVec[d3][0]; c2 += dirVec[d3][1];
                }
                testRoutes[d3] = libre;
            }
            if ((testRoutes[0] && testRoutes[1]) || (testRoutes[2] && testRoutes[3])) {
                score -= 1500;  // pre-fork : Blanc peut créer un fork au prochain coup
                break outerLoop; // pas besoin de vérifier les autres cases adjacentes au roi
            }
        }
        int menace = 0;
        for (int i = 0; i < 4; i++) {
            int nl = king.l + dl[i], nc = king.c + dc[i];
            if (!inBounds(nl, nc)) continue;
            int v = jeu.getGrille()[nl][nc];
            if (v == 1 || v == 4) menace++;
        }
        score += new int[]{0, 80, 350, 8000}[Math.min(menace, 3)];
        int protection = 0;
        for (int i = 0; i < 4; i++) {
            int nl = king.l + dl[i], nc = king.c + dc[i];
            if (inBounds(nl, nc) && jeu.getGrille()[nl][nc] == 2) protection++;
        }
        score -= protection * 15;
        // Plus les Noirs sont proches du roi globalement, plus la menace est forte
        // Remplace partiellement la mobilité (coûteuse) par une mesure positionnelle rapide
        int sommeDist = 0;
        int nbNoirs = 0;
        for (int l = 0; l < 9; l++) {
            for (int c = 0; c < 9; c++) {
                if (jeu.getGrille()[l][c] == 1) {
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
