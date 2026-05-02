package Model;

import java.util.ArrayList;
import java.util.List;

// Condition de vitoire:
// 1)Le roi s'échappe. déjà traité V
// 2)L'un des deux joeur n'a plus de coup possible. 
// 3)Le roi se fait manger. à vérifier avec manger
// 4)Plus assez de pion noir. à vérifier avec manger

//Condition null:
// Répétition 3 coups identiques.
public abstract class BaseJeu {
    protected int[][] grille; // 0 vide, 1 noir, 2 blanc, 3 roi, 4 trone
    protected int Joueur_actif; // joueur 1(Noir) ou 2(Blanc)
    protected final int Taille_plateau;
    protected int nbNoir;
    protected int nbBlanc;
    protected Point kingLoc;
    protected boolean IA_joueur1;
    protected boolean IA_joueur2;
    protected boolean fini = false; // à voir si les blanc ou noir gagnent
    // ListePion Liste_blanc;
    // ListePion Liste_noir;

    public BaseJeu() {
        Taille_plateau = 9;
        Joueur_actif = 1; // Les noirs commencent
        nbNoir = 16;
        nbBlanc = 9;
        kingLoc = new Point(4, 4);
        grille = new int[Taille_plateau][Taille_plateau];
        IA_joueur1 = false;
        IA_joueur2 = false;
        ListePion init = new ListePion();

        ListePion Liste_blanc = init.ListeBaseBlanc();
        ListePion Liste_noir = init.ListeBaseNoir();

        for (Pion PB : Liste_blanc.LP) {
            grille[PB.l][PB.c] = PB.t;
        }

        for (Pion PN : Liste_noir.LP) {
            grille[PN.l][PN.c] = PN.t;
        }
    }

    abstract void jeuRedondant();

    public int[][] getGrille() {
        return this.grille;
    }

    public int getJoueur() {
        return this.Joueur_actif;
    }

    public ListePion getListeBlanc() {
        return Liste_blanc;
    }

    public ListePion getListeNoir() {
        return Liste_noir;
    }

    public int getTaillePlateau() {
        return this.Taille_plateau;
    }

    public boolean CoupValide(int l1, int c1, int l2, int c2) {// regarde si c'est un coup sur le plateau + coup meme
                                                               // ligne ou colonne + aucun pion entre les deux
        if ((l2 < 0) || (l2 > 8) || (c2 < 0) || (c2 > 8)) {
            return false;
        }
        // if (grille[l2][c2] != 0) {
        // return false;
        // }

        if (c1 != c2 && l1 != l2) { // déplacement que perpendiculaire
            return false;
        }

        if (c1 == c2 && l1 == l2) {// reste sur la même case
            return false;
        }

        if (c2 == 4 && l2 == 4) {// se déplace vers le trone
            return false;
        }
        int min;
        int max;// vérifie si des pions sont entre les deux positions
        if (c1 == c2) {
            if (l1 >= l2) {
                min = l2;
                max = l1;
            } else {
                min = l1;
                max = l2;
            }
            for (int l = min + 1; l < max; l++) {
                if (grille[l][c1] != 0) {
                    return false;
                }
            }
        }

        if (l1 == l2) {
            if (c1 >= c2) {
                min = c2;
                max = c1;
            } else {
                min = c1;
                max = c2;
            }
            for (int c = min + 1; c < max; c++) {
                if (grille[l1][c] != 0) {
                    return false;
                }
            }
        }

        return true;

    }

    public boolean estCaseLibre(int l, int c) {
        if (grille[l][c] != 0) {
            return false;
        }
        return true;
    }

    public boolean deplacer(Point a, Point b) {// renvoie faux si coup invalide sinon renvoie vrai avec le
        // déplacement
        if (!estCaseLibre(b.i, b.j)) {
            return false;
        }
        if (!CoupValide(a.i, a.j, b.i, b.j)) {
            return false;
        }
        grille[b.i][b.j] = grille[a.i][a.j];
        grille[a.i][a.j] = 0;

        /* A voir si c'est necessaire */
        // ListePion init = new ListePion();
        // if (P.t == 1) {
        // Liste_noir = init.DeplacerListePion(Liste_noir, P.l, P.c, ligne, colonne);
        // }
        // if (P.t == 2 || P.t == 3) {
        // Liste_blanc = init.DeplacerListePion(Liste_blanc, P.l, P.c, ligne, colonne);
        // }
        //
        // if (P.t == 3) {
        // if (ligne == 0 || ligne == 8 || colonne == 0 || colonne == 8) {
        // fini = true;
        // }
        //
        // }

        // à finir avec manger

        return true;

    }

    public void afficher() {
        for (int l = 0; l < Taille_plateau; l++) {
            for (int c = 0; c < Taille_plateau; c++) {
                System.out.print(grille[l][c] + " ");
            }
            System.out.println();
        }
    }
}