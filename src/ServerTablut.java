import java.net.*;
import java.io.*;
import java.util.Random;
import Model.*;
import Utils.*;

/*Utilisé thread*/

public class ServerTablut{
    public static void main(String[] args){
        ServerSocket serverfd = null;
        Socket[] clientfd = null;
        Communication2[] comIO;
        Random r = new Random();
        try{
            /*Connexion des joueurs*/
            serverfd = new ServerSocket(2121);
            clientfd = new Socket[2];
            comIO = new Communication2[2];
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
            int couleur = r.nextInt(2) + 1; // 1 noir, 2 blanc
            Thread j1 = new hndlJoueur(comIO,jeu,couleur);
            Thread j2 = new hndlJoueur(comIO,jeu,couleur);
            
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
                    comIO[i].close();
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
        private final Communication2[] comIO;
        private final int joueur;
        private final int jAdverse;
        
        public hndlJoueur(Communication2[] io,Jeu j,int couleur){
            comIO = io;
            this.j = j;
            joueur = couleur - 1;
            jAdverse = couleur;
        }
        
        
        /*Traite une request r et envoie une reponse aux clients*/
        public synchronized void traitementReq(Request r){
            Reponse jActif = new Reponse(-1,null);
            if((jAdverse == j.getJoueur() && r.reqType == 1)// Si joueur (non actif) envoie une requete autre que annule
                    || (joueur == j.getJoueur() && r.reqType == 2)){ // ou Si joueur (est actif) alors peut pas annuler
                comIO[joueur].sndRep(jActif); 
                return;
            }
            Reponse jAdvrs = new Reponse(-1,null);

            switch(r.reqType){
                case 1: /*Joue un coup*/
                    if(r.coup != null){
                        if(j.deplacer(r.coup.a, r.coup.b) != null){ /*Si le deplacement à eu lieu*/
                            jActif.repType = 0; // Ack

                            /*Propagation du deplacement au joueur adverse*/
                            jAdvrs.repType = 1; // Update
                            jAdvrs.grille = j.transformerGrilleEnLong();
                            break;
                        }
                    }
                    System.err.println("Coup invalid from client");
                    break;
                case 2: /*Annule un coup*/
                    jAdvrs.repType = 2;
                    comIO[jAdverse].sndRep(jAdvrs);
                    int repAdvrs = comIO[jAdverse].rcvInt();
                    if(repAdvrs == 1){//Accept annulation
                        j.annuler();
                        TableauBit g = j.transformerGrilleEnLong();
                        jAdvrs.repType = 1;
                        jAdvrs.grille = g;
                        jActif.repType = 1;
                        jActif.grille = g;
                    }
                    else{
                        jActif.repType = -1; // Pas d'annulation
                    }
                    break;
                case 3: /*Quit*/
                    jAdvrs.repType = 3; // send victory to jAdverse
                    break;
                default:
                    System.err.println("Unknown request");
                    break;
            }
            comIO[jAdverse].sndRep(jAdvrs);
            comIO[joueur].sndRep(jActif);
        }
        
        @Override
        public void run(){ // Catch exception ?
            try{
                while(!j.estFini()){
                    Request r = comIO[joueur].rcvReq();
                    traitementReq(comIO,r,j,joueur);
                }
                Reponse result = new Reponse(3 + j.getJoueur() == jAdverse,null);
                comIO[joueur].sndRep(result);
            }
            catch(IOException e){ // A enlever ?
                System.err.println("Error IO socket : " + e.getMessage());
            }
        }  
    }
}