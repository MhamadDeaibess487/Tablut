package Model;
// Classe Message simple pour structurer les échanges
public class Message {

    private String type;
    private String content;
    long blanc1,noir1,roi1;
    long blanc2,noir2,roi2;
    //taille offset etc? 

    public Message(String type, String content) {
        this.type = type;
        this.content = content;
    }

    // Sérialisation pour envoi sur le réseau
    public String toNetworkString() {
        return type + ":" + content;
    }

    // Désérialisation à la réception
    public static Message parseFromString(String raw) {
        String[] parts = raw.split(":", 2);
        if (parts.length == 2) {
            return new Message(parts[0], parts[1]);
        }
        return new Message("UNKNOWN", raw);
    }

    public String getType()    { return type; }
    public String getContent() { return content; }
}