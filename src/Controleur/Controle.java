package Controleur;

import Model.Point;
import Model.BaseJeu;
import Vue.FenetreJeu;
import Vue.GrilleGraphique;
import Model.IA;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.lang.Thread;


public class Controle extends ControleAbstract{
    String fichier;
    FenetreJeu fenetreJeu;
    int ligneSelectee;
    int colonneSelectee;
    public boolean iaNoirActive;
    public boolean iaBlancActive;
    IA IaBlanc;
    IA IaNoir;
    int levelIANoir;
    int levelIABlanc;
    int dernierLevelBlanc;
    int dernierLevelNoir;
    Timer timer_IA;
    Timer timer_joueur;
    public Controle(BaseJeu Jeu, GrilleGraphique Grille,FenetreJeu fenetreJeu) {
        super(Jeu,Grille);
        this.fenetreJeu = fenetreJeu;
        this.fichier = "";
        this.ligneSelectee = -1;
        this.colonneSelectee = -1;
        this.fenetreJeu = fenetreJeu;
        levelIANoir = 1;
        levelIABlanc = 1;
        dernierLevelBlanc = 0;
        dernierLevelNoir = 0;
        this.IaBlanc = IA.creer(((Model.Jeu)Jeu),levelIABlanc,2);
        this.IaNoir = IA.creer(((Model.Jeu)Jeu),levelIANoir,1);
        this.fichier = "";
        this.iaNoirActive=false;
        this.iaBlancActive=false;

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
                try {
                    verifierTourIA();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        timer_IA.start();

    }

    public void setIaNoir(boolean active) {
        iaNoirActive = active;
    }

    public void setIaBlanc(boolean active) {
        iaBlancActive = active;
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

    public void verifierTourIA() throws InterruptedException {
        if (Jeu.getFini()) {
            timer_IA.stop();
            return;
        }
        if (dernierLevelBlanc != levelIABlanc) {
            IaBlanc = IA.creer(((Model.Jeu) Jeu), levelIABlanc, 2);
            dernierLevelBlanc = levelIABlanc;
        }

        if (dernierLevelNoir != levelIANoir) {
            IaNoir = IA.creer(((Model.Jeu) Jeu), levelIANoir, 1);
            dernierLevelNoir = levelIANoir;
        }

        if (Jeu.getJoueur() == 2 && iaBlancActive) {
            IaBlanc.joue();
            refreshGrid();
            return;
        }

        if (Jeu.getJoueur() == 1 && iaNoirActive) {
            IaNoir.joue();
            refreshGrid();
        }
    }
    public void undo() {
        if(((Model.Jeu)Jeu).annuler())
            refreshGrid();
    }

    public void redo() {
        if(((Model.Jeu)Jeu).refaire())
            refreshGrid();
    }




    public void refreshGrid() {
        Grille.repaint();
    }
    public void handleGridClick(int row, int column) {
        int [][]grille_case = Jeu.getGrille();

        if(ligneSelectee == -1  && colonneSelectee == -1) {
            int contenu = grille_case[row][column];

            if (contenu == 0 || contenu == 4 || getJeu().getFini()) {
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
