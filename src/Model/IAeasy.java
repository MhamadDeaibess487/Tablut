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
        if (king == null){
            // IA facile donc ajout d'un random pour parfois ne pas faire un coup gagnant, laisser une chance
            // On a 1 chance sur 3 de voir la victoire immédiate, sinon on continue à évaluer le coup comme s'il n'était pas gagnant
            if(Math.random() > 0.33) {
                //System.out.println("VICTOIRE IMMEDIATE DETECTEE");
                return 100000; // victoire immédiate pour Noir
                // 33% de chance de ne pas voir la victoire immédiate
            }
        }
        if (king.l==0||king.l==8||king.c==0||king.c==8){
            if(Math.random() < 0.33){
                //System.out.println("VICTOIRE IMMEDIATE DETECTEE");
                return -100000; // 33% de chance de ne pas voir la défaite immédiate
            }
        }     

        int score = 0;
        int[] dl = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};



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