package Vue;

import Controleur.ControleReseau;
import Model.BaseJeu;
import Reseau.Client;
import Config.Config;

import javax.swing.*;
import java.awt.*;

public class FenetreJeuReseau extends JFrame {
    private BaseJeu jeu;
    private JoueurActifPanel joueurActifPanel;
    private GrilleGraphique grille;
    private ControleReseau controle;
    private Client client;

    public FenetreJeuReseau(BaseJeu jeu, String ip) throws Exception {
        setTitle("Tablut - Multijoueur");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        this.jeu = jeu;

        
        grille = new GrilleGraphique(jeu);

        controle = new ControleReseau(jeu, grille);

        joueurActifPanel = new JoueurActifPanel(
            Config.CHEMIN_IMAGES + "/nap_on.png",
            Config.CHEMIN_IMAGES + "/nap_on.png",
            Config.CHEMIN_IMAGES + "/julien.png",
            Config.CHEMIN_IMAGES + "/julien.png",
            controle
        );

        client = new Client(ip, 2121, jeu, grille, joueurActifPanel);
        controle.setClient(client);

        GrilleButtonsReseau buttons = new GrilleButtonsReseau(grille, controle);

        add(grille, BorderLayout.CENTER);
        add(joueurActifPanel, BorderLayout.WEST);
        add(buttons, BorderLayout.EAST);

        getContentPane().setBackground(new Color(18, 22, 32));

        setVisible(true);

        Thread reception = new Thread(new Runnable() {
            public void run() {
                client.ecouterServeur();
            }
        });

        reception.start();
    }

    public void retourMenu() {
        dispose();
        new MenuGraphique(jeu);
    }
}