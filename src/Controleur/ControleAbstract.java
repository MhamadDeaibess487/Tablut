package Controleur;

import Model.BaseJeu;
import Vue.GrilleGraphique;


public abstract class ControleAbstract {
    BaseJeu Jeu;
    GrilleGraphique Grille;

    public ControleAbstract(BaseJeu Jeu, GrilleGraphique Grille) {
        this.Jeu = Jeu;
        this.Grille = Grille;
    }

    public abstract void undo();

    public void refreshGrid(){
        Grille.repaint();
    }
    
    public abstract void handleGridClick(int row, int column);

    public BaseJeu getJeu() {
        return Jeu;
    }

    public void setIaNoir(boolean active) {}

    public void setIaBlanc(boolean active) {}

    public void setNiveauIANoir(int niveau) {}

    public void setNiveauIABlanc(int niveau) {}
}
