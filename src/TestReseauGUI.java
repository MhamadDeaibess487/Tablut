import Model.Jeu;
import Reseau.Client;
import Vue.GrilleGraphique;
import Vue.JoueurActifPanel;
import Vue.GrilleButtonsReseau;
import Controleur.Controle;
import Controleur.ControleReseau;
import Config.Config;

import javax.swing.*;
import java.awt.*;

public class TestReseauGUI {
    public static void main(String[] args) throws Exception {
        Jeu jeu = new Jeu();

        JFrame frame = new JFrame("Test Réseau Tablut");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLayout(new BorderLayout());

        GrilleGraphique grille = new GrilleGraphique(jeu);

        ControleReseau controle = new ControleReseau(jeu, grille);

        JoueurActifPanel panel = new JoueurActifPanel(
            Config.CHEMIN_IMAGES + "/nap_on.png",
            Config.CHEMIN_IMAGES + "/nap_on.png",
            Config.CHEMIN_IMAGES + "/julien.png",
            Config.CHEMIN_IMAGES + "/julien.png",
            controle
        );

        


        Client client = new Client("localhost", 2121, jeu, grille, panel);
        controle.setClient(client);

        GrilleButtonsReseau buttons = new GrilleButtonsReseau(grille, controle);

        frame.add(grille, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.WEST);
        frame.add(buttons, BorderLayout.EAST);

        frame.setVisible(true);

        Thread reception = new Thread(new Runnable() {
            public void run() {
                client.ecouterServeur();
            }
        });

        reception.start();
    }
}