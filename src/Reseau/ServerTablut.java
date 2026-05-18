import java.net.*;
import java.io.*;
import java.util.Random;
import Model.*;
import Utils.*;


public class ServerTablut{
    public static void main(String[] args){
        ServerSocket serverfd = null;
        Socket[] clientfd = null;
        Communication[] comIO;
        Random r = new Random();
        try{
            /*Connexion des joueurs*/
            serverfd = new ServerSocket(2121);
            clientfd = new Socket[2];
            comIO = new Communication[2];
            for(int i = 0;i<2;i++){
                clientfd[i] = serverfd.accept();
                comIO[i] = new Communication(
                    new BufferedReader(new InputStreamReader(clientfd[i].getInputStream())),
                    new PrintWriter(clientfd[i].getOutputStream(),true));
                /*bit ACK*/
                comIO[i].sndInt(0);
            }
            /*Fin connexion*/
            
            /*Lancement du jeu*/
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
//                    comIO[i].close();
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
            jAdverse = couleur; // 1 ou 2 = index error ?
        }
        
        public void initJoueur(){
            Reponse rep = new Reponse(comIO[joueur].REP_ASSIGN_PLAYER,null);
            /*Envoie de la couleur a joueur*/
            comIO[joueur].sndRep(rep);
            
            /*Envoie joueur actif*/
            rep.repType = comIO[joueur].REP_TURN_UPDATE;
            TableauBit g = new TableauBit(1,0,0,0,0,0);
            comIO[joueur].sndRep(rep);
            
            /*Envoie du plateau initiale*/
            g = j.transformerGrilleEnLong();
            rep.grille = g;
            comIO[joueur].sndRep(rep);
        }
        
        /*Traite une request r et envoie une reponse aux clients*/
        public synchronized void traitementReq(Request r) throws IOException{
            Reponse jMoi = new Reponse(-1,null);
            if((jAdverse == j.getJoueur() && r.reqType == comIO[joueur].REQ_JOUE)// Si joueur (non actif) envoie une requete joue
                    || (joueur == j.getJoueur() && r.reqType == comIO[joueur].REQ_ANNULE)){ // ou Si joueur (est actif) alors peut pas annuler
                comIO[joueur].sndRep(jMoi); 
                return;
            }
            Reponse jAdvrs = new Reponse(-1,null);

            switch(r.reqType){
                case 1: /*Joue un coup*/
                    if(r.coup != null){
                        if(j.deplacer(r.coup.a, r.coup.b) != null){ /*Si le deplacement à eu lieu*/
                            jMoi.repType = comIO.ACK; // Ack

                            /*Propagation du deplacement au joueur adverse*/
                            jAdvrs.repType = comIO.REP_BOARD_UPDATE; // Update
                            jAdvrs.grille = j.transformerGrilleEnLong();
                            break;
                        }
                    }
                    System.err.println("Coup invalid from client");
                    break;
                case 2: /*Annule un coup*/
                    jAdvrs.repType = comIO.REP_ANNULE;
                    comIO[jAdverse].sndRep(jAdvrs);
                    int repAdvrs = comIO[jAdverse].rcvInt();
                    if(repAdvrs == comIO[joueur].ACK){//Accept annulation
                        j.annuler();
                        TableauBit g = j.transformerGrilleEnLong();
                        jAdvrs.repType = comIO.REP_BOARD_UPDATE;
                        jAdvrs.grille = g;
                        jMoi.repType = comIO.REP_BOARD_UPDATE;
                        jMoi.grille = g;
                    }
                    else{
                        jMoi.repType = comIO.ERROR; // Pas d'annulation
                    }
                    break;
                case 3: /*Quit*/
                    jAdvrs.repType = comIO.REP_GAME_OVER; // send victory to jAdverse
                    jAdvrs.grille = new TableauBit(j.getJoueur(),0,0,0,0,0);
                    break;
                default:
                    System.err.println("Unknown request");
                    break;
            }
            comIO[jAdverse].sndRep(jAdvrs);
            comIO[joueur].sndRep(jMoi);
        }
        
        @Override
        public void run(){ // Catch exception ?
            try{
                initJoueur();
                /*Boucle d'ecoute des requetes*/
                while(!j.estFini()){
                    Request r = comIO[joueur].rcvReq();
                    traitementReq(r);
                }
                TableauBit tab = new TableauBit(j.getJoueur(),0,0,0,0,0);
                Reponse result = new Reponse(comIO.REP_GAME_OVER,tab);
                comIO[joueur].sndRep(result);
                
            }
            catch(IOException e){
                System.err.println("Error IO socket : " + e.getMessage());
            }
        }  
    }
}