package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import Model.Coup;
import Model.Point;

public class Communication{
    BufferedReader in;
    PrintWriter out;
    
    public Communication(BufferedReader buf,PrintWriter wrt){
        in = buf;
        out = wrt;
    }
    
    public void sndAck(){
        out.write(0);
    }
    
    public void rcvAck() throws IOException{
       in.read();
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
        out.write(r.reqType);
        int coup = serializedCoup(r.c);
        out.write(coup + "\n");
        return true;
    }
    
    public Request rcvReq() throws IOException{
        int reqType = in.read();
        if(reqType < 1 && reqType > 4){
            System.err.println("Bad request");
            return null;
        }
        int coup = Integer.parseInt(in.readLine());
        return new Request(reqType,deserializedCoup(coup));
    }
}