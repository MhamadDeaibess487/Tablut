package Controleur;
import Model.BaseJeu;
import Model.Coup;
import Model.Point;
import Reseau.Client;
import Vue.FenetreJeu;
import Vue.GrilleGraphique;
import Vue.MenuGraphique;
import Vue.JoueurActifPanel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import Reseau.Client;

import static Config.Config.CHEMIN_SAUVEGARDES;

public class Controle {
    BaseJeu Jeu;
    GrilleGraphique Grille;
    int ligneSelectee;
    int colonneSelectee;
    String fichier;
    FenetreJeu fenetreJeu;
    private Client client;



    public Controle(BaseJeu Jeu, GrilleGraphique Grille,FenetreJeu fenetreJeu) {

        this.Jeu = Jeu;
        this.Grille = Grille;
        this.ligneSelectee = -1;
        this.colonneSelectee = -1;
        this.fenetreJeu = fenetreJeu;

        this.fichier = "";

    }

    public void toggleIA_blanc() {
    }
    public void toggleIA_noir() {
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
        int [][]grille_case = Jeu.getGrille();

        if(ligneSelectee == -1  && colonneSelectee == -1) {
            int contenu = grille_case[row][column];

            if (contenu == 0 || contenu == 4) {
                return;
            }

            // joueur 1 = noir, il ne peut sélectionner que les noirs
            if (Jeu.getJoueur() == 1 && contenu != 1) {
                return;
            }

            // joueur 2 = blanc, il peut sélectionner blancs + roi
            if (Jeu.getJoueur() == 2 && contenu != 2 && contenu != 3) {
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

            Point p = new Point(ligneSelectee,colonneSelectee);
            Point p1= new Point(row,column);
            Coup coup = new Coup(p, p1, client.getJoueur());
            client.envoyerCoup(coup);
            Grille.effacerCasesValides();
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
        fichier = CHEMIN_SAUVEGARDES+nom;
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
            ((Model.Jeu) Jeu).sauvegardeFichier(fichier);
        }

        fenetreJeu.retourMenu();
    }

    public void setClient(Client client) {
        this.client = client;
    }


}
