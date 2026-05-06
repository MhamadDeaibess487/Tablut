package Model;

public class TestBaseJeu {
    public static void main(String[] args) {
        BaseJeu jeu = new Jeu();
        vider(jeu);
        jeu.grille[8][0] = 1;
        jeu.grille[0][1] = 2;
        jeu.grille[0][8] = 1;
        jeu.grille[4][4] = 3;
        jeu.afficher();

        // tester déplacer
        System.out.println("test déplacement valide l1:8 c1 : 0 l2 : 0 c2 :0");
        if (!move(jeu, 8, 0, 0, 0))
            System.out.println("Erreur Déplacment: doit se déplacer");

        jeu.afficher();

        System.out.println("test déplacement invalide : diagonale : 0 0 1 1");
        if (move(jeu, 0, 0, 1, 1))
            System.out.println("Erreur Déplacement, ne doit pas se déplacer en diagonale");
        ;
        jeu.afficher();

        System.out.println("test capture valide: 0 8 0 2");
        move(jeu, 0, 8, 0, 2);
        jeu.afficher();

        System.out.println("test creation trone valide : 4 4 3 4");
        move(jeu, 4, 4, 3, 4);
        jeu.afficher();

        System.out.println("test deplacement sur trone invalide 3 4 4 4");
        if (move(jeu, 3, 4, 4, 4))
            System.out.println("NE doit pas se déplacer sur le trone");
        ;
        jeu.afficher();

        System.out.println("test deplacement du trone invalide 4 4 4 5");
        if (move(jeu, 4, 4, 4, 5))
            System.out.println("le trone ne peut pas se déplacer");
        ;
        jeu.afficher();
        System.out.println();

        // test capture
        System.out.println("nouvelle grille test capture:");
        vider(jeu);
        jeu.grille[4][4] = 3;
        move(jeu, 4, 4, 3, 4);
        jeu.grille[3][3] = 1;
        jeu.grille[3][5] = 1;
        jeu.grille[0][4] = 1;
        jeu.afficher();

        System.out.println("test capture roi valide centre 0 4 2 4");
        move(jeu, 0, 4, 2, 4);
        jeu.afficher();

        System.out.println("nouvelle grille test capture:");
        vider(jeu);

        jeu.grille[2][2] = 3;
        jeu.grille[2][1] = 1;
        jeu.grille[2][4] = 1;
        jeu.grille[1][3] = 2;
        jeu.grille[7][3] = 2;
        jeu.afficher();

        System.out.println("test capture roi valide hors cenrte 2 4 2 3");
        move(jeu, 2, 4, 2, 3);
        jeu.afficher();

        System.out.println("test capture Noir valide 7 3 3 3");
        move(jeu, 7, 3, 3, 3);
        jeu.afficher();

        // test VideEntreDeuxPoint
        vider(jeu);
        System.err.println();
        System.out.println("nouvelle grille test VideEntreDeuxPoint:");
        jeu.grille[0][0] = 1;
        jeu.grille[0][1] = 1;
        jeu.grille[2][0] = 2;
        jeu.afficher();

        System.out.println("test déplacement horizontal invalide : après un pion: 0 0 0 2");
        if (move(jeu, 0, 0, 0, 2))
            System.out.println("ne peut pas passer à travers un pion");
        ;
        jeu.afficher();

        System.out.println("test déplacement vertical invalide : après un pion: 0 0 2 3");
        if (move(jeu, 0, 0, 2, 3))
            System.out.println("ne peut pas passer à travers un pion");
        ;
        jeu.afficher();

        // test PlusDecoup
        vider(jeu);
        System.err.println();
        System.out.println("nouvelle grille test PlusDecoup:");
        Point a = new Point(0, 0);
        jeu.grille[0][0] = 1;
        jeu.grille[0][1] = 2;
        jeu.grille[1][0] = 2;
        jeu.grille[1][1] = 1;
        jeu.grille[1][2] = 2;
        jeu.grille[2][1] = 4;
        jeu.afficher();
        ListePoint test = new ListePoint();
        test.AddListePoint(a);

        a = new Point(1, 1);
        test.AddListePoint(a);

        System.out.println("test PlusDecoup sur les noirs:");
        if (!jeu.PlusDeCoup(test)) {
            System.out.println("test Plus de Coup invalide");
        } else
            System.out.println("test Plus de Coup valide");

    }

    public static boolean move(BaseJeu jeu, int l1, int c1, int l2, int c2) {
        Point a = new Point(l1, c1);
        Point b = new Point(l2, c2);
        return jeu.deplacer(a, b);
    }

    public static void vider(BaseJeu j) {
        for (int i = 0; i < j.getTaillePlateau(); i++) {
            for (int k = 0; k < j.getTaillePlateau(); k++) {
                if (j.grille[i][k] == 0) {
                    continue;
                }
                if (j.grille[i][k] == 1) {
                    Point a = new Point(i, k);

                    j.ListePointN = j.ListePointN.SupprimeListePoint(j.ListePointN, a);
                    j.grille[i][k] = 0;

                } else {
                    Point a = new Point(i, k);
                    j.ListePointB.SupprimeListePoint(j.ListePointB, a);
                    j.grille[i][k] = 0;

                }

            }
        }
    }

}
