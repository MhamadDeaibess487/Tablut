package Model;

public class Coup{
    Point a,b;
    int joueur;
    Coup(Point src,Point dest,int joueur){
        a = src;
        b = dest;
        this.joueur = joueur;
    }
}