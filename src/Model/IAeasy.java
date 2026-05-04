package Model;

import java.util.Random;

class IAeasy extends IA{
    Random r;
    ListePion listeP;
    
    public IAeasy(){
        r = new Random();
        if(couleur == 1)
            listeP = blanc;
        else
            listeP = noir;
    }
    
    Coup calculCoup(){
        int i,l2=-1,c2=-1;
        Pion p = new Pion(-1,-1,0);
        while(!j.CoupValide(p.l,p.c,l2,c2)){
            i = r.nextInt(listeP.totale);
            p = listeP.getIndex(i);
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