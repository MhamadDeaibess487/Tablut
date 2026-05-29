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
            case 2 -> res = new IAhardV2(jeu,couleur);
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
        TableauBit snapshot = j.transformerGrilleEnLong();
        int joueurAvant = j.getJoueur();
        boolean finiAvant = j.getFini();

        Coup c = calculCoup();
        restaurerEtatSiCalculAModifieLaPartie(snapshot, joueurAvant, finiAvant);

        if (c == null) {
            return;
        }

        j.deplacer(c.a,c.b);
    }

    private void restaurerEtatSiCalculAModifieLaPartie(TableauBit snapshot, int joueurAvant, boolean finiAvant) {
        TableauBit apresCalcul = j.transformerGrilleEnLong();
        if (apresCalcul.compareTo(snapshot) == 0
                && j.getJoueur() == joueurAvant
                && j.getFini() == finiAvant) {
            return;
        }

        j.transformeLongEnGrille(snapshot);
        j.setJouerActif(joueurAvant);
        j.fini = finiAvant;
    }
}
