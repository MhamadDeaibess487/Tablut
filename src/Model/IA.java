package Model;

import java.util.ArrayList;
import java.util.List;

public abstract class IA{
    int couleur; // 2 blanc, 1 noir
    Jeu j;
    ListePion noir;
    ListePion blanc;
    
    public IA(Jeu jeu){
        j = jeu;
        if(j.IA_joueur1)
            couleur = 1;
        else if(j.IA_joueur2)
            couleur = 2;
        int[][] g = j.getGrille();
        noir = new ListePion();
        blanc = new ListePion();
        for(int i=0;i<j.Taille_plateau;i++){
            for(int p = 0;p<j.Taille_plateau;p++){
                switch(g[i][j]){
                    case 1:
                        noir.AddListePion(new Pion(i,p,1));
                        break;
                    case 2:
                        blanc.AddListePion(new Pion(i,p,2));
                        break;
                    case 3:
                        blanc.AddListePion(new Pion(i,p,3));
                        break;
                    case 4:
                        Pion a = new Pion(i,p,4);
                        noir.AddListePion(a);
                        blanc.AddListePion(a);
                        break;
                    default:
                        break;
                }
            }
        }
        
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
    
    public void joue(Coup c){
        j.deplacer(c.a,c.b);
    }
}