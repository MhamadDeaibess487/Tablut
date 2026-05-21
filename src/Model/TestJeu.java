package Model;

public class TestJeu {
    public static void main(String[] args) {
        Jeu jeu = new Jeu(); // je suis dans Jeu
        System.out.println("test Configuration draw invalide : TailleBit vide");
        TableauBit TB = new TableauBit(0, 0, 0, 0, 0, 0);
        if (!jeu.draw(TB)) {
            System.out.println("test ok");
        } else
            System.out.println("Problème dans Draw");

        System.out.println("test Configuration draw invalide : Pile Vide");

        TableauBit T2 = move(jeu, 0, 3, 0, 2);

        if (!jeu.draw(T2)) {
            System.out.println("test ok");
        } else
            System.out.println("Problème dans Draw");

        move(jeu, 3, 4, 3, 2);

        move(jeu, 0, 2, 0, 3);
        move(jeu, 3, 2, 3, 4);

        ///

        move(jeu, 0, 3, 0, 1);// coup différent
        move(jeu, 3, 4, 3, 1);

        move(jeu, 0, 1, 0, 3);
        TableauBit T1 = move(jeu, 3, 1, 3, 4);

        System.out.println("test Configuration draw invalide : pas 3 config identiques");
        if (!jeu.draw(T1)) {
            System.out.println("test ok");
        } else
            System.out.println("Problème dans Draw");

        ///
        TableauBit tmp = move(jeu, 0, 3, 0, 0);
        jeu.afficher();

        jeu.transformeLongEnGrille(tmp);// bug sur la fonction ou dans déplacer
        jeu.afficher();

        move(jeu, 3, 4, 3, 1);

        move(jeu, 0, 0, 0, 3);
        move(jeu, 3, 1, 3, 4);

        System.out.println("test Configuration draw valide : 3 config identiques");
        if (jeu.draw(tmp)) {
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