package Tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import Model.Coup;
import Model.Point;
import Model.TableauBit;
import Utils.Communication;
import Utils.Reponse;
import Utils.Request;

public class TestCommunication {
    public static void main(String[] args) throws Exception {
        testerSerializeDeserializeCoup();
        testerSendReceiveRequest();
        testerSendReceiveReponse();
        System.out.println("TestCommunication OK");
    }

    private static void testerSerializeDeserializeCoup() {
        Communication communication = new Communication(
            new ByteArrayInputStream(new byte[0]),
            new ByteArrayOutputStream()
        );

        Coup coup = new Coup(new Point(0, 3), new Point(3, 3), 1);
        int serialise = communication.serializeCoup(coup);
        Coup deserialise = communication.deserializeCoup(serialise);

        verifierCoup(coup, deserialise, "serialize/deserialize Coup");
    }

    private static void testerSendReceiveRequest() throws Exception {
        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        Communication emetteur = new Communication(
            new ByteArrayInputStream(new byte[0]),
            sortie
        );

        Request attendue = new Request(
            Communication.REQ_JOUE,
            new Coup(new Point(4, 4), new Point(4, 6), 2)
        );
        emetteur.sndReq(attendue);

        Communication recepteur = new Communication(
            new ByteArrayInputStream(sortie.toByteArray()),
            new ByteArrayOutputStream()
        );

        Request obtenue = recepteur.rcvReq();
        if (obtenue == null) {
            throw new RuntimeException("Request recue nulle");
        }
        if (obtenue.reqType != attendue.reqType) {
            throw new RuntimeException("Type de Request incorrect");
        }
        verifierCoup(attendue.coup, obtenue.coup, "send/receive Request");
    }

    private static void testerSendReceiveReponse() throws Exception {
        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        Communication emetteur = new Communication(
            new ByteArrayInputStream(new byte[0]),
            sortie
        );

        TableauBit tab = new TableauBit(1L, 2L, 3L, 4L, 5L, 6L);
        Reponse attendue = new Reponse(Communication.REP_BOARD_UPDATE, tab);
        emetteur.sndRep(attendue);

        Communication recepteur = new Communication(
            new ByteArrayInputStream(sortie.toByteArray()),
            new ByteArrayOutputStream()
        );

        Reponse obtenue = recepteur.rcvRep();
        if (obtenue == null) {
            throw new RuntimeException("Reponse recue nulle");
        }
        if (obtenue.repType != attendue.repType) {
            throw new RuntimeException("Type de Reponse incorrect");
        }
        verifierTableauBit(attendue.grille, obtenue.grille, "send/receive Reponse");
    }

    private static void verifierCoup(Coup attendu, Coup obtenu, String contexte) {
        if (obtenu == null) {
            throw new RuntimeException("Coup nul pour " + contexte);
        }
        if (attendu.joueur != obtenu.joueur
                || attendu.a.l != obtenu.a.l
                || attendu.a.c != obtenu.a.c
                || attendu.b.l != obtenu.b.l
                || attendu.b.c != obtenu.b.c) {
            throw new RuntimeException("Coup incorrect pour " + contexte);
        }
    }

    private static void verifierTableauBit(TableauBit attendu, TableauBit obtenu, String contexte) {
        if (obtenu == null) {
            throw new RuntimeException("TableauBit nul pour " + contexte);
        }
        if (attendu.compareTo(obtenu) != 0) {
            throw new RuntimeException("TableauBit incorrect pour " + contexte);
        }
    }
}
