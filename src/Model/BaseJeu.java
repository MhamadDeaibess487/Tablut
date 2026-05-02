package Model;

// Condition de vitoire:
// 1)Le roi s'échappe. déjà traité V
// 2)L'un des deux joeur n'a plus de coup possible. 
// 3)Le roi se fait manger. déjà traité V
// 4)Plus assez de pion noir. déjà traité V

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
    ListePion Liste_blanc;
    ListePion Liste_noir;

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

        Liste_blanc = init.ListeBaseBlanc();
        Liste_noir = init.ListeBaseNoir();

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
        if (!estCaseLibre(b.l, b.c)) {
            return false;
        }
        if (!CoupValide(a.l, a.c, b.l, b.c)) {
            return false;
        }
        grille[b.l][b.c] = grille[a.l][a.c];
        grille[a.l][a.c] = 0;

        if (grille[b.l][b.c] == 3) {
            if (b.l == 0 || b.l == 8 || b.c == 0 || b.c == 8) {
                // gagner blanc
            }
        }

        ListePoint Liste = new ListePoint();
        if (grille[b.l][b.c] == 1) {
            Liste = CaptureBlanc(b);
            for (Point P : Liste.LP) {
                if (grille[P.l][P.c] == 3) {
                    // gagner noir
                }
                if (grille[P.l][P.c] == 1) {
                    grille[P.l][P.c] = 0;
                    nbBlanc--;
                }
            }
        }
        if (grille[b.l][b.c] == 2 || grille[b.l][b.c] == 3) {
            Liste = CaptureNoir(b);
            for (Point P : Liste.LP) {
                if (grille[P.l][P.c] == 2) {
                    grille[P.l][P.c] = 0;
                    nbNoir--;
                    if (nbNoir == 0) {
                        // gagner blanc
                    }
                }
            }
        }

        return true;

    }

    public boolean CaptureRoi(int L, int C) { // mange le roi

        if (grille[L + 1][C] != 1 || grille[L + 1][C] != 4) { // je regarde si le roi peut s'échapper
            return false;
        }
        if (grille[L - 1][C] != 1 || grille[L - 1][C] != 4) {
            return false;
        }
        if (grille[L][C + 1] != 1 || grille[L][C + 1] != 4) {
            return false;
        }
        if (grille[L][C - 1] != 1 || grille[L][C - 1] != 4) {
            return false;
        }
        return true;

    }

    public ListePoint CaptureBlanc(Point P) { // mange les pions blanc
        ListePoint Liste = new ListePoint();

        // manger les pions blancs
        if (P.l < 7) {
            if (grille[P.l + 1][P.c] == 2) { // mange les pions blanc basiques
                if (grille[P.l + 2][P.c] == 1 || grille[P.l + 2][P.c] == 4) {
                    Point tmp = new Point(P.l + 1, P.c);
                    Liste.AddListePoint(tmp);
                }
            }
            if (grille[P.l + 1][P.c] == 3) { // le roi
                if (P.l + 1 <= 5 && P.l + 1 >= 3 && P.c <= 5 && P.c >= 3) { // centre du plateau
                    if (CaptureRoi(P.l + 1, P.c)) {
                        Point tmp = new Point(P.l + 1, P.c);
                        Liste.AddListePoint(tmp);
                    }
                } else {
                    if (grille[P.l + 2][P.c] == 1) { // comme un pion normal
                        Point tmp = new Point(P.l + 1, P.c);
                        Liste.AddListePoint(tmp);
                    }
                }
            }
        }
        if (P.l > 1) {
            if (grille[P.l - 1][P.c] == 2) { // pion normal
                if (grille[P.l - 2][P.c] == 1 || grille[P.l - 2][P.c] == 4) {
                    Point tmp = new Point(P.l - 1, P.c);
                    Liste.AddListePoint(tmp);
                }
            }
            if (grille[P.l - 1][P.c] == 3) { // le roi
                if (P.l - 1 <= 5 && P.l - 1 >= 3 && P.c <= 5 && P.c >= 3) { // centre du plateau
                    if (CaptureRoi(P.l - 1, P.c)) {
                        Point tmp = new Point(P.l - 1, P.c);
                        Liste.AddListePoint(tmp);
                    }
                } else {
                    if (grille[P.l - 2][P.c] == 1) { // comme un pion normal
                        Point tmp = new Point(P.l - 1, P.c);
                        Liste.AddListePoint(tmp);
                    }
                }
            }
        }

        if (P.c < 7) {
            if (grille[P.l][P.c + 1] == 2) { // pion normal
                if (grille[P.l][P.c + 2] == 1 || grille[P.l][P.c + 2] == 4) {
                    Point tmp = new Point(P.l, P.c + 1);
                    Liste.AddListePoint(tmp);
                }
            }
            if (grille[P.l][P.c + 1] == 3) { // le roi
                if (P.l <= 5 && P.l >= 3 && P.c + 1 <= 5 && P.c + 1 >= 3) { // centre du plateau
                    if (CaptureRoi(P.l, P.c + 1)) {
                        Point tmp = new Point(P.l, P.c + 1);
                        Liste.AddListePoint(tmp);
                    }
                } else {
                    if (grille[P.l][P.c + 2] == 1) { // comme un pion normal
                        Point tmp = new Point(P.l, P.c + 1);
                        Liste.AddListePoint(tmp);
                    }
                }
            }
        }
        if (P.c > 1) {
            if (grille[P.l][P.c - 1] == 2) { // Pion normal
                if (grille[P.l][P.c - 2] == 1 || grille[P.l][P.c - 2] == 4) {
                    Point tmp = new Point(P.l, P.c - 1);
                    Liste.AddListePoint(tmp);
                }
            }
            if (grille[P.l][P.c - 1] == 3) { // le roi
                if (P.l <= 5 && P.l >= 3 && P.c - 1 <= 5 && P.c - 1 >= 3) { // centre du plateau
                    if (CaptureRoi(P.l, P.c - 1)) {
                        Point tmp = new Point(P.l, P.c - 1);
                        Liste.AddListePoint(tmp);
                    }
                } else {
                    if (grille[P.l][P.c - 2] == 1) { // comme un pion normal
                        Point tmp = new Point(P.l, P.c - 1);
                        Liste.AddListePoint(tmp);
                    }
                }
            }
        }

        return Liste;
    }

    public ListePoint CaptureNoir(Point P) { // capture les pions noir
        ListePoint Liste = new ListePoint();
        if (P.l < 7) {
            if (grille[P.l + 1][P.c] == 1) {
                if (grille[P.l + 2][P.c] == 2 || grille[P.l + 2][P.c] == 4 || grille[P.l + 2][P.c] == 3) {
                    Point tmp = new Point(P.l + 1, P.c);
                    Liste.AddListePoint(tmp);
                }
            }
        }
        if (P.l > 1) {
            if (grille[P.l - 1][P.c] == 1) {
                if (grille[P.l - 2][P.c] == 2 || grille[P.l - 2][P.c] == 4 || grille[P.l - 2][P.c] == 3) {
                    Point tmp = new Point(P.l - 1, P.c);
                    Liste.AddListePoint(tmp);
                }
            }
        }
        if (P.c < 7) {
            if (grille[P.l][P.c + 1] == 1) {
                if (grille[P.l][P.c + 2] == 2 || grille[P.l][P.c + 2] == 4 || grille[P.l][P.c + 2] == 3) {
                    Point tmp = new Point(P.l, P.c + 1);
                    Liste.AddListePoint(tmp);
                }
            }
        }
        if (P.c > 1) {
            if (grille[P.l][P.c - 1] == 1) {
                if (grille[P.l][P.c - 2] == 2 || grille[P.l][P.c - 2] == 4 || grille[P.l][P.c - 2] == 3) {
                    Point tmp = new Point(P.l, P.c - 1);
                    Liste.AddListePoint(tmp);
                }
            }
        }
        return Liste;
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