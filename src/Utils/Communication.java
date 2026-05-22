package Utils;

import java.io.*;

import Model.Coup;
import Model.Point;
import Model.TableauBit;

public class Communication {
    private BufferedReader in;
    private PrintWriter out;

    public static final int ACK = 0;
    public static final int ERROR = -1;

    public static final int REQ_JOUE = 1;
    public static final int REQ_ANNULE = 2;
    public static final int REQ_QUIT = 3;
    public static final int REQ_ABANDONNE = 4;
    public static final int REQ_NUL = 5;

    public static final int REP_BOARD_UPDATE = 10;
    public static final int REP_TURN_UPDATE = 11;
    public static final int REP_GAME_OVER = 12;
    public static final int REP_ASSIGN_PLAYER = 13;
    public static final int REP_ANNULE = 14;


    public Communication(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    public Communication(InputStream input, OutputStream output) {
        this.in = new BufferedReader(new InputStreamReader(input));
        this.out = new PrintWriter(new OutputStreamWriter(output), true);
    }

    public void sndInt(int i) {
        out.println(i);
    }

    public int rcvInt() throws IOException {
        String line = in.readLine();

        if (line == null) {
            throw new IOException("Connexion fermée");
        }

        return Integer.parseInt(line);
    }

    public void sndLong(long l) {
        out.println(l);
    }

    public long rcvLong() throws IOException {
        String line = in.readLine();

        if (line == null) {
            throw new IOException("Connexion fermée");
        }

        return Long.parseLong(line);
    }

    public void sndAck() {
        sndInt(ACK);
    }

    public void sndError() {
        sndInt(ERROR);
    }

    public void hndlError(String message) {
        System.err.println(message);
        sndError();
    }

    public int serializeCoup(Coup c) {
        if (c == null) {
            return Integer.MAX_VALUE;
        }

        int joueur = c.joueur - 1;

        return joueur
                | (c.a.l << 13)
                | (c.a.c << 9)
                | (c.b.l << 5)
                | (c.b.c << 1);
    }

    public Coup deserializeCoup(int value) {
        if (value == Integer.MAX_VALUE) {
            return null;
        }

        int joueur = (value & 1) + 1;

        Point a = new Point((value >> 13) & 0xF, (value >> 9) & 0xF);
        Point b = new Point((value >> 5) & 0xF, (value >> 1) & 0xF);

        return new Coup(a, b, joueur);
    }

    public void sndReq(Request r) {
        if (r == null || r.reqType < REQ_JOUE || r.reqType > REQ_NUL) {
            hndlError("Bad request");
            return;
        }

        sndInt(r.reqType);
        sndInt(serializeCoup(r.coup));
    }

    public Request rcvReq() throws IOException {
        int reqType = rcvInt();

        if (reqType < REQ_JOUE || reqType > REQ_NUL) {
            System.err.println("Bad request");
            return null;
        }

        int coup = rcvInt();

        return new Request(reqType, deserializeCoup(coup));
    }

    public void sndTabBit(TableauBit tabB){
        if(tabB == null){
            sndLong(Long.MAX_VALUE);
            return;
        }
        sndLong(tabB.noir1);
        sndLong(tabB.noir2);
        sndLong(tabB.blanc1);
        sndLong(tabB.blanc2);
        sndLong(tabB.roi1);
        sndLong(tabB.roi2);
    }
    
    public TableauBit rcvTabBit() throws IOException {
        long n1,n2,b1,b2,r1,r2;
        n1 = rcvLong();
        if(n1 == Long.MAX_VALUE){
            return null;
        }
        n2 = rcvLong();
        b1 = rcvLong();
        b2 = rcvLong();
        r1 = rcvLong();
        r2 = rcvLong();
        return new TableauBit(n1,n2,b1,b2,r1,r2);
    }
    
    private boolean repTypeValide(int type) {
        return type == ERROR
            || type == ACK
            || type == REP_BOARD_UPDATE
            || type == REP_TURN_UPDATE
            || type == REP_GAME_OVER
            || type == REP_ASSIGN_PLAYER
            || type == REP_ANNULE;
    }

    public void sndRep(Reponse r) {
        if (r == null || !repTypeValide(r.repType)) {
            System.err.println("Bad response");
            return;
        }

        sndInt(r.repType);
        sndTabBit(r.grille);
    }

    public Reponse rcvRep() throws IOException {
        int type = rcvInt();

        if (!repTypeValide(type)) {
            System.err.println("Bad response");
            return null;
        }

        TableauBit tabB = rcvTabBit();
        return new Reponse(type, tabB);
    }

    

    public void close() throws IOException {
        in.close();
        out.close();
    }
}