package Reseau;

import java.io.IOException;
import java.net.Socket;
import javax.swing.JOptionPane;

import Model.BaseJeu;
import Model.Coup;
import Model.TableauBit;
import Utils.Communication;
import Utils.Request;
import Utils.Reponse;
import Vue.GrilleGraphique;
import Vue.JoueurActifPanel;

public class Client {

    private Socket socket;
    private Communication communication;

    private BaseJeu jeu;
    private GrilleGraphique grilleGraphique;
    private JoueurActifPanel joueurActifPanel;

    private int joueur;      // 1 = noir, 2 = blanc
    private boolean monTour,gameOver;

    public Client(String adresseServeur, int port,
                  BaseJeu jeu,
                  GrilleGraphique grilleGraphique,
                  JoueurActifPanel joueurActifPanel) throws IOException {

        socket = new Socket(adresseServeur, port);
        communication = new Communication(
                socket.getInputStream(),
                socket.getOutputStream()
        );

        this.jeu = jeu;
        this.grilleGraphique = grilleGraphique;
        this.joueurActifPanel = joueurActifPanel;

        joueur = -1;
        gameOver = monTour = false;

        System.out.println("Connecté au serveur.");
    }

    public void ecouterServeur() {
        try {
            while (true) {
                Reponse rep = communication.rcvRep();
                traitementReponse(rep);
            }
        } catch (IOException e) {
            System.err.println("Connexion perdue avec le serveur.");
        }
    }


    private void traitementReponse(Reponse rep) {
        if (rep == null) {
            System.err.println("Réponse nulle reçue.");
            return;
        }

        switch (rep.repType) {
            case Communication.ERROR:
                JOptionPane.showMessageDialog(
                        null,
                        "Action refusée par le serveur.",
                        "Erreur",
                        JOptionPane.WARNING_MESSAGE
                );
                break;

            case Communication.ACK:
                System.out.println("Requête acceptée.");
                break;

            case Communication.REP_BOARD_UPDATE:
                appliquerNouvelleGrille(rep);
                break;

            case Communication.REP_ANNULE:
                traiterDemandeAnnulation();
                break;
            case Communication.REP_ASSIGN_PLAYER:
                joueur = (int) rep.grille.noir1;
                System.out.println("ASSIGN joueur = " + joueur);
                break;
            case Communication.REP_TURN_UPDATE:
                int joueurActif = (int) rep.grille.noir1;
                monTour = (joueurActif == joueur);
                System.out.println("TURN joueurActif = " + joueurActif 
                    + ", moi = " + joueur 
                    + ", monTour = " + monTour);
                break;
            case Communication.REP_GAME_OVER:
                /*Lecture r.grille.n1*/
                gameOver = true;
                break;
            default:
                System.err.println("Réponse inconnue : " + rep.repType);
                break;
        }
    }

    private void appliquerNouvelleGrille(Reponse rep) {
        if (rep.grille == null) {
            return;
        }
        jeu.transformeLongEnGrille(rep.grille);
        grilleGraphique.effacerCasesValides();
        grilleGraphique.repaint();
    }

    public void envoyerCoup(Coup coup) {
        if (!monTour) {
            System.out.println("Ce n'est pas ton tour.");
            return;
        }
        Request req = new Request(Communication.REQ_JOUE, coup);
        communication.sndReq(req);
    }

    public void demanderAnnulation() {
        Request req = new Request(Communication.REQ_ANNULE, null);
        communication.sndReq(req);
    }

    private void traiterDemandeAnnulation() {
        int choix = JOptionPane.showConfirmDialog(
                null,
                "L'autre joueur demande une annulation. Accepter ?",
                "Demande d'annulation",
                JOptionPane.YES_NO_OPTION
        );

        if (choix == JOptionPane.YES_OPTION) {
            communication.sndInt(Communication.ACK);
        } else {
            communication.sndInt(Communication.ERROR);
        }
    }

    public void abandonner() {
        Request req = new Request(Communication.REQ_ABANDONNE, null);
        communication.sndReq(req);
    }

    public void demanderNul() {
        Request req = new Request(Communication.REQ_NUL, null);
        communication.sndReq(req);
    }

    public void deconnecter() {
        try {
            Request req = new Request(Communication.REQ_QUIT, null);
            communication.sndReq(req);

            communication.close();
            socket.close();

        } catch (IOException e) {
            System.err.println("Erreur pendant la déconnexion.");
        }
    }

    // private void terminerPartie(String message) {
    //     JOptionPane.showMessageDialog(
    //             null,
    //             message,
    //             "Fin de partie",
    //             JOptionPane.INFORMATION_MESSAGE
    //     );

    //     grilleGraphique.setEnabled(false);
    // }

    public boolean estMonTour() {
        return monTour;
    }

    public int getJoueur() {
        return joueur;
    }
    
    public BaseJeu getGrille() {
        return jeu;
    }
    
    public void runClient(){
        while(!gameOver){ /*ou true si besoin arret part deconnexion() ou fermeture du jeu*/
            Reponse rep = communication.rcvRep();
            traitementReponse(rep);
        }
        /*Fermeture socket*/
    }

    // Méthode de test pour envoyer un coup arbitraire
    public void testEnvoyerCoup(int a, int b, int c, int d) {
        Request req = new Request(Communication.REQ_JOUE, new Coup(new Model.Point(a, b), new Model.Point(c, d), joueur));
        communication.sndReq(req);
    }
}