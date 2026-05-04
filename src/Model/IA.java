package Model;

import java.util.ArrayList;
import java.util.List;


/*Faire la temporisation*/

public abstract class IA{
    int couleur; // 2 blanc, 1 noir
    Jeu j;
    
    public IA(Jeu jeu){
        j = jeu;
        if(j.IA_joueur1)
            couleur = 1;
        else if(j.IA_joueur2)
            couleur = 2;
    }
    
    public IA creer(int lvl){ // lvl : 0 ez, 1 inter, 2 hard
        IA res = null;
        switch(lvl){
            case 0 -> res = new IAeasy();
            case 1 -> res = new IAinter();
            case 2 -> res = new IAhard();
            default -> {
                System.err.println("Erreur creation IA de type "+lvl);
                System.exit(-2);
            }
        }
        return res;
    }
    
    final ArrayList<Point> casesLibre(Point a){
        return null; // A implem
    }
    
    abstract Coup calculCoup();
    
    public void joue(){
        Coup c = calculCoup();
        j.deplacer(c.a,c.b);
    }
}