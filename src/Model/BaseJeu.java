package Model;

import java.util.ArrayList;
// Condition de vitoire:
// 1)Le roi s'échappe. déjà traité V
// 2)L'un des deux joeur n'a plus de coup possible.  fonction PlusDeCoup à utiliser
// 3)Le roi se fait manger. déjà traité V
// 4)Plus assez de pion noir. déjà traité V

//Condition null:
// Répétition 3 coups identiques.
public abstract class BaseJeu {
    protected int[][] grille; // 0 vide, 1 noir, 2 blanc, 3 roi, 4 trone
    protected final TableauBit grilleInit; // Grille initiale serializé
    protected int Joueur_actif; // joueur 1(Noir) ou 2(Blanc)
    protected final int Taille_plateau;
    protected Point trone;
    protected boolean fini = false; // à voir si les blanc ou noir gagnent
    protected ListePoint ListePointB;
    protected ListePoint ListePointN;

    public BaseJeu() {
        Taille_plateau = 9;
        Joueur_actif = 1; // Les noirs commencent
        trone = new Point(4, 4);
        grille = new int[Taille_plateau][Taille_plateau];
        ListePointB = new ListePoint();
        ListePointN = new ListePoint();
        
        grilleInit = new TableauBit(0x20383808002038L, 0x3808, 0x40206c080400000L, 0, 0x10000000000L, 0);
        transformeLongEnGrille(grilleInit);
    }

    abstract public boolean jeuRedondant();

    public int[][] getGrille() {
        return this.grille;
    }
    
    public boolean estFini(){
        return fini;
    }

    public int getJoueur() {
        return Joueur_actif;
    }

    public boolean getFini(){
        return fini;
    }

    public void changerJoueur() {
        Joueur_actif = (Joueur_actif % 2) + 1;
    }

    public ListePoint getListePointB() {
        return this.ListePointB;
    }

    public ListePoint getListePointN() {
        return this.ListePointN;
    }

    public int getTaillePlateau() {
        return this.Taille_plateau;
    }

