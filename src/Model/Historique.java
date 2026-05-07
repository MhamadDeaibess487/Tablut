package Model;

/*Historique des pions mangés*/
public final class Historique{
    ListePion l;
    int couleur;
    int numCoup;
    
    public Historique(ListePion l,int couleur, int numCoup){
        this.l = l;
        this.couleur = couleur;
        this.numCoup = numCoup;
    }
}