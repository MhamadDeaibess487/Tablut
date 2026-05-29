package Tests;

import java.net.Socket;

import Model.Coup;
import Model.Jeu;
import Model.TableauBit;
import Reseau.ServerTablut;
import Utils.Communication;
import Utils.Reponse;
import Utils.Request;

public class TestReseauLocal {
    public static void main(String[] args) throws Exception {
        Thread serveur = new Thread(() -> ServerTablut.main(new String[0]));
        serveur.setDaemon(true);
        serveur.start();

        attendre(200);

        try (
            Socket socket1 = new Socket("127.0.0.1", 2121);
            Socket socket2 = new Socket("127.0.0.1", 2121)
        ) {
            socket1.setSoTimeout(4000);
            socket2.setSoTimeout(4000);

            Communication client1 = new Communication(socket1.getInputStream(), socket1.getOutputStream());
            Communication client2 = new Communication(socket2.getInputStream(), socket2.getOutputStream());

            EtatInitial etat1 = lireEtatInitial(client1);
            EtatInitial etat2 = lireEtatInitial(client2);

            Communication noir = etat1.joueur == 1 ? client1 : client2;
            Communication blanc = etat1.joueur == 2 ? client1 : client2;

            Jeu attendu = new Jeu();
            Coup coup = TestTableauBit.premierCoupLegal(attendu, 1);
            if (coup == null) {
                throw new RuntimeException("Impossible de trouver un coup noir legal");
            }

            noir.sndReq(new Request(Communication.REQ_JOUE, coup));
            if (attendu.deplacer(coup.a, coup.b) == null) {
                throw new RuntimeException("Le coup envoye au serveur est invalide cote modele");
            }

            Reponse majNoir = noir.rcvRep();
            Reponse majBlanc = blanc.rcvRep();
            Reponse tourNoir = noir.rcvRep();
            Reponse tourBlanc = blanc.rcvRep();

            verifierRepType(majNoir, Communication.REP_BOARD_UPDATE, "board update noir");
            verifierRepType(majBlanc, Communication.REP_BOARD_UPDATE, "board update blanc");
            verifierRepType(tourNoir, Communication.REP_TURN_UPDATE, "turn update noir");
            verifierRepType(tourBlanc, Communication.REP_TURN_UPDATE, "turn update blanc");

            TableauBit tabAttendu = attendu.transformerGrilleEnLong();
            verifierTableauBit(tabAttendu, majNoir.grille, "grille noire synchronisee");
            verifierTableauBit(tabAttendu, majBlanc.grille, "grille blanche synchronisee");

            if ((int) tourNoir.grille.noir1 != 2 || (int) tourBlanc.grille.noir1 != 2) {
                throw new RuntimeException("Le tour actif devrait passer aux blancs apres le premier coup");
            }

        }

        attendre(200);
        System.out.println("TestReseauLocal OK");
    }

    private static EtatInitial lireEtatInitial(Communication client) throws Exception {
        Reponse assignation = client.rcvRep();
        Reponse tour = client.rcvRep();
        Reponse plateau = client.rcvRep();

        verifierRepType(assignation, Communication.REP_ASSIGN_PLAYER, "assignation joueur");
        verifierRepType(tour, Communication.REP_TURN_UPDATE, "tour initial");
        verifierRepType(plateau, Communication.REP_BOARD_UPDATE, "plateau initial");

        return new EtatInitial((int) assignation.grille.noir1);
    }

    private static void verifierRepType(Reponse rep, int typeAttendu, String contexte) {
        if (rep == null) {
            throw new RuntimeException("Reponse nulle pour " + contexte);
        }
        if (rep.repType != typeAttendu) {
            throw new RuntimeException(
                "Type de reponse incorrect pour " + contexte + ": attendu " + typeAttendu + ", obtenu " + rep.repType
            );
        }
    }

    private static void verifierTableauBit(TableauBit attendu, TableauBit obtenu, String contexte) {
        if (obtenu == null || attendu.compareTo(obtenu) != 0) {
            throw new RuntimeException("Tableaux non synchronises pour " + contexte);
        }
    }

    private static void attendre(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

    private static class EtatInitial {
        private final int joueur;

        private EtatInitial(int joueur) {
            this.joueur = joueur;
        }
    }
}
