package Reseau;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

import Model.BaseJeu;
import Model.Coup;
import Model.TableauBit;
import Utils.*;
import Vue.GrilleGraphique;
import Vue.JoueurActifPanel;

public class Client {

    private Socket socket;
    private Communication comIO;

    private BaseJeu jeu;
    private GrilleGraphique grilleGraphique;
    private JoueurActifPanel joueurActifPanel;

    private int joueur; // 1 = noir, 2 = blanc
    private boolean monTour;

    public Client(String adresseServeur, int port,
                  BaseJeu jeu,
                  GrilleGraphique grilleGraphique,
                  JoueurActifPanel joueurActifPanel) throws IOException {

        this.socket = new Socket(adresseServeur, port);
        this.comIO = new Communication(
                socket.getInputStream(),
                socket.getOutputStream()
        );

        this.jeu = jeu;
        this.grilleGraphique = grilleGraphique;
        this.joueurActifPanel = joueurActifPanel;

        this.joueur = -1;
        this.monTour = false;

        System.out.println("Connecté au serveur.");
    }

    public void ecouterServeur() {
        try {
            while (true) {
                int type = comIO.rcvInt();
                traiterMessage(type);
            }

        } catch (IOException e) {
            System.err.println("Connexion perdue avec le serveur.");
        } catch (NumberFormatException e) {
            System.err.println("Message serveur invalide.");
        }
    }

    private void traitementRep(Reponse r) throws IOException {
        
    }
    
    public void envoyerCoup(Coup coup) {
        if (!monTour) {
            System.out.println("Ce n'est pas ton tour.");
            return;
        }
        Request request = new Request(Communication.REQ_JOUE, coup);
        communication.sndReq(request);
    }

    public void demanderAnnulation() {
        Request request = new Request(Communication.REQ_ANNULE, null);
        communication.sndReq(request);
    }

    public void abandonner() {
        Request request = new Request(Communication.REQ_ABANDONNE, null);
        communication.sndReq(request);
    }

    public void demanderNul() {
        Request request = new Request(Communication.REQ_NUL, null);
        communication.sndReq(request);
    }

    public void deconnecter() {
        try {
            Request request = new Request(Communication.REQ_QUIT, null);
            communication.sndReq(request);

            communication.close();
            socket.close();

        } catch (IOException e) {
            System.err.println("Erreur pendant la déconnexion.");
        }
    }

    public boolean estMonTour() {
        return monTour;
    }

    public int getJoueur() {
        return joueur;
    }
    
    public void runClient(){
        
    }
}