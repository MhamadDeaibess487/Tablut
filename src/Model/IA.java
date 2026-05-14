package Model;

/*Faire la temporisation*/

public abstract class IA{
    protected Jeu j;
    protected int couleur;
    
    
    public static IA creer(Jeu jeu,int lvl,int couleur){ // lvl : 0 ez, 1 inter, 2 hard
        IA res = null;
        switch(lvl){
            case 0 -> res = new IAeasy(jeu,couleur);
            case 1 -> res = new IAinter(jeu, couleur);
//            case 2 -> res = new IAhard(jeu);
            default -> {
                System.err.println("Erreur creation IA de type "+lvl);
                System.exit(-2);
            }
        }
        if(res != null){
            res.j = jeu;
            res.couleur = couleur;
        }
        return res;
    }
     
    abstract Coup calculCoup();
    
    public void joue(){
        Coup c = calculCoup();
        j.deplacer(c.a,c.b);
    }
}