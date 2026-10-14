package tablut.model;

public class Coup{
    public Point a,b;
    public int joueur;
    public Coup(Point src,Point dest,int joueur){
        a = src;
        b = dest;
        this.joueur = joueur;
    }
}
