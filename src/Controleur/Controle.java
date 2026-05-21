package Controleur;

import Model.Point;
import Model.BaseJeu;
import Vue.FenetreJeu;
import Vue.GrilleGraphique;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import static Config.Config.CHEMIN_SAUVEGARDES;

public class Controle extends ControleAbstract{
    String fichier;
    FenetreJeu fenetreJeu;
    int ligneSelectee;
    int colonneSelectee;

    public Controle(BaseJeu Jeu, GrilleGraphique Grille,FenetreJeu fenetreJeu) {
        super(Jeu,Grille);
        this.fenetreJeu = fenetreJeu;
        this.fichier = "";
        this.ligneSelectee = -1;
        this.colonneSelectee = -1;
    }

    public void toggleIA_blanc() {
    }
    
    public void toggleIA_noir() {
    }

    public void reset() {
        ((Model.Jeu)jeu).nouvellePartie();
        refreshGrid();
    }

    @Override
    public void undo() {
        if(((Model.Jeu)jeu).annuler())
            refreshGrid();
    }

    public void redo() {
        if(((Model.Jeu)jeu).refaire())
            refreshGrid();
    }
    
    public void harder() {
    }

    public void easier() {
    }
    
    @Override
    public void handleGridClick(int row, int column){
        int [][]grille_case = jeu.getGrille();

        if(ligneSelectee == -1  && colonneSelectee == -1) {
            int contenu = grille_case[row][column];

            if (contenu == 0 || contenu == 4) {
                return;
            }

            // joueur 1 = noir, il ne peut sélectionner que les noirs
            if (jeu.getJoueur() == 1 && contenu != 1) {
                return;
            }

            // joueur 2 = blanc, il peut sélectionner blancs + roi
            if (jeu.getJoueur() == 2 && contenu != 2 && contenu != 3) {
                return;
            }
            ligneSelectee = row;
            colonneSelectee = column;
            ArrayList<Point> liste = jeu.listeCasesValides(ligneSelectee, colonneSelectee);
            grille.dessiner_valide(liste);
            refreshGrid();
        }else  if(ligneSelectee == row && colonneSelectee == column) {
            ligneSelectee = -1;
            colonneSelectee = -1;
            grille.effacerCasesValides();
            refreshGrid();
        }else{
            Point p = new Point(ligneSelectee,colonneSelectee);
            Point p1= new Point(row,column);
            jeu.deplacer(p,p1);
            grille.effacerCasesValides();
            colonneSelectee=-1;
            ligneSelectee=-1;
            refreshGrid();
        }
    }
    
    public boolean choisir_nom(){
        String nom = JOptionPane.showInputDialog(fenetreJeu,"Entrez le nom du fichier");

        if (nom == null) {
            return false;
        }
        fichier = nom;
        return true;
    }
    
    public void demanderSauvegardeEtQuitter() {
        int choix = JOptionPane.showConfirmDialog(
                fenetreJeu,
                "Voulez-vous sauvegarder la partie ?",
                "Sauvegarde",
                JOptionPane.YES_NO_CANCEL_OPTION
        );

        if (choix == JOptionPane.CANCEL_OPTION ) {
            return;
        }

        if (choix == JOptionPane.YES_OPTION) {
            if(!choisir_nom()){
             return;
            }
            ((Model.Jeu) jeu).sauvegardeFichier(fichier);
        }
        fenetreJeu.retourMenu();
    }
}
