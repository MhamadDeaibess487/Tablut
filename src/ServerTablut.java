import java.net.*;
import java.io.*;
import Model.*;
import Utils.*;

public class ServerTablut{
    
    /*Traite une request r et renvoie un code de retour au client (Ack or -1)*/
    public void traitementReq(Communication[] com,Request r,Jeu j, int joueur){
        switch(r.reqType){
            case 1: /*Joue un coup*/
                if(r.coup != null){
                    com[joueur].sndInt((j.deplacer(r.coup.a, r.coup.b) != null) - 1); /*Envoie au client 0 si coupValide, -1 sinon*/
                    int jAdverse = (joueur % 2) + 1;
//                    int grille_updt = ;
                    /*Propagation du deplacement au joueur adverse*/
//                    com[jAdverse].
                }
                else{
                    com.hndlError("Coup invalid from client");
                }
                break;
            case 2: /*Annule un coup*/
                
                break;
            case 3: /*Quit*/
                break;
            default:
                com.hndlError("Unknown request");
                break;
        }
    }
    
    public static void main(String[] args){
        try{
            /*Connexion des joueurs*/
            ServerSocket serverfd = new ServerSocket(2121);
            Socket[] clientfd = new Socket[2];
            Communication[] comIO = new Communication[2];
            for(int i = 0;i<2;i++){
                clientfd[i] = serverfd.accept();
                comIO[i] = new Communication(
                    new InputStreamReader(clientfd[i].getInputStream()),
                    new PrintWriter(clientfd[i].getOutputStream(),true));
                /*bit ACK*/
                comIO[i].sndAck();
            }
            /*Fin connexion*/
            
            /*Lancement du jeu*/
            Jeu jeu = new Jeu();
            
            /*Boucle du jeu*/
            while(!jeu.estFini()){
                int joueur = jeu.getJoueur();
                Request r = comIO[joueur].rcvReq();
                traitementReq(comIO[joueur],r,jeu);
            }
        }
        catch(IOException e){
            System.err.println("Error socket : " + e.getMessage());
        }
    }
}