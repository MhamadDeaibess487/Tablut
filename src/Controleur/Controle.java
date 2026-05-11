package Controleur;
import Model.BaseJeu;
import Model.Point;
import Vue.FenetreJeu;
import Vue.GrilleGraphique;
import Vue.MenuGraphique;
import Vue.JoueurActifPanel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static Config.Config.CHEMIN_SAUVEGARDES;

public class Controle {
    BaseJeu Jeu;
    GrilleGraphique Grille;
    int ligneSelectee;
    int colonneSelectee;
    String fichier;
    FenetreJeu fenetreJeu;


    public Controle(BaseJeu Jeu, GrilleGraphique Grille,FenetreJeu fenetreJeu) {

        this.Jeu = Jeu;
        this.Grille = Grille;
        this.ligneSelectee = -1;
        this.colonneSelectee = -1;
    }
    
    //Diviser en deux fonctions ia_blanc, ia_noir
    public void toggleIA() {
    }

    public void reset() {
        ((Model.Jeu)Jeu).nouvellePartie();
        refreshGrid();
    }

    public void undo() {
        ((Model.Jeu)Jeu).annuler();
        refreshGrid();
    }

    public void redo() {
        ((Model.Jeu)Jeu).refaire();
        refreshGrid();
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
        if(ligneSelectee == -1  && colonneSelectee == -1) {
            if (Jeu.estCaseLibre(row,column)) {//pas case vide
                return;
            }
            ligneSelectee = row;
            colonneSelectee = column;
            ArrayList<Point> liste = Jeu.listeCasesValides(ligneSelectee, colonneSelectee);
            Grille.dessiner_valide(liste);
        }else  if(ligneSelectee == row && colonneSelectee == column) {
            ligneSelectee = -1;
            colonneSelectee = -1;
            Grille.effacerCasesValides();
        }else{
            Point p = new Point(ligneSelectee,colonneSelectee);
            Point p1= new Point(row,column);
            Jeu.deplacer(p,p1);
            Grille.effacerCasesValides();
            colonneSelectee=-1;
            ligneSelectee=-1;
        }
        refreshGrid();
    }
}
