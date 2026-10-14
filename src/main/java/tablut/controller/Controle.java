package tablut.controller;

import tablut.model.Point;
import tablut.model.Coup;
import java.util.concurrent.ExecutionException;
import tablut.model.Jeu;
import tablut.ui.FenetreJeu;
import tablut.ui.GrilleGraphique;
import tablut.ai.IA;
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
    int levelIANoir;
    int levelIABlanc;
    Timer timer_IA;
    Timer timer_joueur;
    private boolean iaEnTrainDeJouer = false;
    private Timer attenteIA;
    private SwingWorker<Coup, Void> rechercheIA;
    private int revisionPartie;

    private void annulerRechercheIA() {
        revisionPartie++;
        if (attenteIA != null) attenteIA.stop();
        if (rechercheIA != null) rechercheIA.cancel(true);
        rechercheIA = null;
        iaEnTrainDeJouer = false;
    }

    public void arreter() {
        annulerRechercheIA();
        timer_IA.stop();
        timer_joueur.stop();
    }
    public Controle(Jeu Jeu, GrilleGraphique Grille,FenetreJeu fenetreJeu) {
        super(Jeu,Grille);
        this.fenetreJeu = fenetreJeu;
        this.fichier = "";
        this.ligneSelectee = -1;
        this.colonneSelectee = -1;
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
        annulerRechercheIA();
        iaNoirActive = active;
    }

    public void setIaBlanc(boolean active) {
        annulerRechercheIA();
        iaBlancActive = active;
    }

    public void setNiveauIANoir(int niveau) {
        annulerRechercheIA();
        levelIANoir = niveau;
    }

    public void setNiveauIABlanc(int niveau) {
        annulerRechercheIA();
        levelIABlanc = niveau;
    }

    public void reset() {
        annulerRechercheIA();
        jeu.nouvellePartie();
        ligneSelectee = -1;
        colonneSelectee = -1;
        Grille.effacerCasesValides();
        iaEnTrainDeJouer = false;
        if (!timer_joueur.isRunning()) {
            timer_joueur.start();
        }
        if (!timer_IA.isRunning()) {
            timer_IA.start();
        }
        refreshGrid();
    }

    public void verifierTourIA() throws InterruptedException {
        if (jeu.PlusDeCoup()) {
            jeu.terminerPartie();
            arreterTimersSiPartieFinie();
            refreshGrid();
            return;
        }

        if (jeu.getFini()) {
            arreterTimersSiPartieFinie();
            return;
        }
        if (jeu.getJoueur() == 2 && iaBlancActive) {
            lancerTourIA(levelIABlanc);
            return;
        }

        if (jeu.getJoueur() == 1 && iaNoirActive) {
            lancerTourIA(levelIANoir);
        }
        
    }
    
    public void undo() {
        if (jeu.getFini()) {
            return;
        }

        annulerRechercheIA();
        if (jeu.annuler()) {
            refreshGrid();
        }
    }

    public void redo() {
        if (jeu.getFini()) {
            return;
        }

        annulerRechercheIA();
        if (jeu.refaire()) {
            refreshGrid();
        }
    }

    public void refreshGrid() {
        Grille.repaint();
    }

    private void arreterTimersSiPartieFinie() {
        if (!jeu.getFini()) {
            return;
        }
        annulerRechercheIA();
        timer_IA.stop();
        timer_joueur.stop();
    }

    private void finaliserApresCoup() {
        if (!jeu.getFini() && jeu.PlusDeCoup()) {
            jeu.terminerPartie();
        }

        if (jeu.draw()) {
            arreterTimersSiPartieFinie();
            refreshGrid();
            return;
        }

        arreterTimersSiPartieFinie();
        refreshGrid();
    }

    public void handleGridClick(int row, int column) {
        if (iaEnTrainDeJouer || jeu.getFini()
                || (jeu.getJoueur() == 1 ? iaNoirActive : iaBlancActive)) return;
        int [][]grille_case = jeu.getGrille();

        if (jeu.PlusDeCoup()) {
            jeu.terminerPartie();
            arreterTimersSiPartieFinie();
            refreshGrid();
            return;
        }

        int contenu = grille_case[row][column];

        if(ligneSelectee == -1  && colonneSelectee == -1) {
            if (!estPieceSelectionnable(contenu)) {
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
        }else if (estPieceSelectionnable(contenu)) {
            ligneSelectee = row;
            colonneSelectee = column;
            ArrayList<Point> liste = jeu.listeCasesValides(ligneSelectee, colonneSelectee);
            Grille.dessiner_valide(liste);
            refreshGrid();
        }else{
            Point p = new Point(ligneSelectee,colonneSelectee);
            Point p1= new Point(row,column);
            if (jeu.deplacer(p,p1) == null) {
                return;
            }

            Grille.effacerCasesValides();
            colonneSelectee=-1;
            ligneSelectee=-1;
            finaliserApresCoup();
        }
    }

    private boolean estPieceSelectionnable(int contenu) {
        if (contenu == 0 || contenu == 4 || jeu.getFini()) {
            return false;
        }

        if (jeu.getJoueur() == 1) {
            return contenu == 1;
        }

        return contenu == 2 || contenu == 3;
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

        if (choix == JOptionPane.CANCEL_OPTION || choix == JOptionPane.CLOSED_OPTION) {
            return;
        }

        if (choix == JOptionPane.YES_OPTION) {
            if(!choisir_nom()){
             return;
            }
            try {
                jeu.sauvegardeFichier(fichier);
            } catch (IllegalArgumentException | java.io.UncheckedIOException e) {
                JOptionPane.showMessageDialog(fenetreJeu, e.getMessage(), "Erreur de sauvegarde", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        fenetreJeu.retourMenu();
    }

    // Keep the original two-second pacing; calculate on a detached game snapshot.
    private void lancerTourIA(int niveau) {
        if (iaEnTrainDeJouer || jeu.getFini()) return;
        iaEnTrainDeJouer = true;
        ligneSelectee = colonneSelectee = -1;
        Grille.effacerCasesValides();
        int revision = revisionPartie;
        int joueur = jeu.getJoueur();
        Jeu snapshot = new Jeu(jeu);
        attenteIA = new Timer(2000, e -> {
            rechercheIA = new SwingWorker<>() {
                @Override
                protected Coup doInBackground() {
                    return IA.creer(snapshot, niveau, joueur).choisirCoup();
                }

                @Override
                protected void done() {
                    if (isCancelled() || revision != revisionPartie) return;
                    try {
                        Coup coup = get();
                        if (!jeu.getFini() && jeu.getJoueur() == joueur && coup != null) {
                            jeu.deplacer(coup.a, coup.b);
                        }
                        finaliserApresCoup();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    } catch (ExecutionException ex) {
                        setIaNoir(false);
                        setIaBlanc(false);
                        JOptionPane.showMessageDialog(fenetreJeu, ex.getCause().toString(), "Erreur IA", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        if (revision == revisionPartie) {
                            rechercheIA = null;
                            iaEnTrainDeJouer = false;
                        }
                    }
                }
            };
            rechercheIA.execute();
        });
        attenteIA.setRepeats(false);
        attenteIA.start();
    }
}
