package tablut.tests;

import tablut.model.*;
import java.nio.file.*;
import java.io.UncheckedIOException;
import static tablut.tests.TestSupport.*;

public class TestPersistence {
    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("tablut-saves-");
        System.setProperty("tablut.saves.dir", directory.toString());
        try {
            Jeu game = new Jeu();
            game.setPseudo(1, "Élodie");
            game.setPseudo(2, "Player Two");
            move(game,0,3,1,3);
            move(game,2,4,2,3);
            check(game.annuler(), "Setup undo failed");
            game.sauvegardeFichier("round-trip");
            Jeu loaded = new Jeu("round-trip");
            check(loaded.isLoaded && loaded.getJoueur() == game.getJoueur(), "Load lost active player");
            check(loaded.transformerGrilleEnLong().compareTo(game.transformerGrilleEnLong()) == 0, "Load changed board");
            check(loaded.getUndo().size() == game.getUndo().size() && loaded.getRedo().size() == game.getRedo().size(), "Load lost histories");
            check(loaded.getPseudo(1).equals("Élodie"), "UTF-8 name not restored");
            check(loaded.refaire() && game.refaire() && loaded.transformerGrilleEnLong().compareTo(game.transformerGrilleEnLong()) == 0, "Loaded redo failed");

            Jeu terminal = kingCapture();
            move(terminal,4,7,4,5);
            terminal.sauvegardeFichier("terminal");
            Jeu restored = new Jeu("terminal");
            check(restored.getFini() && restored.getResultatPartie() == BaseJeu.RESULTAT_NOIRS, "Terminal metadata lost");
            check(restored.annuler() && !restored.getFini(), "Loaded terminal undo failed");
            Jeu drawn = new Jeu();
            drawn.setResultatPartie(BaseJeu.RESULTAT_NULLE);
            drawn.terminerPartie();
            drawn.sauvegardeFichier("draw");
            check(new Jeu("draw").getResultatPartie() == BaseJeu.RESULTAT_NULLE, "Draw metadata lost");

            int fixtures = 0;
            for (String name : java.util.List.of("black;1;0","black;1;1","black;2;0","black;3;0",
                    "white;1;0","white;1;1","white;2;0","white;2;1","white;3;0","bugIAnoirJoueBlanc")) {
                Path file = Path.of("res/saves",name);
                Files.copy(file, directory.resolve(file.getFileName()));
                check(new Jeu(file.getFileName().toString()).isLoaded, "Legacy save failed: " + file);
                fixtures++;
            }
            check(fixtures == 10, "Expected ten university save fixtures");
            Files.writeString(directory.resolve("invalid"), "GRILLE\n1,2\n");
            expectInvalid("invalid");
            Files.writeString(directory.resolve("empty"), "");
            expectInvalid("empty");
            expectInvalid("../outside");
            try { new Jeu("missing"); throw new AssertionError("Missing save silently accepted"); }
            catch (UncheckedIOException expected) { }
            System.out.println("TestPersistence OK (round-trip, terminal state, ten legacy saves, invalid input)");
        } finally {
            try (var files = Files.list(directory)) {
                for (Path path : files.toList()) Files.delete(path);
            }
            Files.delete(directory);
        }
    }

    private static void expectInvalid(String filename) {
        try { new Jeu(filename); throw new AssertionError("Invalid save accepted: " + filename); }
        catch (IllegalArgumentException expected) { }
    }
}
