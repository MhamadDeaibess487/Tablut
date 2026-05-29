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
        
        public void initJoueur() {
            comIO[joueur].sndRep(new Reponse(
                Communication.REP_ASSIGN_PLAYER,
                new TableauBit(joueur + 1, 0, 0, 0, 0, 0)
            ));

            comIO[joueur].sndRep(new Reponse(
                Communication.REP_TURN_UPDATE,
                new TableauBit(j.getJoueur(), 0, 0, 0, 0, 0)
            ));

            comIO[joueur].sndRep(new Reponse(
                Communication.REP_BOARD_UPDATE,
                j.transformerGrilleEnLong()
            ));
        }
                
        /*Traite une request r et envoie une reponse aux clients*/
        public void traitementReq(Request r) throws IOException{
            synchronized(j){
                if (r == null) {
                    comIO[joueur].sndRep(new Reponse(Communication.ERROR, null));
                    return;
                }

                Reponse jMoi = new Reponse(Communication.ERROR, null);
                Reponse jAdvrs = new Reponse(Communication.ERROR, null);

                if ((joueur + 1 != j.getJoueur() && r.reqType == Communication.REQ_JOUE)
                        || (joueur + 1 == j.getJoueur() && r.reqType == Communication.REQ_ANNULE)) {
                    comIO[joueur].sndRep(jMoi);
                    return;
                }

                switch(r.reqType){
                    case Communication.REQ_JOUE:
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

                                if (j.estFini()) {
                                    envoyerFinDePartie(determinerGagnantApresCoup(r.coup.joueur));
                                }
                                return;
                            }
                        }
                        System.err.println("Coup invalid from client");
                        break;
                    case Communication.REQ_ANNULE:
                        if (j.getUndo().isEmpty()) {
                            jAdvrs.repType = Communication.ACK;
                            jAdvrs.grille = null;
                            jMoi.repType = Communication.ERROR;
                            break;
                        }
                        jAdvrs.repType = Communication.REP_ANNULE;
                        comIO[jAdverse].sndRep(jAdvrs);
                        int repAdvrs = comIO[jAdverse].rcvInt();
                        if(repAdvrs == Communication.ACK){
                            if (!j.annuler()) {
                                jAdvrs.repType = Communication.ACK;
                                jAdvrs.grille = null;
                                jMoi.repType = Communication.ERROR;
                                break;
                            }
                            TableauBit g = j.transformerGrilleEnLong();
                            jAdvrs.repType = Communication.REP_BOARD_UPDATE;
                            jAdvrs.grille = g;
                            jMoi.repType = Communication.REP_BOARD_UPDATE;
                            jMoi.grille = g;
                            comIO[jAdverse].sndRep(jAdvrs);
                            comIO[joueur].sndRep(jMoi);

                            Reponse turn = new Reponse(
                                Communication.REP_TURN_UPDATE,
                                new TableauBit(j.getJoueur(), 0, 0, 0, 0, 0)
                            );
                            comIO[0].sndRep(turn);
                            comIO[1].sndRep(turn);
                            return;
                        }
                        else{
                            jAdvrs.repType = Communication.ACK;
                            jAdvrs.grille = null;
                            jMoi.repType = Communication.ERROR; // Pas d'annulation
                        }
                        break;
                    case Communication.REQ_QUIT:
                    case Communication.REQ_ABANDONNE:
                        j.terminerPartie();
                        envoyerFinDePartie(jAdverse + 1);
                        return;
                    case Communication.REQ_NUL:
                        jAdvrs.repType = Communication.REP_NUL;
                        comIO[jAdverse].sndRep(jAdvrs);
                        int repNul = comIO[jAdverse].rcvInt();
                        if (repNul == Communication.ACK) {
                            j.terminerPartie();
                            envoyerFinDePartie(0);
                            return;
                        }
                        jAdvrs.repType = Communication.ACK;
                        jAdvrs.grille = null;
                        jMoi.repType = Communication.ERROR;
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
                
            }
            catch(IOException e){
                if (!"Connexion fermée".equals(e.getMessage())) {
                    System.err.println("Error IO socket : " + e.getMessage());
                }
            }
        }

        private int determinerGagnantApresCoup(int joueurQuiAJoue) {
            int[][] grille = j.getGrille();
            boolean roiPresent = false;

            for (int l = 0; l < grille.length; l++) {
                for (int c = 0; c < grille[l].length; c++) {
                    if (grille[l][c] == 3) {
                        roiPresent = true;
                        if (l == 0 || l == grille.length - 1 || c == 0 || c == grille[l].length - 1) {
                            return 2;
                        }
                    }
                }
            }

            if (!roiPresent) {
                return 1;
            }

            return joueurQuiAJoue;
        }

        private void envoyerFinDePartie(int resultat) {
            Reponse fin = new Reponse(
                Communication.REP_GAME_OVER,
                new TableauBit(resultat, 0, 0, 0, 0, 0)
            );
            comIO[0].sndRep(fin);
            comIO[1].sndRep(fin);
        }
    }  
}
