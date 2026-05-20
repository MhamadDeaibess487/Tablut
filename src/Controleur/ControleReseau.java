package Controleur;

import Model.BaseJeu;
import Model.Coup;
import Model.Point;
import Vue.GrilleGraphique;
import java.awt.*;
import java.util.ArrayList;
import Reseau.Client;

public class Controle {
    int ligneSelectee;
    int colonneSelectee;
    private Client client;

    public Controle(BaseJeu Jeu, GrilleGraphique Grille) {
        this.Jeu = Jeu;
        this.Grille = Grille;
        this.ligneSelectee = -1;
        this.colonneSelectee = -1;
    }

    @Override
    public void undo() {
        client.demanderAnnulation();
        refreshGrid();
    }
    
    public void abandonne(){
        client.abandonner();
    }
    
    public void demandeNul(){
        client.demanderNul();
    }

    public void refreshGrid() {
        Grille.repaint();
    }
    
    @Override
    public void handleGridClick(int row, int column) {
        int [][]grille_case = Jeu.getGrille();

        if(ligneSelectee == -1  && colonneSelectee == -1) {
            int contenu = grille_case[row][column];

            if (contenu == 0 || contenu == 4 || (Jeu.getJoueur() == 1 && contenu != 1)) {
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
    
    public void setClient(Client client) {
        this.client = client;
    }
}
