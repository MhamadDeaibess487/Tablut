package Model;

import java.io.*;
import java.util.Stack;
import Config.Config;

public class Jeu extends BaseJeu {
    Stack<TableauBit> redo, undo;

    public Stack<TableauBit> getRedo() {
        return redo;
    }

    public Stack<TableauBit> getUndo() {
        return undo;
    }

    public Jeu() {
        redo = new Stack<>();
        undo = new Stack<>();

    }

    public Jeu(String fichier) { // Charge une sauvegarde
        redo = new Stack<>();
        undo = new Stack<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(Config.CHEMIN_SAUVEGARDES + fichier));
            String line;
            TableauBit tab;
            boolean inRedo = false;
            boolean inUndo = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                // else if(line.equalsIgnoreCase("GRILLE")){
                // }
                else if (line.equalsIgnoreCase("REDO")) {
                    inRedo = true;
                    inUndo = false;
                    continue;
                } else if (line.equalsIgnoreCase("UNDO")) {
                    inRedo = false;
                    inUndo = true;
                    continue;
                } else if (line.equalsIgnoreCase("JACTIF")) {
                    Joueur_actif = Integer.valueOf(br.readLine());
                    continue;
                }
                tab = lirePlateau(line);
                if (tab != null) {
                    if (inRedo)
                        redo.add(tab);
                    else if (inUndo)
                        undo.add(tab);
                    else
                        transformeLongEnGrille(tab);
                }
            }
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

    public void sauvegarder(TableauBit t) {
        undo.push(t);
        redo.clear();
    }

    @Override
    public TableauBit deplacer(Point a, Point b) {
        TableauBit tab = super.deplacer(a, b);
        if (tab == null) {
            return null;
        }
        sauvegarder(tab);
        changerJoueur();// regarder avant de jouer s'il existe des coups possibles(Plus de pions ou
                        // bloqué) + regarder draw après avoir fait un coup
        return tab;
    }

    public void ecritTabBit(PrintWriter pw, TableauBit tab) {
        pw.print(tab.noir1 + ", " + tab.noir2);
        pw.print(", " + tab.blanc1 + ", " + tab.blanc2 + ", ");
        pw.println(tab.roi1 + ", " + tab.roi2);
    }

    public void sauvegardeFichier(String fichier) {
        try {
            File file = new File(Config.CHEMIN_SAUVEGARDES + fichier);

            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("GRILLE");
                ecritTabBit(pw, transformerGrilleEnLong());
                pw.println("JACTIF\n" + Joueur_actif);
                Stack<TableauBit> tmp;
                for (int i = 0; i < 2; i++) {
                    if (i == 0) {
                        pw.println("REDO");
                        tmp = redo;
                    } else {
                        pw.println("UNDO");
                        tmp = undo;
                    }
                    for (TableauBit tab : tmp) {
                        ecritTabBit(pw, tab);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    public boolean annuler() {
        if (!undo.isEmpty()) {
            TableauBit last = undo.pop();
            redo.push(transformerGrilleEnLong());
            transformeLongEnGrille(last);
            changerJoueur();
            return true;
        }
        return false;
    }

    public boolean refaire() {
        if (!redo.isEmpty()) {
            TableauBit last = redo.pop();
            undo.push(transformerGrilleEnLong());
            transformeLongEnGrille(last);
            changerJoueur();
            return true;
        }
        return false;
    }

    private void reconstruireJeu() {
        transformeLongEnGrille(undo.peek());
    }

    private TableauBit lirePlateau(String line) {
        String[] parts = line.split(",");
        try {
            long n1, n2, b1, b2, r1, r2;
            n1 = Long.parseLong(parts[0].trim());
            n2 = Long.parseLong(parts[1].trim());
            b1 = Long.parseLong(parts[2].trim());
            b2 = Long.parseLong(parts[3].trim());
            r1 = Long.parseLong(parts[4].trim());
            r2 = Long.parseLong(parts[5].trim());
            return new TableauBit(n1, n2, b1, b2, r1, r2);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void nouvellePartie() {
        this.Joueur_actif = 1;
        this.grille = new int[Taille_plateau][Taille_plateau];
        ListePoint init = new ListePoint();
        this.ListePointN = init.ListeBaseNoir();
        this.ListePointB = init.ListeBaseBlanc();
        for (Point PB : ListePointB.LP) {
            grille[PB.l][PB.c] = 2;
        }
        grille[4][4] = 3;
        for (Point PN : ListePointN.LP) {
            grille[PN.l][PN.c] = 1;
        }
        // Clear undo/redo stacks
        this.redo.clear();
        this.undo.clear();
    }

    // draw doit être utilisé après chaque coup joué
    public boolean draw(TableauBit Last) {// retourne un boolean: true si on doit arreter la partie,faux sinon
        // je parcours la liste redo pour voir si la configuaration réapparait
        int nb = 0;
        for (TableauBit tmp : undo) {
            // System.out.println(tmp.blanc1 + " " + tmp.noir1);
            if (Last.compareTo(tmp) == 0) {
                nb++;
            }
            if (nb == 3) {
                return true;
            }

        }
        return false;

    }

}