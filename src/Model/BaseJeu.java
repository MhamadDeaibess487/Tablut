
import java.util.Random;

public abstract class BaseJeu{
    protected int[][] grille;
    
    public BaseJeu(){
        grille = new int[9][9];
        grille[0][0] = 1;
    }
    
    public void joue(Point a,Point b){
        grille[b.i][b.j] = grille[a.i][a.j];
        grille[a.i][a.j] = 0;
    }
}