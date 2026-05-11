package Model;
import java.net.*;
import java.io.*;

public class Client {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    // Thread de réception des messages du serveur
    private Thread receiverThread;

    public Client(String serverAddress, int port) throws IOException {
        // Connexion au serveur central
        this.socket = new Socket(serverAddress, port);
        this.reader = new BufferedReader(
            new InputStreamReader(socket.getInputStream())
        );
        this.writer = new PrintWriter(
            new OutputStreamWriter(socket.getOutputStream()), true
        );

        System.out.println("Connecté au serveur : " + serverAddress + ":" + port);
    }

    // Démarrer le thread de réception (écoute le serveur en continu)
    public void startReceiver() {
        receiverThread = new Thread(() -> {
            try {
                String ligne;
                while ((ligne = reader.readLine()) != null) {
                    Message msg = Message.parseFromString(ligne);
                    handleServerMessage(msg);
                }
            } catch (IOException e) {
                System.err.println("Connexion perdue avec le serveur.");
            }
        });
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    // Traitement d'un message reçu du serveur → mise à jour GUI
    private void handleServerMessage(Message msg) {
        switch (msg.getType()) {
            case "UPDATE":
                // Mise à jour de l'interface graphique (thread Swing)

            case "GAME_OVER": //base jeu bool =  fini
                break;

            default:
                System.err.println("Message inconnu : " + msg.getType());
        }
    }

    // Envoi d'une action joueur → message vers le serveur
    public void sendAction(String action) {
        Message msg = new Message("ACTION", action);
        writer.println(msg.toNetworkString()); 
    }

    // Mise à jour de l'interface graphique
    private void updateGraphique(Message msg) {
    }

    private void showGameOver(String result) {
    }

    public void disconnect() throws IOException {
        socket.close();
    }

    // // Point d'entrée
    // public static void main(String[] args) throws IOException {
    //     Client client = new Client("localhost", 12345);
    //     client.startReceiver();

    //     // Simulation d'une action joueur (normalement déclenchée par la GUI)
    //     client.sendAction("MOVE:3:4");
    // }
}