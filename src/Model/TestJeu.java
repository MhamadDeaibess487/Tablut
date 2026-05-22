package Model;

public class TestJeu {
    public static void main(String[] args) {
        // config initale qui a besoin que de 2 coups identiques
        Jeu jeu = new Jeu(); // je suis dans Jeu
        System.out.println("test Configuration initiale draw invalide : JeuInitiale vide");
        if (!jeu.draw(jeu)) {
            System.out.println("test ok");
        } else
            System.out.println("Problème dans Draw");

        move(jeu, 0, 3, 0, 2);
        move(jeu, 3, 4, 3, 2);

        move(jeu, 0, 2, 0, 3);
        move(jeu, 3, 2, 3, 4);

        System.out.println("test Configuration initiale draw invalide : pas 3 config identiques");
        if (!jeu.draw(jeu)) {
            System.out.println("test ok");
        } else
            System.out.println("Problème dans Draw");

        ///

        move(jeu, 0, 3, 0, 1);// coup différent
        move(jeu, 3, 4, 3, 1);

        move(jeu, 0, 1, 0, 3);
        move(jeu, 3, 1, 3, 4);

        System.out.println("test Configuration initiale draw valide : 3 config identiques");
        if (jeu.draw(jeu)) {
            System.out.println("test ok");
        } else
            System.out.println("Problème dans Draw");

        /// Cas de base
        Jeu base = new Jeu(); // je suis dans Jeu
        move(base, 3, 0, 2, 0);
        move(base, 3, 4, 3, 2);

        move(base, 2, 0, 1, 0);
        move(base, 3, 2, 3, 1);

        System.out.println("test Configuration draw invalide : pas 3 config identiques");
        if (!base.draw(base)) {
            System.out.println("test ok");
        } else
            System.out.println("Problème dans Draw");

        ///

        move(base, 1, 0, 2, 0);// coup différent
        move(base, 3, 1, 3, 2);

        System.out.println("test Configuration draw invalide : 2 config identiques");
        if (!base.draw(base)) {
            System.out.println("test ok");
        } else
            System.out.println("Problème dans Draw");

        move(base, 2, 0, 0, 0);
        move(base, 3, 2, 3, 0);

        move(base, 3, 0, 3, 2);
        move(base, 0, 0, 2, 0);

        System.out.println("test Configuration draw valide : 3 config identiques");
        if (base.draw(base)) {
            System.out.println("test ok");
        } else
            System.out.println("Problème dans Draw");

        move(base, 2, 0, 0, 0);
        move(base, 3, 2, 3, 0);

        move(base, 0, 0, 2, 0);

        System.out.println("test Configuration draw invalide : après manger un pion");
        if (!base.draw(base)) {
            System.out.println("test ok");
        } else
            System.out.println("Problème dans Draw");

    }

    public static TableauBit move(Jeu jeu, int l1, int c1, int l2, int c2) {
        Point a = new Point(l1, c1);
        Point b = new Point(l2, c2);
        return jeu.deplacer(a, b);
    }

    public static void afficherGrille(int[][] grille) {
        for (int l = 0; l < 9; l++) {
            for (int c = 0; c < 9; c++) {
                System.out.print(grille[l][c] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}