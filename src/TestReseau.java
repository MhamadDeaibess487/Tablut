import Reseau.Client;
import Model.Jeu;
import Model.Coup;
import Model.Point;
import Vue.GrilleGraphique;
import Vue.JoueurActifPanel;

public class TestReseau {
    public static void main(String[] args) throws Exception {
        Jeu jeu = new Jeu();

        GrilleGraphique grille = new GrilleGraphique(jeu);

        JoueurActifPanel panel = new JoueurActifPanel(
            "res/images/j1.png",
            "res/images/j1_highlight.png",
            "res/images/j2.png",
            "res/images/j2_highlight.png"
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