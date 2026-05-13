package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import Model.Coup;
import Model.Point;
import Model.TableauBit;

public class Communication{
    BufferedReader in;
    PrintWriter out;
    
    public Communication(BufferedReader buf,PrintWriter wrt){
        in = buf;
        out = wrt;
    }
    
    public void sndInt(int i){
        out.println(i);
    }
    
    public void sndLong(long i){
        out.println(i);
    }
           
    public int rcvInt() throws IOException{
       return Integer.parseInt(in.readLine());
    }
    
    public long rcvLong() throws IOException{
       return Long.parseLong(in.readLine());
    }
    
    public void hndlError(String s){
        System.err.println(s);
        sndInt(-1);
    }
    
    public int serializedCoup(Coup c){
        int serialz = 0;
        if(c == null){
            return Integer.MAX_VALUE;
        }
        
        serialz = c.joueur | c.a.l << 13 | c.a.c << 9 | c.b.l << 5 | c.b.c << 1; 
        return serialz;
    }
    
    public Coup deserializedCoup(int coup){
        if(coup == Integer.MAX_VALUE)
            return null;
        int j = coup & 1;
        Point a = new Point((coup >> 13) & 0xFF,(coup >> 9) & 0xFF);
        Point b = new Point((coup >> 5) & 0xFF,(coup >> 1) & 0xFF);
        return new Coup(a,b,j);
    }
    
    public boolean sndReq(Request r){
        if(r.reqType < 1 && r.reqType > 4){
            System.err.println("Bad request");
            return false;
        }
        sndInt(r.reqType);
        int coup = serializedCoup(r.c);
        out.println(coup);
        return true;
    }
    
    /*Effet de bord sur coup pour stocker la serialization*/
    public Request rcvReq() throws IOException{
        int reqType = in.read();
        if(reqType < 1 && reqType > 4){
            System.err.println("Bad request");
            return null;
        }
        int coup = Integer.parseInt(in.readLine());
        return new Request(reqType,deserializedCoup(coup));
    }
    
    public void sndTabBit(TableauBit tabB){
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
        n2 = rcvLong();
        b1 = rcvLong();
        b2 = rcvLong();
        r1 = rcvLong();
        r2 = rcvLong();
        return new TableauBit(n1,n2,b1,b2,r1,r2);
    }
}