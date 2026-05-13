package Model;

public class Message {
    private String type;
    private String content;

    public Message(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public static Message move(int fromL, int fromC, int toL, int toC) {
        return new Message("MOVE", fromL + "," + fromC + "," + toL + "," + toC);
    }

    public static Message boardUpdate(TableauBit t) {
        String content =
            t.noir1 + "," + t.noir2 + "," +
            t.blanc1 + "," + t.blanc2 + "," +
            t.roi1 + "," + t.roi2;

        return new Message("BOARD_UPDATE", content);
    }

    public static Message turn(int joueur) {
        return new Message("TURN", String.valueOf(joueur));
    }

    public static Message assignPlayer(int joueur) {
        return new Message("ASSIGN_PLAYER", String.valueOf(joueur));
    }

    public static Message invalidMove(String reason) {
        return new Message("INVALID_MOVE", reason);
    }

    public static Message gameOver(String result) {
        return new Message("GAME_OVER", result);
    }

    public String toNetworkString() {
        return type + ":" + content;
    }

    public static Message parseFromString(String raw) {
        if (raw == null) return null;

        String[] parts = raw.split(":", 2);

        if (parts.length != 2) {
            return new Message("UNKNOWN", raw);
        }

        return new Message(parts[0], parts[1]);
    }

    public TableauBit getTableauBit() {
        if (!type.equals("BOARD_UPDATE")) return null;

        String[] values = content.split(",");

        if (values.length != 6) return null;

        TableauBit t = new TableauBit();

        t.noir1 = Long.parseLong(values[0]);
        t.noir2 = Long.parseLong(values[1]);
        t.blanc1 = Long.parseLong(values[2]);
        t.blanc2 = Long.parseLong(values[3]);
        t.roi1 = Long.parseLong(values[4]);
        t.roi2 = Long.parseLong(values[5]);

        return t;
    }

    public int[] getMove() {
        if (!type.equals("MOVE")) {
            return null;
        }

        String[] values = content.split(",");

        if (values.length != 4) {
            return null;
        }

        return new int[] {
            Integer.parseInt(values[0]),
            Integer.parseInt(values[1]),
            Integer.parseInt(values[2]),
            Integer.parseInt(values[3])
        };
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }


    public static Message move(Coup c) {
        return new Message("MOVE", String.valueOf(serializeCoup(c)));
    }

    public Coup getCoup() {
        if (!type.equals("MOVE")) return null;
        int value = Integer.parseInt(content);
        return deserializeCoup(value);
    }

    private static int serializeCoup(Coup c) {
        if (c == null) return Integer.MAX_VALUE;
        return c.joueur | (c.a.l << 13) | (c.a.c << 9) | (c.b.l << 5) | (c.b.c << 1);
    }

    private static Coup deserializeCoup(int value) {
        if (value == Integer.MAX_VALUE) return null;

        int joueur = value & 1;
        Point a = new Point(
            (value >> 13) & 0xF,
            (value >> 9) & 0xF
        );
        Point b = new Point(
            (value >> 5) & 0xF,
            (value >> 1) & 0xF
        );
        return new Coup(a, b, joueur);
    }
}