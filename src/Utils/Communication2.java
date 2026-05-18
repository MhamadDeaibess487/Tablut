package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import Model.Coup;
import Model.Point;
import Model.TableauBit;

public class Communication2{
    private BufferedReader in;
    private PrintWriter out;
    //Peut etre mettre une instance de reponse et request ici, et des fonctions permettant d'edit leurs vars

    public Communication2(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }
    
    public void sndInt(int i){
        out.println(i);
    }
    
    public void sndLong(long i){
        out.println(i);
    }
           
    public int rcvInt() throws IOException{
       return Integer.parseInt(in .readLine());
    }
    
    public long rcvLong() throws IOException{
       return Long.parseLong(in.readLine());
    }
    
    public int serializedCoup(Coup c){
        int serialz = 0;
        if(c == null){
            return Integer.MAX_VALUE;
        }
        int joueur = c.joueur - 1; // 0 ou 1
        serialz = (int) joueur | (int) c.a.l << 13 | (int) c.a.c << 9 | 
                (int) c.b.l << 5 | (int) c.b.c << 1;
        return serialz;
    }

    public Coup deserializedCoup(int coup){
        if(coup == Integer.MAX_VALUE)
            return null;
        int j = (coup & 1) + 1;
        Point a = new Point((coup >> 13) & 0xF,(coup >> 9) & 0xF);
        Point b = new Point((coup >> 5) & 0xF,(coup >> 1) & 0xF);
        return new Coup(a,b,j);
    }

    public void sndReq(Request r){
        if(r.reqType < 1 && r.reqType > 4){
            System.err.println("Bad request");
            return;
        }
        sndInt(r.reqType);
        int coup = serializedCoup(r.coup);
        sndInt(coup);
    }
    
    /*Effet de bord sur coup pour stocker la serialization*/
    public Request rcvReq() throws IOException{
        int reqType = rcvInt();
        if(reqType < 1 && reqType > 4){
            System.err.println("Bad request");
            return null;
        }
        int coup = rcvInt();
        return new Request(reqType,deserializedCoup(coup));
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
    
    public void sndRep(Reponse r){
        if(r.repType < -1 && r.repType > 4){
            System.err.println("Bad reponse");
            return;
        }
        sndInt(r.repType);
        sndTabBit(r.grille);
    }
    
    public Reponse rcvRep() throws IOException {
        int type = rcvInt();
        if(type < -1 && type > 4){
            System.err.println("Bad reponse");
            return null;
        }
        TableauBit tabB =rcvTabBit();
        return new Reponse(type,tabB);
    }
}
