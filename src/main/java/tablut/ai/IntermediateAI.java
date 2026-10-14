package tablut.ai;

import tablut.model.*;


class IntermediateAI extends AlphaBetaAI {
    // Recursive depth AFTER the candidate move; total horizon is depth + 1 plies.
    private static final int PROFONDEUR = 1;

    public IntermediateAI(Jeu j, int couleur) {
        super(j, couleur, PROFONDEUR, false);
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
        // le bonus de couloir partiel est réduit (était +350, maintenant +120)
        // pour ne pas valoir plus que la pièce sacrifiée (150)
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
