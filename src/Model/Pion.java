package Model;
public class Pion {
    int c, l;
    int t;// 0 vide, 1 noir, 2 blanc, 3 roi , 4 trone

    public Pion(int ligne, int colonne, int type) {
        this.c = colonne;
        this.l = ligne;
        this.t = type;
    }
    public int getLigne() {
        return l;
    }
    public int getColonne() {
        return c;
    }
    public int getType() {
        return t;
    }

}