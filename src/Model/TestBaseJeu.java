package Model;

public class TestBaseJeu {
    public static void main(String[] args) {
        BaseJeu jeu = new Jeu();
        vider(jeu);
        // test VideEntreDeuxPoint
        jeu.grille[4][4] = 3;
        jeu.grille[2][4] = 1;
        jeu.grille[2][2] = 1;
        jeu.grille[5][4] = 2;
        jeu.grille[4][7] = 2;
        jeu.afficher();

        System.out.println();

        System.out.println("test VideEntreDeuxPoint horizontal valide : après un pion: 4 7 4 5");
        if (vide(jeu, 4, 7, 4, 5) == false)
            System.out.println("Problème dans VideEnrteDeuxPoints -> horizontal: coup autorisé");
        else
            System.out.println("Test ok");

        System.out.println();

        System.out.println("test VideEntreDeuxCoup horizontal invalide : après un pion: 4 4 4 8");
        if (vide(jeu, 4, 4, 4, 8) == true)
            System.out.println("Problème dans VideEnrteDeuxPoints -> horizontal: coup non autorisé");
        else
            System.out.println("Test ok");

        System.out.println("");

        System.out.println("test VideEntreDeuxCoup vertical valide : après un pion: 2 4 3 4");
        if (vide(jeu, 2, 4, 3, 4) == false)
            System.out.println("Problème dans VideEnrteDeuxPoints -> vertical: coup autorisé");
        else
            System.out.println("Test ok");

        System.out.println("");

        System.out.println("test VideEntreDeuxCoup vertical invalide : après un pion: 5 4 2 4");
        if (vide(jeu, 5, 4, 2, 4) == true)
            System.out.println("Problème dans VideEnrteDeuxPoints -> vertical: coup non autorisé");
        else
            System.out.println("Test ok");

        System.out.println("");

        // test Coup valide

        vider(jeu);

        jeu.grille[4][4] = 4;
        jeu.grille[2][4] = 2;

        jeu.afficher();
        System.out.println("");

        System.out.println("test CoupValide valide dans le tableau: 2 4 3 4");
        if (valide(jeu, 2, 4, 3, 4) == false)
            System.out.println("Problème dans CoupValide Dans le tableau: coup autorisé");
        else
            System.out.println("Test ok");

        System.out.println("");

        System.out.println("test CoupValide invalide dans le tableau: 2 4 9 4");
        if (valide(jeu, 2, 4, 9, 4) == true)
            System.out.println("Problème dans CoupValide hors le tableau: coup non autorisé");
        else
            System.out.println("Test ok");

        System.out.println("");

        System.out.println("test CoupValide invalide déplacement du trone: 4 4 6 4");
        if (valide(jeu, 4, 4, 6, 4) == true)
            System.out.println("Problème dans CoupValide avec le trone: coup non autorisé");
        else
            System.out.println("Test ok");

        System.out.println("");

        System.out.println("test CoupValide invalide vers le tableau: 2 4 4 4");
        if (valide(jeu, 2, 4, 4, 4) == true)
            System.out.println("Problème dans CoupValide vers le trone: coup non autorisé");
        else
            System.out.println("Test ok");

        System.out.println("");

        System.out.println("test CoupValide invalide diagonale: 2 4 3 3");
        if (valide(jeu, 2, 4, 3, 3) == true)
            System.out.println("Problème dans CoupValide diagonale: coup non autorisé");
        else
            System.out.println("Test ok");

        System.out.println("");

        System.out.println("test CoupValide invalide reste sur place: 2 4 2 4");
        if (valide(jeu, 2, 4, 2, 4) == true)
            System.out.println("Problème dans CoupValide reste sur place: coup non autorisé");
        else
            System.out.println("Test ok");

        System.out.println("");

        // Test estCaseLibre
        System.out.println("test estCaseLibre libre 2 5 ");

        if (jeu.estCaseLibre(2, 5) == false)
            System.out.println("Problème dans estCaseLibre: cas libre");
        else
            System.out.println("Test ok");

        System.out.println("");

        System.out.println("test estCaseLibre nonlibre 2 4 ");

        if (jeu.estCaseLibre(2, 4) == true)
            System.out.println("Problème dans estCaseLibre: case non libre");
        else
            System.out.println("Test ok");

        System.out.println("");

        // test CaptureRoi

        vider(jeu);

        jeu.grille[4][3] = 3;
        jeu.grille[4][2] = 1;
        jeu.grille[3][3] = 1;
        jeu.grille[5][3] = 1;
        jeu.grille[4][4] = 4;

        jeu.afficher();

        System.out.println("test CaptureRoi valide 4 4  ");

        if (jeu.CaptureRoi(4, 3) == false)
            System.out.println("Problème dans CaptureRoi : roi capturer ");
        else
            System.out.println("Test ok");

        System.out.println("");

        jeu.grille[5][3] = 2;
        jeu.afficher();

        System.out.println("test CaptureRoi invalide 4 4  ");

        if (jeu.CaptureRoi(4, 3) == true)
            System.out.println("Problème dans CaptureRoi: roi non capturer ");
        else
            System.out.println("Test ok");

        System.out.println("");

        // test CaptureNoir

        vider(jeu);
        jeu.grille[1][0] = 1;
        jeu.grille[3][0] = 1;
        jeu.grille[0][0] = 2;
        jeu.grille[4][0] = 2;
        jeu.grille[0][1] = 2;
        jeu.grille[1][1] = 1;
        jeu.grille[2][0] = 2;
        jeu.grille[3][1] = 1;
        jeu.grille[4][1] = 2;
        jeu.afficher();

        System.out.println("test CaptureNoir bord du plateau valide 2 0  ");
        mangerN(jeu, 2, 0);
        if (jeu.grille[1][0] != 0 && jeu.grille[3][0] != 0)
            System.out.println("Problème dans CaptureNoir: bord du plateau ");
        else
            System.out.println("Test ok");

        System.out.println("");
        System.out.println("après avoir manger");
        jeu.afficher();

        System.out.println("nouvelle grille pour Test CaptureNoir");
        jeu.grille[2][0] = 0;
        jeu.grille[2][1] = 2;
        jeu.afficher();
        mangerN(jeu, 2, 1);
        System.out.println("test CaptureNoir valide 2 1  ");

        if (jeu.grille[1][1] != 0 && jeu.grille[3][1] != 0)
            System.out.println("Problème dans CaptureNoir: les pions doivent etre manger ");
        else
            System.out.println("Test ok");
        System.out.println("");
        System.out.println("après avoir manger");
        jeu.afficher();

        // test CaptureBlanc
        vider(jeu);
        jeu.grille[1][0] = 2;
        jeu.grille[3][0] = 2;
        jeu.grille[0][0] = 1;
        jeu.grille[4][0] = 1;
        jeu.grille[0][1] = 1;
        jeu.grille[1][1] = 2;
        jeu.grille[2][0] = 1;
        jeu.grille[3][1] = 2;
        jeu.grille[4][1] = 1;
        jeu.afficher();
        System.out.println("test CaptureBlanc bord du plateau valide 2 0  ");
        mangerB(jeu, 2, 0);
        if (jeu.grille[1][0] != 0 && jeu.grille[3][0] != 0)
            System.out.println("Problème dans CaptureBlanc: bord du plateau ");
        else
            System.out.println("Test ok");

        System.out.println("");
        System.out.println("après avoir manger");
        jeu.afficher();

        System.out.println("");
        System.out.println("nouvelle grille pour Test CaptureBlanc Pion");
        jeu.grille[2][0] = 0;
        jeu.grille[2][1] = 1;
        jeu.afficher();

        System.out.println("test CaptureBlanc valide 2 1  ");

        mangerB(jeu, 2, 1);
        if (jeu.grille[1][1] != 0 && jeu.grille[3][1] != 0)
            System.out.println("Problème dans CaptureBlanc: les pions doivent etre manger ");
        else
            System.out.println("Test ok");

        System.out.println("");
        System.out.println("après avoir manger");
        jeu.afficher();

        vider(jeu);
        jeu.grille[4][3] = 3;
        jeu.grille[4][4] = 4;
        jeu.grille[4][2] = 1;
        jeu.grille[3][3] = 1;
        jeu.grille[5][3] = 1;
        System.out.println("nouvelle grille pour Test CaptureBlanc Roi");
        jeu.afficher();
        System.out.println("test CaptureBlanc Roi centre du plateau valide 3 3  ");

        mangerB(jeu, 3, 3);

        if (jeu.grille[4][3] != 0)
            System.out.println("Problème dans CaptureBlanc: le roi doit etre manger ");
        else
            System.out.println("Test ok");
        jeu.afficher();

        System.out.println("");

        jeu.grille[5][3] = 2;
        jeu.grille[4][3] = 3;
        System.out.println("nouvelle grille pour Test CaptureBlanc Roi");
        jeu.afficher();
        System.out.println("test CaptureBlanc Roi centre du plateau invalide 3 3  ");

        if (jeu.grille[4][3] == 0)
            System.out.println("Problème dans CaptureBlanc: le roi ne doit pas etre manger ");
        else
            System.out.println("Test ok");
        System.out.println("");

        vider(jeu);
        jeu.grille[0][0] = 1;
        jeu.grille[1][0] = 3;
        jeu.grille[2][0] = 1;
        System.out.println("nouvelle grille pour Test CaptureBlanc Roi: cas de bord");
        jeu.afficher();
        System.out.println("test CaptureBlanc Roi bord du plateau valide 0 0  ");
        mangerB(jeu, 0, 0);
        if (jeu.grille[1][0] != 0)
            System.out.println("Problème dans CaptureBlanc: le roi doit etre manger ");
        else
            System.out.println("Test ok");
        System.out.println("");

        System.out.println("après avoir manger");
        jeu.afficher();

        jeu.grille[2][0] = 2;
        jeu.grille[1][0] = 3;

        System.out.println("nouvelle grille pour Test CaptureBlanc Roi: cas de bord");
        jeu.afficher();
        System.out.println("test CaptureBlanc Roi bord du plateau invalide 0 0  ");
        mangerB(jeu, 0, 0);
        if (jeu.grille[1][0] == 0)
            System.out.println("Problème dans CaptureBlanc: le roi ne doit doit etre manger ");
        else
            System.out.println("Test ok");
        System.out.println("");

        System.out.println("après avoir manger");
        jeu.afficher();

        // Test deplacer
        vider(jeu);
        jeu.grille[4][4] = 3;

        System.out.println("nouvelle grille pour deplacer ");
        jeu.afficher();
        System.out.println("test création de trone  4 4 3 4");
        move(jeu, 4, 4, 3, 4);
        if (jeu.grille[4][4] != 4)
            System.out.println("Problème dans deplacer trone");
        else
            System.out.println("Test ok");
        System.out.println("");

        System.out.println("après déplacement");
        jeu.afficher();

        System.out.println("test déplacement valide  3 4 3 3");
        move(jeu, 3, 4, 3, 3);
        if (jeu.grille[3][4] != 0 || jeu.grille[3][3] == 0)
            System.out.println("Problème dans deplacer");
        else
            System.out.println("Test ok");
        System.out.println("");

        System.out.println("après déplacement");
        jeu.afficher();

        System.out.println("test déplacement invalide  3 3 2 2");
        move(jeu, 3, 3, 2, 2);
        if (jeu.grille[3][3] == 0)
            System.out.println("Problème dans deplacer");
        else
            System.out.println("Test ok");
        System.out.println("");

        // Test EstBloque
        vider(jeu);
        jeu.grille[0][0] = 1;
        jeu.grille[1][1] = 1;
        jeu.grille[0][1] = 2;
        jeu.grille[2][1] = 2;
        jeu.grille[1][0] = 2;
        jeu.grille[1][2] = 2;

        jeu.grille[6][1] = 1;
        jeu.grille[5][1] = 2;
        jeu.grille[7][1] = 2;
        jeu.grille[6][0] = 2;

        System.out.println("nouvelle grille pour estbloque ");
        jeu.afficher();

        System.out.println("test EstBloque 1 1");
        if (Block(jeu, 1, 1) == false)
            System.out.println("Problème dans EstBloque : doit etre true ");
        else
            System.out.println("Test ok");
        System.out.println("");

        System.out.println("test EstBloque 6 1");
        if (Block(jeu, 6, 1) == true)
            System.out.println("Problème dans EstBloque : doit etre false ");
        else
            System.out.println("Test ok");
        System.out.println("");

        System.out.println("test EstBloque 0 0: cas de bord");
        if (Block(jeu, 0, 0) == false)
            System.out.println("Problème dans EstBloque : doit etre true ");
        else
            System.out.println("Test ok");
        System.out.println("");

        // test PlusDecoup
        vider(jeu);
        System.out.println("nouvelle grille test PlusDecoup:");
        jeu.grille[0][0] = 1;
        jeu.grille[0][1] = 2;
        jeu.grille[1][0] = 2;
        jeu.grille[1][2] = 2;
        jeu.grille[2][1] = 4;
        jeu.afficher();
        ListePoint test = new ListePoint();
        System.out.println("test PlusDecoup sur les noirs: PointA 0 0: cas de bord");
        if (!PDC(jeu, test, 0, 0)) {
            System.out.println("Problème Plus de Coup doit être true");
        } else
            System.out.println("test ok");

        jeu.grille[1][0] = 0;
        System.out.println("");
        System.out.println("nouvelle grille test PlusDecoup:");
        jeu.afficher();
        System.out.println("test PlusDecoup sur les noirs : PointA 0 0: cas de bord");
        if (PDC(jeu, test, 0, 0)) {
            System.out.println("Problème Plus de Coup doit être false");
        } else
            System.out.println("test ok");

        vider(jeu);
        jeu.grille[3][3] = 1;
        jeu.grille[3][4] = 1;

        ListePoint tmp = new ListePoint();
        System.out.println("");
        System.out.println("Nouvelle grille pour plus de coup");
        jeu.afficher();
        System.out.println("test PlusDecoup sur les noirs: PointA 3 3 et PointB 3 4");
        if (PDC(jeu, tmp, 3, 3) || PDC(jeu, tmp, 3, 4)) {
            System.out.println("Problème Plus de Coup doit être false");
        } else
            System.out.println("test ok");
        System.out.println("");

        jeu.grille[3][5] = 2;
        jeu.grille[3][2] = 2;
        jeu.grille[2][4] = 2;
        jeu.grille[4][4] = 2;
        jeu.grille[2][3] = 2;
        jeu.grille[4][3] = 2;

        System.out.println("Nouvelle grille pour plus de coup");
        jeu.afficher();
        System.out.println("test PlusDecoup sur les noirs: PointA 3 3 et PointB 3 4");
        if (!PDC(jeu, tmp, 3, 3) || !PDC(jeu, tmp, 3, 4)) {
            System.out.println("Problème Plus de Coup doit être true");
        } else
            System.out.println("test ok");

    }

