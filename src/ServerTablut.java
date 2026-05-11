//import java.net.*;
//import java.io.*;
//import Model.*;
//import Utils.*;
//
//public class ServerTablut{
//    
//    public void traitementReq(Request r){
//        switch(r.getType()){
//            case 1:
//                
//                break;
//            case 2:
//                break;
//                
//            case 3:
//                break;
//            default:
//                break;
//        }
//    }
//    
//    public static void main(String[] args){
//        try{
//            /*Connexion des joueurs*/
//            ServerSocket serverfd = new ServerSocket(2121);
//            Socket[] clientfd = new Socket[2];
//            Communication[] comIO = new Communication[2];
////            PrintWriter[] out = new PrintWriter[2];
////            BufferedReader[] in = new BufferedReader[2];
//            for(int i = 0;i<2;i++){
//                clientfd[i] = serverfd.accept();
//                comIO[i] = new Communication(
//                    new InputStreamReader(clientfd[i].getInputStream()),
//                    new PrintWriter(clientfd[i].getOutputStream(),true));
////                out[i] = new PrintWriter(clientfd[i].getOutputStream(),true);
////                in[i] = new BufferedReader(new InputStreamReader(clientfd[i].getInputStream()));
//                /*bit ACK*/
//                comIO[i].sndAck();
//            }
//            /*Fin connexion*/
//            
//            /*Lancement du jeu*/
//            BaseJeu jeu = new Jeu();
//            
//            /*Boucle du jeu*/
//            while(!jeu.estFini()){
//                Request r = comIO[jeu.getJoueur()].rcvReq();
//                comIO[jeu.getJoueur()].sndAck();
//                
//                int jAdverse = (jeu.getJoueur() % 2) + 1;
//            }
//        }
//        catch(IOException e){
//            System.err.println("Error socket : " + e.getMessage());
//        }
//    }
//}