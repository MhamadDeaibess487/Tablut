package Controleur;
import Model.BaseJeu;
import Model.Point;
import Vue.FenetreJeu;
import Vue.GrilleGraphique;
import Vue.JoueurActifPanel;
import Model.IA;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;



public class Controle {
    BaseJeu Jeu;
    GrilleGraphique Grille;
    int ligneSelectee;
    int colonneSelectee;
    String fichier;
    FenetreJeu fenetreJeu;

    public boolean joueur1_IA;
    public boolean joueur2_IA;
    IA IA_blanc;
    IA IA_noir;
    int levelIANoir;
    int levelIABlanc;
    Timer timer_IA;
    Timer timer_joueur;
    public Controle(BaseJeu Jeu, GrilleGraphique Grille,FenetreJeu fenetreJeu) {

        this.Jeu = Jeu;
        this.Grille = Grille;
        this.ligneSelectee = -1;
        this.colonneSelectee = -1;
        this.fenetreJeu = fenetreJeu;
        levelIANoir = 0;
        levelIABlanc = 0;
        this.IA_blanc = IA.creer(((Model.Jeu)Jeu),levelIABlanc,2);
        this.IA_noir = IA.creer(((Model.Jeu)Jeu),levelIANoir,1);
        this.fichier = "";
        this.joueur1_IA = false;
        this.joueur2_IA = false;

        timer_joueur = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fenetreJeu.setJoueurActif(Jeu.getJoueur());
            }
        });
        timer_joueur.start();
        timer_IA = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifierTourIA();
            }
        });
        timer_IA.start();

    }

    public void set_IA_blanc(boolean active) {
        joueur1_IA = active;
    }
    public void set_IA_noir(boolean active) {
        joueur2_IA = active;
    }

    public BaseJeu getJeu() {
        return Jeu;
    }
    public void  setNiveauIANoir(int niveau){
        levelIANoir = niveau;
    }
    public void setNiveauIABlanc(int niveau){
        levelIABlanc = niveau;
    }

    public void reset() {
        ((Model.Jeu)Jeu).nouvellePartie();
        refreshGrid();
    }

    public void verifierTourIA() {
        if (Jeu.getFini()) {
            timer_IA.stop();
            return;
        }
        this.IA_blanc = IA.creer(((Model.Jeu)Jeu),levelIABlanc,2);
        this.IA_noir = IA.creer(((Model.Jeu)Jeu),levelIANoir,1);
        if (Jeu.getJoueur() == 2 && joueur1_IA) {
            IA_blanc.joue();
            refreshGrid();
            //ajouter l'affichage des coup possible

            return;
        }

        if (Jeu.getJoueur() == 1 && joueur2_IA) {

            IA_noir.joue();
            refreshGrid();
            //ajouter l'affichage des coup possible

        }
    }
    public void undo() {
        ((Model.Jeu)Jeu).annuler();
        refreshGrid();
    }

    public void redo() {
        ((Model.Jeu)Jeu).refaire();
        refreshGrid();
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
            Jeu.deplacer(p,p1);

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
            ((Model.Jeu) Jeu).sauvegardeFichier(fichier);
        }

        fenetreJeu.retourMenu();
    }


}
