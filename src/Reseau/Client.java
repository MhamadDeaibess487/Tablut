package Reseau;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import Model.BaseJeu;
import Model.Coup;
import Model.Message;
import Model.TableauBit;
import Vue.GrilleGraphique;
import Vue.JoueurActifPanel;

public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private BaseJeu jeu;
    private GrilleGraphique grilleGraphique;
    private JoueurActifPanel joueurActifPanel;

    private int joueur; 
    private boolean monTour;

    public Client(String adresseServeur, int port,
                  BaseJeu jeu,
                  GrilleGraphique grilleGraphique,
                  JoueurActifPanel joueurActifPanel) throws IOException {

        this.socket = new Socket(adresseServeur, port);

        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
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
            String ligne;

            while ((ligne = in.readLine()) != null) {
                Message message = Message.parseFromString(ligne);
                traiterMessage(message);
            }

        } catch (IOException e) {
            System.err.println("Connexion perdue avec le serveur.");
        }
    }

    private void traiterMessage(Message message) {
        String type = message.getType();

        if (type.equals("ASSIGN_PLAYER")) {
            joueur = Integer.parseInt(message.getContent());
            System.out.println("Je suis le joueur " + joueur);
        }

        else if (type.equals("BOARD_UPDATE")) {
            TableauBit tableauBit = message.getTableauBit();

            jeu.transformeLongEnGrille(tableauBit);
            grilleGraphique.effacerCasesValides();
            grilleGraphique.repaint();
        }

        else if (type.equals("TURN")) {
            int joueurActif = Integer.parseInt(message.getContent());

            monTour = (joueurActif == joueur);

            joueurActifPanel.setJoueurActif(joueurActif);
            grilleGraphique.setEnabled(monTour);

            if (monTour) {
                System.out.println("C'est mon tour.");
            } else {
                System.out.println("J'attends l'autre joueur.");
            }
        }

        else if (type.equals("INVALID_MOVE")) {
            JOptionPane.showMessageDialog(
                    null,
                    message.getContent(),
                    "Coup invalide",
                    JOptionPane.WARNING_MESSAGE
            );
        }

        else if (type.equals("GAME_OVER")) {
            JOptionPane.showMessageDialog(
                    null,
                    message.getContent(),
                    "Fin de partie",
                    JOptionPane.INFORMATION_MESSAGE
            );

            grilleGraphique.setEnabled(false);
        }

        else {
            System.err.println("Message inconnu : " + type);
        }
    }

    public void envoyerCoup(Coup coup) {
        if (!monTour) {
            System.out.println("Ce n'est pas ton tour.");
            return;
        }

        Message message = Message.move(coup);
        out.println(message.toNetworkString());
    }

    public void deconnecter() {
        try {
            Message message = new Message("DISCONNECT", "");
            out.println(message.toNetworkString());

            in.close();
            out.close();
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
}