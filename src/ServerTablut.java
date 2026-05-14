import java.net.*;
import java.io.*;
import Model.*;
import Utils.*;

public class ServerTablut{
    
    /*Traite une request r et renvoie un code de retour au client (Ack or -1)*/
    public static void traitementReq(Communication2[] com, Request r, Jeu j, int joueur){
        switch(r.reqType){
            case 1: /*Joue un coup*/
                if(r.coup != null){
                    if(j.deplacer(r.coup.a, r.coup.b) != null){ /*Si le deplacement à eu lieu*/
                        com[joueur].sndInt(0); // Ack send to client
                        int jAdverse = (joueur % 2) + 1;
                        /*Propagation du deplacement au joueur adverse*/
                        TableauBit grille_updt = j.transformerGrilleEnLong();
                        com[jAdverse].sndTabBit(grille_updt);
                        return;
                    }
                }
                com[joueur].hndlError("Coup invalid from client");
                break;
            case 2: /*Annule un coup*/
                break;
            case 3: /*Quit*/
                break;
            default:
                com[joueur].hndlError("Unknown request");
                break;
        }
    }
    
    public static void main(String[] args){
        try{
            /*Connexion des joueurs*/
            ServerSocket serverfd = new ServerSocket(2121);
            Socket[] clientfd = new Socket[2];
            Communication2[] comIO = new Communication2[2];
            for(int i = 0;i<2;i++){
                clientfd[i] = serverfd.accept();
                comIO[i] = new Communication2(
                    new BufferedReader(new InputStreamReader(clientfd[i].getInputStream())),
                    new PrintWriter(clientfd[i].getOutputStream(),true));
                /*bit ACK*/
                comIO[i].sndInt(0);
            }
            /*Fin connexion*/
            
            /*Lancement du jeu*/
            Jeu jeu = new Jeu();
            
            /*Boucle du jeu*/
            while(!jeu.estFini()){
                int joueur = jeu.getJoueur();
                Request r = comIO[joueur].rcvReq();
                traitementReq(comIO,r,jeu,joueur);
            }
        }
        catch(IOException e){
            System.err.println("Error socket : " + e.getMessage());
        }
    }
}