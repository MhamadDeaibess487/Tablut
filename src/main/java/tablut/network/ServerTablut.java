package tablut.network;
import java.net.*;
import java.io.*;
import java.util.Random;
import tablut.model.*;
import tablut.network.protocol.Request;
import tablut.network.protocol.Reponse;
import tablut.network.protocol.Communication;


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
            AttenteReponse attenteReponse = new AttenteReponse();
            Thread j1 = new hndlJoueur(comIO,clientfd,jeu,couleur, attenteReponse);
            Thread j2 = new hndlJoueur(comIO,clientfd,jeu,(couleur % 2) + 1, attenteReponse);
            
            j1.start();
            j2.start();
            
            j1.join();
            j2.join();
            
        }
        catch(IOException e){ //Catch broken pipe ?
            System.err.println("Error server : " + e.getMessage());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally{
            try{
                for(int i = 0;i<2;i++){
                    //comIO[i].close();
                    if(clientfd != null && clientfd[i] != null)
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
        private final Socket[] sockets;
        private final int joueur;
        private final int jAdverse;
        private final AttenteReponse attenteReponse;
        
        public hndlJoueur(Communication[] io,Socket[] sockets,Jeu j,int couleur, AttenteReponse attenteReponse){
            comIO = io;
            this.sockets = sockets;
            this.j = j;
            joueur = couleur - 1;
            jAdverse = (joueur + 1) % 2;
            this.attenteReponse = attenteReponse;
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
                                    envoyerFinDePartie(j.getResultatPartie());
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
                        attenteReponse.demarrerAttente(jAdverse);
                        comIO[jAdverse].sndRep(jAdvrs);
                        int repAdvrs = attenteReponse.attendreReponse(jAdverse);
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
                        attenteReponse.demarrerAttente(jAdverse);
                        comIO[jAdverse].sndRep(jAdvrs);
                        int repNul = attenteReponse.attendreReponse(jAdverse);
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
                    Request r = lireMessage();
                    if (r == null) {
                        continue;
                    }
                    traitementReq(r);
                }
                
            }
            catch(IOException e){
                if (!"Connexion fermée".equals(e.getMessage())) {
                    System.err.println("Error IO socket : " + e.getMessage());
                }
            }
            finally {
                // Wake an outstanding undo/draw negotiation before taking the game lock.
                attenteReponse.annulerAttente();
                synchronized (j) { j.terminerPartie(); }
                for (Socket socket : sockets) {
                    try { if (socket != null) socket.close(); }
                    catch (IOException e) { System.err.println("Error closing client: " + e.getMessage()); }
                }
            }
        }

        private Request lireMessage() throws IOException {
            int type = comIO[joueur].rcvInt();

            if (type == Communication.ACK || type == Communication.ERROR) {
                if (attenteReponse.enAttentePour(joueur)) {
                    attenteReponse.enregistrerReponse(joueur, type);
                    return null;
                }
                return null;
            }

            if (type < Communication.REQ_JOUE || type > Communication.REQ_NUL) {
                System.err.println("Bad request");
                return null;
            }

            int coupSerialise = comIO[joueur].rcvInt();
            return new Request(type, comIO[joueur].deserializeCoup(coupSerialise));
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

    private static class AttenteReponse {
        private int joueurAttendu = -1;
        private Integer reponse = null;

        public synchronized void annulerAttente() {
            reponse = Communication.ERROR;
            joueurAttendu = -1;
            notifyAll();
        }

        public synchronized void demarrerAttente(int joueur) {
            joueurAttendu = joueur;
            reponse = null;
        }

        public synchronized boolean enAttentePour(int joueur) {
            return joueurAttendu == joueur && reponse == null;
        }

        public synchronized void enregistrerReponse(int joueur, int valeur) {
            if (joueurAttendu != joueur || reponse != null) {
                return;
            }
            reponse = valeur;
            notifyAll();
        }

        public synchronized int attendreReponse(int joueur) {
            while (joueurAttendu == joueur && reponse == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return Communication.ERROR;
                }
            }

            int resultat = reponse == null ? Communication.ERROR : reponse;
            joueurAttendu = -1;
            reponse = null;
            return resultat;
        }
    }  
}