    public static TableauBit move(BaseJeu jeu, int l1, int c1, int l2, int c2) {
        Point a = new Point(l1, c1);
        Point b = new Point(l2, c2);
        return jeu.deplacer(a, b);
    }

    public static boolean vide(BaseJeu jeu, int l1, int c1, int l2, int c2) {
        Point a = new Point(l1, c1);
        Point b = new Point(l2, c2);
        return jeu.VideEntreDeuxPoints(a, b);
    }

    public static boolean valide(BaseJeu jeu, int l1, int c1, int l2, int c2) {
        Point a = new Point(l1, c1);
        Point b = new Point(l2, c2);
        return jeu.CoupValide(a, b);
    }

    public static boolean Block(BaseJeu jeu, int l, int c) {
        Point A = new Point(l, c);
        return jeu.EstBloque(A);
    }

    public static boolean PDC(BaseJeu jeu, ListePoint Liste, int l, int c) {
        Point A = new Point(l, c);
        Liste.AddListePoint(A);
        return jeu.PlusDeCoup(Liste);
    }

    public static void mangerN(BaseJeu j, int l, int c) {
        ListePion Liste = new ListePion();
        Point M = new Point(l, c);
        Liste = j.CaptureNoir(M);
        for (Pion p : Liste.LP) {
            Point P = p.p;
            j.grille[P.l][P.c] = 0;
        }
    }

    public static void mangerB(BaseJeu j, int l, int c) {
        ListePion Liste = new ListePion();
        Point M = new Point(l, c);
        Liste = j.CaptureBlanc(M);
        for (Pion p : Liste.LP) {
            Point P = p.p;
            j.grille[P.l][P.c] = 0;
        }
    }

    public static void vider(BaseJeu j) {
        for (int i = 0; i < j.getTaillePlateau(); i++) {
            for (int k = 0; k < j.getTaillePlateau(); k++) {
                j.grille[i][k] = 0;

            }
        }
    }

}