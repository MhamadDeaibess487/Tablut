package Model;

import java.util.Random;

class IArandom extends IA{
    Random r;
    ListePoint l;
    
    public IArandom(Jeu j,int couleur){
        r = new Random();
        if(couleur == 1)
            l = j.ListePointN;
        else
            l = j.ListePointB;
    }
    
    Coup calculCoup(){
        int i;
        Point a = new Point(-1,-1);
        Point b = new Point(-1,-1);
        while(!j.CoupValide(a,b)){
            i = r.nextInt(l.totale);
            a = l.getPoint(i);
            if(r.nextInt(2) == 0){
                b.l = a.l;
                b.c = r.nextInt(j.Taille_plateau);
            }
            else{
                b.c = a.c;
                b.l = r.nextInt(j.Taille_plateau);
            }
        }
        return new Coup(a,b,couleur);
    }

    // @Override
    // public int heuristique(Jeu copie) {
    //     // heuristique basique pour IA random
    //     return 0;
    // }
}