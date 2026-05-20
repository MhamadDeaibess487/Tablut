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
}
