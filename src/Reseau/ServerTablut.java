package Reseau;
import java.net.*;
import java.io.*;
import java.util.Random;
import Model.*;
import Utils.Request;
import Utils.Reponse;
import Utils.Communication;


public class ServerTablut{
    public static void main(String[] args){
        ServerSocket serverfd = null;
        Socket[] clientfd = null;
        Communication[] comIO;
        Random r = new Random();
        try{
            serverfd = new ServerSocket(2121);
            clientfd = new Socket[2];
            comIO = new Communication[2];
            for(int i = 0;i<2;i++){
                clientfd[i] = serverfd.accept();
                comIO[i] = new Communication(
                    new BufferedReader(new InputStreamReader(clientfd[i].getInputStream())),
                    new PrintWriter(clientfd[i].getOutputStream(),true));
                // Envoie un ACK
                //comIO[i].sndInt(0);
            }

            Jeu jeu = new Jeu();
            int couleur = r.nextInt(2) + 1; // 1 noir, 2 blanc
            Thread j1 = new hndlJoueur(comIO,jeu,couleur);
            Thread j2 = new hndlJoueur(comIO,jeu,(couleur % 2) + 1);
            
            j1.start();
            j2.start();
            
            while(!jeu.estFini()){}
            
        }
        catch(IOException e){ //Catch broken pipe ?
            System.err.println("Error server : " + e.getMessage());
        }
        finally{
            try{
                for(int i = 0;i<2;i++){
                    //comIO[i].close();
                    if(clientfd != null)
                        clientfd[i].close();
                }
                if(serverfd != null)
                    serverfd.close();
            }
            catch(IOException e){
                System.err.println("Error closing socket : " + e.getMessage());
            }
        }
    }
    
    private static class hndlJoueur extends Thread{
        private Jeu j;
        private final Communication[] comIO;
        private final int joueur;
        private final int jAdverse;
        
        public hndlJoueur(Communication[] io,Jeu j,int couleur){
            comIO = io;
            this.j = j;
            joueur = couleur - 1;
            jAdverse = (joueur + 1) % 2;
        }
        
        public void initJoueur(){
            Reponse rep = new Reponse(Communication.REP_ASSIGN_PLAYER,null);
            /*Envoie de la couleur a joueur*/
            comIO[joueur].sndRep(rep);
            
            /*Envoie joueur actif*/
            rep.repType = comIO[joueur].REP_TURN_UPDATE;
            TableauBit g = new TableauBit(1,0,0,0,0,0);
            rep.grille = g;
            comIO[joueur].sndRep(rep);
            
            /*Envoie du plateau initiale*/
            g = j.transformerGrilleEnLong();
            rep.grille = g;
            comIO[joueur].sndRep(rep);
        }
        
        /*Traite une request r et envoie une reponse aux clients*/
        public void traitementReq(Request r) throws IOException{
            synchronized(j){
                Reponse jMoi = new Reponse(-1,null);
                if ((joueur + 1 != j.getJoueur() && r.reqType == Communication.REQ_JOUE)
                        || (joueur + 1 == j.getJoueur() && r.reqType == comIO[joueur].REQ_ANNULE)){ // ou Si joueur (est actif) alors peut pas annuler
                    comIO[joueur].sndRep(jMoi); 
                    return;
                }
                Reponse jAdvrs = new Reponse(-1,null);

                switch(r.reqType){
                    case 1: //Joue un coup
                        if(r.coup != null){
                            if(j.deplacer(r.coup.a, r.coup.b) != null){ 
                                TableauBit g = j.transformerGrilleEnLong();
                                jMoi.repType = Communication.REP_BOARD_UPDATE;
                                jMoi.grille = g;
                                jAdvrs.repType = Communication.REP_BOARD_UPDATE;
                                jAdvrs.grille = g;

                                comIO[jAdverse].sndRep(jAdvrs);
                                comIO[joueur].sndRep(jMoi);

                                // Send turn update to both
                                Reponse turn = new Reponse(
                                    Communication.REP_TURN_UPDATE,
                                    new TableauBit(j.getJoueur(), 0, 0, 0, 0, 0)
                                );
                                comIO[0].sndRep(turn);
                                comIO[1].sndRep(turn);
                                return;
                            }
                        }
                        System.err.println("Coup invalid from client");
                        break;
                    case 2: //Annule un coup
                        jAdvrs.repType = Communication.REP_ANNULE;
                        comIO[jAdverse].sndRep(jAdvrs);
                        int repAdvrs = comIO[jAdverse].rcvInt();
                        if(repAdvrs == comIO[joueur].ACK){//Accept annulation
                            j.annuler();
                            TableauBit g = j.transformerGrilleEnLong();
                            jAdvrs.repType = Communication.REP_BOARD_UPDATE;
                            jAdvrs.grille = g;
                            jMoi.repType = Communication.REP_BOARD_UPDATE;
                            jMoi.grille = g;
                        }
                        else{
                            jMoi.repType = Communication.ERROR; // Pas d'annulation
                        }
                        break;
                    case 3: //Quit
                        jAdvrs.repType = Communication.REP_GAME_OVER; // send victory to jAdverse
                        jAdvrs.grille = new TableauBit(j.getJoueur(),0,0,0,0,0);
                        break;
                    default:
                        System.err.println("Unknown request");
                        break;
                }
                comIO[jAdverse].sndRep(jAdvrs);
                comIO[joueur].sndRep(jMoi);
            }
        }
        
        @Override
            public void run(){ // Catch exception ?
            try{
                initJoueur();
                while(!j.estFini()){
                    Request r = comIO[joueur].rcvReq();
                    traitementReq(r);
                }
                TableauBit tab = new TableauBit(j.getJoueur(),0,0,0,0,0);
                Reponse result = new Reponse(Communication.REP_GAME_OVER,tab);
                comIO[joueur].sndRep(result);
                
            }
            catch(IOException e){
                System.err.println("Error IO socket : " + e.getMessage());
            }
        }  
    }
}