    public boolean VideEntreDeuxPoints(Point a, Point b) {
        int l1 = a.l;
        int c1 = a.c;
        int l2 = b.l;
        int c2 = b.c;
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

    public boolean CoupValide(Point a, Point b) {// regarde si c'est un coup sur le plateau + coup meme
        // ligne ou colonne + aucun pion entre les deux
        int l1 = a.l;
        int c1 = a.c;
        int l2 = b.l;
        int c2 = b.c;

        if ((l2 < 0) || (l2 > 8) || (c2 < 0) || (c2 > 8)) { // en dehors du tableau
            return false;
        }

        if (grille[a.l][a.c] == 4) { // le trone ne se déplace pas
            return false;
        }

        if (c1 != c2 && l1 != l2) { // déplacement en diagonale
            return false;
        }

        if (c1 == c2 && l1 == l2) {// reste sur la même case
            return false;
        }

        if (c2 == 4 && l2 == 4) {// se déplace vers le trone
            return false;
        }
        if (!VideEntreDeuxPoints(a, b)) {
            return false;
        }

        return true;

    }

    public boolean estCaseLibre(int l, int c) {
        return grille[l][c] == 0;
    }
    
    public TableauBit deplacer(Point a, Point b) {// renvoie faux si coup invalide sinon renvoie vrai avec le déplacement
        if (!estCaseLibre(b.l, b.c) || !CoupValide(a, b)) {
            return null;
        }
        TableauBit save = transformerGrilleEnLong();
        grille[b.l][b.c] = grille[a.l][a.c];
        if (grille[b.l][b.c] == 3 && a.l == 4 && a.c == 4) {
            grille[a.l][a.c] = 4;

        } else
            grille[a.l][a.c] = 0;

        if (grille[b.l][b.c] == 3) { // si le roi s'échappe
            if (b.l == 0 || b.l == 8 || b.c == 0 || b.c == 8) {
                fini = true;
//                System.out.println("blanc ganger");
            }
        }

        ListePion Liste = new ListePion();
        if (grille[b.l][b.c] == 1) { // tour des noirs
            ListePointN.DeplacerListePoint(a, b);
            Liste = CaptureBlanc(b);
            for (Pion p : Liste.LP) {
                Point P = p.p;
                if (grille[P.l][P.c] == 3) { // si mange roi gagner
                    fini = true;
//                    System.out.println("noir ganger");
                }
                if (grille[P.l][P.c] == 2) { // vérification pions blanc (peut être inutile?)
                    ListePointB.SupprimeListePoint(P);
                    grille[P.l][P.c] = 0;
                }
            }
        }
        else if(grille[b.l][b.c] == 2 || grille[b.l][b.c] == 3) { // tour des blancs
            ListePointB.DeplacerListePoint(a, b);
            Liste = CaptureNoir(b);
            for (Pion p : Liste.LP) {
                Point P = p.p;
                grille[P.l][P.c] = 0;
                ListePointN.SupprimeListePoint(P);
                if (ListePointN.totale == 0) {
                    fini = true;
                }
            }
        }
        return save;
    }

    public boolean CaptureRoi(int L, int C) { // roi bloqué des 4 cotés par des pions noirs

        if (grille[L + 1][C] != 1 && grille[L + 1][C] != 4) { // je regarde si le roi peut s'échapper
            return false;
        }
        if (grille[L - 1][C] != 1 && grille[L - 1][C] != 4) {
            return false;
        }
        if (grille[L][C + 1] != 1 && grille[L][C + 1] != 4) {
            return false;
        }
        if (grille[L][C - 1] != 1 && grille[L][C - 1] != 4) {
            return false;
        }
        return true;

    }


    public ListePion CaptureBlanc(Point P) { // Liste des pions blancs qui peuvent être manger
        ListePion Liste = new ListePion();

        if (P.l < 7) {
            if (grille[P.l + 1][P.c] == 2) { // mange les pions blanc basiques
                if (grille[P.l + 2][P.c] == 1 || grille[P.l + 2][P.c] == 4) {
                    Point tmp = new Point(P.l + 1, P.c);
                    Liste.AddListePion(new Pion(tmp,2));
                }
            }
            if (grille[P.l + 1][P.c] == 3) { // le roi
                if (P.l + 1 <= 5 && P.l + 1 >= 3 && P.c <= 5 && P.c >= 3) { // centre du plateau
                    if (CaptureRoi(P.l + 1, P.c)) {
                        Point tmp = new Point(P.l + 1, P.c);
                        Liste.AddListePion(new Pion(tmp,2));
                    }
                } else {
                    if (grille[P.l + 2][P.c] == 1) { // comme un pion normal
                        Point tmp = new Point(P.l + 1, P.c);
                        Liste.AddListePion(new Pion(tmp,2));
                    }
                }
            }
        }
        if (P.l > 1) {
            if (grille[P.l - 1][P.c] == 2) { // pion normal
                if (grille[P.l - 2][P.c] == 1 || grille[P.l - 2][P.c] == 4) {
                    Point tmp = new Point(P.l - 1, P.c);
                    Liste.AddListePion(new Pion(tmp,2));
                }
            }
            if (grille[P.l - 1][P.c] == 3) { // le roi
                if (P.l - 1 <= 5 && P.l - 1 >= 3 && P.c <= 5 && P.c >= 3) { // centre du plateau
                    if (CaptureRoi(P.l - 1, P.c)) {
                        Point tmp = new Point(P.l - 1, P.c);
                        Liste.AddListePion(new Pion(tmp,2));
                    }
                } else {
                    if (grille[P.l - 2][P.c] == 1) { // comme un pion normal
                        Point tmp = new Point(P.l - 1, P.c);
                        Liste.AddListePion(new Pion(tmp,2));
                    }
                }
            }
        }

        if (P.c < 7) {
            if (grille[P.l][P.c + 1] == 2) { // pion normal
                if (grille[P.l][P.c + 2] == 1 || grille[P.l][P.c + 2] == 4) {
                    Point tmp = new Point(P.l, P.c + 1);
                    Liste.AddListePion(new Pion(tmp,2));
                }
            }
            if (grille[P.l][P.c + 1] == 3) { // le roi
                if (P.l <= 5 && P.l >= 3 && P.c + 1 <= 5 && P.c + 1 >= 3) { // centre du plateau
                    if (CaptureRoi(P.l, P.c + 1)) {
                        Point tmp = new Point(P.l, P.c + 1);
                        Liste.AddListePion(new Pion(tmp,2));
                    }
                } else {
                    if (grille[P.l][P.c + 2] == 1) { // comme un pion normal
                        Point tmp = new Point(P.l, P.c + 1);
                        Liste.AddListePion(new Pion(tmp,2));
                    }
                }
            }
        }
        if (P.c > 1) {
            if (grille[P.l][P.c - 1] == 2) { // Pion normal
                if (grille[P.l][P.c - 2] == 1 || grille[P.l][P.c - 2] == 4) {
                    Point tmp = new Point(P.l, P.c - 1);
                    Liste.AddListePion(new Pion(tmp,2));
                }
            }
            if (grille[P.l][P.c - 1] == 3) { // le roi
                if (P.l <= 5 && P.l >= 3 && P.c - 1 <= 5 && P.c - 1 >= 3) { // centre du plateau
                    if (CaptureRoi(P.l, P.c - 1)) {
                        Point tmp = new Point(P.l, P.c - 1);
                        Liste.AddListePion(new Pion(tmp,2));
                    }
                } else {
                    if (grille[P.l][P.c - 2] == 1) { // comme un pion normal
                        Point tmp = new Point(P.l, P.c - 1);
                        Liste.AddListePion(new Pion(tmp,2));
                    }
                }
            }
        }

        return Liste;
    }

    public ListePion CaptureNoir(Point P) { // Liste des pions noirs qui peuvent être manger
        ListePion Liste = new ListePion();
        if (P.l < 7) {
            if (grille[P.l + 1][P.c] == 1) {
                if (grille[P.l + 2][P.c] == 2 || grille[P.l + 2][P.c] == 4 || grille[P.l + 2][P.c] == 3) {
                    Point tmp = new Point(P.l + 1, P.c);
                    Liste.AddListePion(new Pion(tmp,1));
                }
            }
        }
        if (P.l > 1) {
            if (grille[P.l - 1][P.c] == 1) {
                if (grille[P.l - 2][P.c] == 2 || grille[P.l - 2][P.c] == 4 || grille[P.l - 2][P.c] == 3) {
                    Point tmp = new Point(P.l - 1, P.c);
                    Liste.AddListePion(new Pion(tmp,1));
                }
            }
        }
        if (P.c < 7) {
            if (grille[P.l][P.c + 1] == 1) {
                if (grille[P.l][P.c + 2] == 2 || grille[P.l][P.c + 2] == 4 || grille[P.l][P.c + 2] == 3) {
                    Point tmp = new Point(P.l, P.c + 1);
                    Liste.AddListePion(new Pion(tmp,1));
                }
            }
        }
        if (P.c > 1) {
            if (grille[P.l][P.c - 1] == 1) {
                if (grille[P.l][P.c - 2] == 2 || grille[P.l][P.c - 2] == 4 || grille[P.l][P.c - 2] == 3) {
                    Point tmp = new Point(P.l, P.c - 1);
                    Liste.AddListePion(new Pion(tmp,1));
                }
            }
        }
        return Liste;
    }

    public boolean EstBloque(Point P) { // Regarde si un Point est bloqué
        if (P.l + 1 <= 8) {
            if (grille[P.l + 1][P.c] == 0) {
                return false;
            }
        }
        if (P.l - 1 >= 0) {
            if (grille[P.l - 1][P.c] == 0) {
                return false;
            }
        }
        if (P.c + 1 >= 8) {
            if (grille[P.l][P.c + 1] == 0) {
                return false;
            }
        }
        if (P.c - 1 >= 0) {
            if (grille[P.l][P.c - 1] == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean PlusDeCoup(ListePoint Liste) { // renvoie vrai le joueur n'a plus de coup
        for (Point P : Liste.LP) {
            if (!EstBloque(P)) {
                return false;
            }
        }
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

    public ArrayList<Point> listeCasesValides(int l, int c) {
        ArrayList<Point> cases = new ArrayList<>();
        int taille = grille.length;

        Point depart = new Point(l, c);

        // bas
        for (int i = l + 1; i < taille; i++) {
            Point arrivee = new Point(i, c);

            if (grille[i][c] != 0) {
                break;
            }

            if (CoupValide(depart, arrivee)) {
                cases.add(arrivee);
            }
        }

        // haut
        for (int i = l - 1; i >= 0; i--) {
            Point arrivee = new Point(i, c);

            if (grille[i][c] != 0) {
                break;
            }

            if (CoupValide(depart, arrivee)) {
                cases.add(arrivee);
            }
        }

        // droite
        for (int j = c + 1; j < taille; j++) {
            Point arrivee = new Point(l, j);

            if (grille[l][j] != 0) {
                break;
            }

            if (CoupValide(depart, arrivee)) {
                cases.add(arrivee);
            }
        }

        // gauche
        for (int j = c - 1; j >= 0; j--) {
            Point arrivee = new Point(l, j);

            if (grille[l][j] != 0) {
                break;
            }

            if (CoupValide(depart, arrivee)) {
                cases.add(arrivee);
            }
        }
        return cases;
    }

    public ListeCoup CoupJouable(int couleur){
        ListeCoup lCoup = new ListeCoup();
        ListePoint lPoint = couleur == 1?ListePointN:ListePointB;
        for(Point p: lPoint.LP){
            ArrayList<Point> L = listeCasesValides(p.l, p.c);
            for (Point pcases: L){
                lCoup.AddListeCoup(new Coup(p, pcases, Joueur_actif));
            }
        }
        return lCoup;
    }
    public TableauBit transformerGrilleEnLong() {
        TableauBit b = new TableauBit();

        for (int i = 0; i < Taille_plateau; i++) {
            for (int j = 0; j < Taille_plateau; j++) {
                int index = i * Taille_plateau + j; // 0 to 80
                boolean partie1 = index < 64;
                int bitIndex;
                if (partie1){
                    bitIndex = index;
                } else bitIndex = index - 64;

                long masque = 1L << bitIndex;

                if (grille[i][j] == 1) { // noir
                    if (partie1) b.noir1 |= masque;
                    else b.noir2 |= masque;
                }
                else if (grille[i][j] == 2) { // blanc
                    if (partie1) b.blanc1 |= masque;
                    else b.blanc2 |= masque;
                }
                else if (grille[i][j] == 3) { // roi
                    if (partie1) b.roi1 |= masque;
                    else b.roi2 |= masque;
                }
            }
        }

        return b;
    }

    private boolean hasBit(long value, int bitIndex) {
        return (value & (1L << bitIndex)) != 0;
    }

    public final void transformeLongEnGrille(TableauBit b){
        grille = new int[Taille_plateau][Taille_plateau];
        ListePointB.clear();
        ListePointN.clear();
        for (int index = 0; index < Taille_plateau * Taille_plateau; index++) {
            boolean partie1 = index < 64;
            int bitIndex = partie1 ? index : index - 64;

            int l = index / Taille_plateau;
            int c = index % Taille_plateau;

            if (partie1) {
                if (hasBit(b.noir1, bitIndex)) {
                    grille[l][c] = 1;
                    ListePointN.AddListePoint(new Point(l,c));
                } else if (hasBit(b.blanc1, bitIndex)) {
                    grille[l][c] = 2;
                    ListePointB.AddListePoint(new Point(l,c));
                } else if (hasBit(b.roi1, bitIndex)) {
                    grille[l][c] = 3;
                    ListePointB.AddListePoint(new Point(l,c));
                }
            } else {
                if (hasBit(b.noir2, bitIndex)) {
                    grille[l][c] = 1;
                    ListePointN.AddListePoint(new Point(l,c));
                } else if (hasBit(b.blanc2, bitIndex)) {
                    grille[l][c] = 2;
                    ListePointB.AddListePoint(new Point(l,c));
                } else if (hasBit(b.roi2, bitIndex)) {
                    grille[l][c] = 3;
                    ListePointB.AddListePoint(new Point(l,c));
                }
            }
        }

        if (grille[4][4] == 0) {
            grille[4][4] = 4;
        }
    }

    public void setJouerActif(int joueur){
        this.Joueur_actif = joueur;
        System.out.println("Joueur actif de base: " + Joueur_actif);
    }
}