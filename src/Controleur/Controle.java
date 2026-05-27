package Controleur;

import Model.Point;
import Model.Jeu;
import Vue.FenetreJeu;
import Vue.GrilleGraphique;
import Model.IA;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
    private boolean iaEnTrainDeJouer = false;
    public Controle(Jeu Jeu, GrilleGraphique Grille,FenetreJeu fenetreJeu) {
        super(Jeu,Grille);
        this.fenetreJeu = fenetreJeu;
        this.fichier = "";
        this.ligneSelectee = -1;
        this.colonneSelectee = -1;
        this.fenetreJeu = fenetreJeu;
        dernierLevelBlanc = 0;
        dernierLevelNoir = 0;
        this.IaBlanc = IA.creer(Jeu,levelIABlanc,2);
        this.IaNoir = IA.creer(Jeu,levelIANoir,1);
        this.fichier = "";
        this.iaNoirActive=false;
        this.iaBlancActive=false;

        timer_joueur = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fenetreJeu.setJoueurActif(jeu.getJoueur());
            }
        });
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
        timer_joueur.start();
        timer_IA.start();
    }

    public void setIaNoir(boolean active) {
        iaNoirActive = active;
    }

    public void setIaBlanc(boolean active) {
        iaBlancActive = active;
    }

    public void setNiveauIANoir(int niveau) {
        levelIANoir = niveau;
        IaNoir = IA.creer(jeu, levelIANoir, 1);
        dernierLevelNoir = levelIANoir;
    }

    public void setNiveauIABlanc(int niveau) {
        levelIABlanc = niveau;
        IaBlanc = IA.creer(jeu, levelIABlanc, 2);
        dernierLevelBlanc = levelIABlanc;
    }

    public void reset() {
        jeu.nouvellePartie();
        refreshGrid();
    }

    public void verifierTourIA() throws InterruptedException {
        if (jeu.getFini()) {
            timer_IA.stop();
            return;
        }
        if (dernierLevelBlanc != levelIABlanc) {
            IaBlanc = IA.creer(jeu, levelIABlanc, 2);
            dernierLevelBlanc = levelIABlanc;
        }

        if (dernierLevelNoir != levelIANoir) {
            IaNoir = IA.creer(jeu, levelIANoir, 1);
            dernierLevelNoir = levelIANoir;
        }

        if (jeu.getJoueur() == 2 && iaBlancActive) {
            lancerTourIA(IaBlanc);
            return;
        }

        if (jeu.getJoueur() == 1 && iaNoirActive) {
            lancerTourIA(IaNoir);
        }
        
    }
    
    public void undo() {
        if (jeu.getFini()) {
            return;
        }

        if (jeu.annuler()) {
            refreshGrid();
        }
    }

    public void redo() {
        if (jeu.getFini()) {
            return;
        }

        if (jeu.refaire()) {
            refreshGrid();
        }
    }

    public void refreshGrid() {
        Grille.repaint();
    }
    public void handleGridClick(int row, int column) {
        int [][]grille_case = jeu.getGrille();

        if(ligneSelectee == -1  && colonneSelectee == -1) {
            int contenu = grille_case[row][column];

            if (contenu == 0 || contenu == 4 || jeu.getFini()) {
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
            jeu.deplacer(p,p1);

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
            jeu.sauvegardeFichier(fichier);
        }
        fenetreJeu.retourMenu();
    }

    //temporisation 
    private void lancerTourIA(IA ia) {
        if (iaEnTrainDeJouer || jeu.getFini()) {
            return;
        }

        iaEnTrainDeJouer = true;

        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ia.joue();
                refreshGrid();
                iaEnTrainDeJouer = false;
            }
        });

        timer.setRepeats(false);
        timer.start();
    }
}

