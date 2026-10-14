package tablut.tests;

import java.net.Socket;

import tablut.model.Coup;
import tablut.model.Jeu;
import tablut.model.TableauBit;
import tablut.network.ServerTablut;
import tablut.network.protocol.Communication;
import tablut.network.protocol.Reponse;
import tablut.network.protocol.Request;

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

            // A request cannot move the opponent's piece, nor index beyond the board.
            noir.sndReq(new Request(Communication.REQ_JOUE,
                new Coup(new tablut.model.Point(2,4),new tablut.model.Point(2,3),1)));
            verifierRepType(noir.rcvRep(),Communication.ERROR,"opponent piece refused");
            verifierRepType(blanc.rcvRep(),Communication.ERROR,"opponent piece refused broadcast");
            noir.sndReq(new Request(Communication.REQ_JOUE,
                new Coup(new tablut.model.Point(0,3),new tablut.model.Point(15,3),1)));
            verifierRepType(noir.rcvRep(),Communication.ERROR,"out-of-board move refused");
            verifierRepType(blanc.rcvRep(),Communication.ERROR,"out-of-board move refused broadcast");

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

            noir.sndReq(new Request(Communication.REQ_ANNULE,null));
            verifierRepType(blanc.rcvRep(),Communication.REP_ANNULE,"undo negotiation");
            blanc.sndInt(Communication.ACK);
            verifierTableauBit(new Jeu().transformerGrilleEnLong(),noir.rcvRep().grille,"undo black");
            verifierTableauBit(new Jeu().transformerGrilleEnLong(),blanc.rcvRep().grille,"undo white");
            verifierRepType(noir.rcvRep(),Communication.REP_TURN_UPDATE,"undo turn black");
            verifierRepType(blanc.rcvRep(),Communication.REP_TURN_UPDATE,"undo turn white");

            noir.sndReq(new Request(Communication.REQ_NUL,null));
            verifierRepType(blanc.rcvRep(),Communication.REP_NUL,"draw negotiation");
            blanc.sndInt(Communication.ACK);
            Reponse finNoir = noir.rcvRep();
            Reponse finBlanc = blanc.rcvRep();
            verifierRepType(finNoir,Communication.REP_GAME_OVER,"draw black");
            verifierRepType(finBlanc,Communication.REP_GAME_OVER,"draw white");
            if (finNoir.grille.noir1 != 0 || finBlanc.grille.noir1 != 0) {
                throw new AssertionError("Negotiated draw has incorrect winner");
            }

        }

        serveur.join(4000);
        if (serveur.isAlive()) throw new AssertionError("Server did not shut down after game over");
        verifierDeconnexion();
        System.out.println("TestReseauLocal OK (move, invalid input, undo, draw, disconnect, shutdown)");
    }

    private static void verifierDeconnexion() throws Exception {
        Thread serveur = new Thread(() -> ServerTablut.main(new String[0]));
        serveur.setDaemon(true);
        serveur.start();
        attendre(200);
        try (Socket a = new Socket("127.0.0.1",2121); Socket b = new Socket("127.0.0.1",2121)) {
            a.setSoTimeout(4000); b.setSoTimeout(4000);
            lireEtatInitial(new Communication(a.getInputStream(),a.getOutputStream()));
            lireEtatInitial(new Communication(b.getInputStream(),b.getOutputStream()));
        }
        serveur.join(4000);
        if (serveur.isAlive()) throw new AssertionError("Server leaked after client disconnect");
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
