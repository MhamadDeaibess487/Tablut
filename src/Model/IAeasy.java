package Model;

import java.util.Random;

class IAeasy extends IA{
    Random r;
    ListePoint l;
    
    public IAeasy(){
        r = new Random();
        if(couleur == 1)
            l = j.ListePointN;
        else
            l = j.ListePointB;
    }
    
    Coup calculCoup(){
        int i,l2=-1,c2=-1;
        Pion p = new Pion(-1,-1,0);
        while(!j.CoupValide(p.l,p.c,l2,c2)){
            i = r.nextInt(l.totale);
            p = l.getIndex(i);
            if(r.nextInt(2) == 0){
                l2 = p.l;
                c2 = r.nextInt(j.Taille_plateau);
            }
            else{
                c2 = p.c;
                l2 = r.nextInt(j.Taille_plateau);
            }
        }
        return new Coup(p.l,p.c,l2,c2);
    }
}