import Reseau.Client;
import Model.Jeu;
import Model.Coup;
import Model.Point;
import Vue.GrilleGraphique;
import Vue.JoueurActifPanel;
import Controleur.ControleReseau;
import Config.Config;

public class TestReseau {
    public static void main(String[] args) throws Exception {
        Jeu jeu = new Jeu();

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

        Thread reception = new Thread(new Runnable() {
            public void run() {
                client.ecouterServeur();
            }
        });

        reception.start();

        Thread.sleep(1000);

        Coup coup = new Coup(
            new Point(2, 3),
            new Point(2, 5),
            client.getJoueur()
        );

        client.envoyerCoup(coup);
    }
}