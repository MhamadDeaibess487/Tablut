package Controleur;

import Model.BaseJeu;
import Vue.GrilleGraphique;


public abstract class ControleAbstract {
    BaseJeu jeu;
    GrilleGraphique grille;

    public ControleAbstract(BaseJeu Jeu, GrilleGraphique Grille) {
        this.jeu = Jeu;
        this.grille = Grille;
    }

    public abstract void undo();

    public void refreshGrid(){
        grille.repaint();
    }
    
    public abstract void handleGridClick(int row, int column);
}
