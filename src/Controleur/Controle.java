package Controleur;
import Model.BaseJeu;
import Model.Pion;
import Model.Point;
import Vue.GrilleGraphique;

import java.awt.*;
import java.util.ArrayList;

public class Controle {
    BaseJeu Jeu;
    GrilleGraphique Grille;
    int ligneSelectee;
    int colonneSelectee;

    public Controle(BaseJeu Jeu, GrilleGraphique Grille) {
        this.Jeu = Jeu;
        this.Grille = Grille;
        this.ligneSelectee = -1;
        this.colonneSelectee = -1;

    }

    public void toggleIA() {
    }

    public void reset() {
    }

    public void undo() {
    }

    public void redo() {
    }

    public void quit() {
        //sauvegarder si possible
        System.exit(0);
    }

    public void harder() {
    }

    public void easier() {
    }

    public void increaseGridSize() {
    }

    public void decreaseGridSize() {
    }
    public void refreshGrid() {

        Grille.repaint();
    }
    public void handleGridClick(int row, int column) {
        int [][]grille_case = Jeu.getGrille();

        if(ligneSelectee == -1  && colonneSelectee == -1) {
            if (grille_case[row][column] == 0) {//pas case vide
                return;
            }
            ligneSelectee = row;
            colonneSelectee = column;
            ArrayList<Point> liste = Jeu.listeCasesValides(ligneSelectee, colonneSelectee);
            Grille.dessiner_valide(liste);
            refreshGrid();
        }else  if(ligneSelectee == row && colonneSelectee == column) {
            ligneSelectee = -1;
            colonneSelectee = -1;
            Grille.effacerCasesValides();
            refreshGrid();
        }else{
            int [][] grille = Jeu.getGrille();
            Point p = new Point(ligneSelectee,colonneSelectee);
            Point p1= new Point(row,column);
            Jeu.deplacer(p,p1);
            Grille.effacerCasesValides();
            colonneSelectee=-1;
            ligneSelectee=-1;
            refreshGrid();
        }

    }
}
