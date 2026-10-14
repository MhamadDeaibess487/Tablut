package tablut.controller;

import tablut.model.Jeu;
import tablut.ui.GrilleGraphique;


public abstract class ControleAbstract {
    Jeu jeu;
    GrilleGraphique Grille;

    public ControleAbstract(Jeu jeu, GrilleGraphique Grille) {
        this.jeu = jeu;
        this.Grille = Grille;
    }

    public abstract void undo();

    public void refreshGrid(){
        Grille.repaint();
    }
    
    // J1 = 1; J2 = 2
    public void setPseudo(int i,String tag){
        jeu.setPseudo(i,tag);
    }
    
    public String getPseudo(int i){
        return jeu.getPseudo(i);
    }
    
    public boolean isLoaded(){
        return jeu.isLoaded;
    }
    
    public abstract void handleGridClick(int row, int column);

    public Jeu getJeu() {
        return jeu;
    }

    public void setIaNoir(boolean active) {}

    public void setIaBlanc(boolean active) {}

    public void setNiveauIANoir(int niveau) {}

    public void setNiveauIABlanc(int niveau) {}
}
