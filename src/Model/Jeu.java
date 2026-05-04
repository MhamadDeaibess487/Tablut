package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;
import java.io.FileNotFoundException;

public class Jeu extends BaseJeu {
    Stack<Coup> redo, undo;

    public Jeu() {
        redo = new Stack<>();
        undo = new Stack<>();
    }

    public Jeu(String fichier) { // Charge une sauvegarde
        redo = new Stack<>();
        undo = new Stack<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fichier));
            String line;
            boolean inRedo = false;
            boolean inUndo = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.equalsIgnoreCase("REDO")) {
                    inRedo = true;
                    inUndo = false;
                    continue;
                }
                if (line.equalsIgnoreCase("UNDO")) {
                    inRedo = false;
                    inUndo = true;
                    continue;
                }
                Coup coup = lireCoup(line);
                if (coup != null) {
                    if (inRedo) {
                        redo.add(coup);
                    } else if (inUndo) {
                        undo.add(coup);
                    }
                }
            }
            reconstruireJeu();
        } catch (FileNotFoundException e) {
            System.err.println("Fichier introuvable");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement : " + e.getMessage());
        }
    }

    @Override
    void jeuRedondant() {
        System.err.println("Jeu redondant non implem");
        System.exit(0);
    }

    public void sauvegarder(Point a, Point b) {
        undo.push(new Coup(a, b, Joueur_actif));
        redo.clear();
    }

    @Override
    public boolean deplacer(Point a, Point b) {
        if (!super.deplacer(a, b)) {
            return false;
        }
        sauvegarder(a, b);
        return true;
    }

    public void sauvegardeFichier(String fichier) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fichier))) {
            pw.println("REDO");
            for (Coup c : redo) {
                pw.println(c.a.l + ", " + c.a.c + ";" + c.b.l + ", " + c.b.c + ";" + c.joueur);
                pw.println();
            }

            pw.println("UNDO");
            for (Coup c : undo) {
                pw.println(c.a.l + ", " + c.a.c + ";" + c.b.l + ", " + c.b.c + ";" + c.joueur);
                pw.println();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    public void annuler() {
        if (!undo.isEmpty()) {
            Coup last = undo.pop();
            redo.push(last);
            super.deplacer(last.b, last.a);
        }
    }

    public void refaire() {
        if (!redo.isEmpty()) {
            Coup last = redo.pop();
            undo.push(last);
            super.deplacer(last.a, last.b);
        }
    }

    private void reconstruireJeu() {
        for (Coup c : undo)
            super.deplacer(c.a, c.b);
    }

    private Coup lireCoup(String line) {
        String[] parts = line.split(";");
        String[] pa = parts[0].split(",");
        String[] pb = parts[1].split(",");

        try {
            int i1, j1, i2, j2;
            i1 = Integer.parseInt(pa[0].trim());
            j1 = Integer.parseInt(pa[1].trim());
            i2 = Integer.parseInt(pb[0].trim());
            j2 = Integer.parseInt(pb[1].trim());
            int joueur = Integer.parseInt(parts[2].trim());
            return new Coup(new Point(i1, j1), new Point(i2, j2), joueur);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}