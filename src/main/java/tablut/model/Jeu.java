package tablut.model;

import java.io.*;
import java.util.Stack;
import tablut.config.Config;

public class Jeu extends BaseJeu {
    Stack<TableauBit> redo, undo;
    String pseudo1 = "Joueur 1",pseudo2 = "Joueur 2";
    public boolean isLoaded = false;

    public Jeu() {
        redo = new Stack<>();
        undo = new Stack<>();
    }

    // fonction pour faire copie du jeu pour l'ia
    public Jeu(Jeu autre) {
        redo = new Stack<>();
        undo = new Stack<>();
        pseudo1 = autre.pseudo1;
        pseudo2 = autre.pseudo2;
        this.grille = new int[autre.grille.length][autre.grille[0].length];
        for (int i = 0; i < autre.grille.length; i++) {
            for (int j = 0; j < autre.grille[i].length; j++) {
                this.grille[i][j] = autre.grille[i][j];
            }
        }
        
        this.ListePointB = new ListePoint();
        for (Point p : autre.ListePointB.LP) {
            this.ListePointB.AddListePoint(new Point(p.l, p.c));
        }
        this.ListePointN = new ListePoint();
        for (Point p : autre.ListePointN.LP) {
            this.ListePointN.AddListePoint(new Point(p.l, p.c));
        }
        
        this.Joueur_actif = autre.Joueur_actif;
        this.fini = autre.fini;
        this.resultatPartie = autre.resultatPartie;
        this.isLoaded = autre.isLoaded;
        this.trone = new Point(autre.trone.l, autre.trone.c);
    }

    public Jeu(String fichier) { // Charge une sauvegarde
        redo = new Stack<>();
        undo = new Stack<>();

        try (BufferedReader br = java.nio.file.Files.newBufferedReader(cheminSauvegarde(fichier))) {
            String line;
            TableauBit tab;
            boolean inRedo = false;
            boolean inUndo = false;
            boolean plateauLu = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                else if (line.equalsIgnoreCase("J1")){
                    pseudo1 = br.readLine();
                    continue;
                }
                else if (line.equalsIgnoreCase("J2")){
                    pseudo2 = br.readLine();
                    continue;
                }
                else if (line.equalsIgnoreCase("REDO")) {
                    inRedo = true;
                    inUndo = false;
                    continue;
                } else if (line.equalsIgnoreCase("UNDO")) {
                    inRedo = false;
                    inUndo = true;
                    continue;
                } else if (line.equalsIgnoreCase("JACTIF")) {
                    Joueur_actif = Integer.parseInt(br.readLine());
                    if (Joueur_actif != 1 && Joueur_actif != 2) {
                        throw new IllegalArgumentException("Invalid active player in save file");
                    }
                    continue;
                } else if (line.equalsIgnoreCase("RESULTAT")) {
                    resultatPartie = Integer.parseInt(br.readLine());
                    if (resultatPartie < RESULTAT_AUCUN || resultatPartie > RESULTAT_NULLE) {
                        throw new IllegalArgumentException("Invalid game result in save file");
                    }
                    fini = resultatPartie != RESULTAT_AUCUN;
                    continue;
                }
                tab = lirePlateau(line);
                if (tab != null) {
                    if (inRedo)
                        redo.add(tab);
                    else if (inUndo)
                        undo.add(tab);
                    else {
                        transformeLongEnGrille(tab);
                        plateauLu = true;
                    }
                }
            }
            if (!plateauLu) throw new IllegalArgumentException("Save file contains no board");
            if (pseudo1 == null || pseudo2 == null) throw new IllegalArgumentException("Missing player name in save file");
            actualiserResultat();
            isLoaded = true;
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load save: " + fichier, e);
        }
    }
    
    public Stack<TableauBit> getRedo() {
        return redo;
    }

    public Stack<TableauBit> getUndo() {
        return undo;
    }
    
    // J1 = 1; J2 = 2
    public void setPseudo(int i,String tag){
        switch(i){
            case 1:
                pseudo1 = tag;
                break;
            case 2:
                pseudo2 = tag;
                break;
            default:
                System.err.println("No pseudo for Joueur "+i);
                break;
        }
    }
    
    // J1 = 1; J2 = 2
    public String getPseudo(int i){
        switch(i){
            case 1:
                return pseudo1;
            case 2:
                return pseudo2;
            default:
                return "Unknown Joueur " + i;
        }
    }
    
    

    @Override
    public boolean jeuRedondant() {  // true si on doit arreter la partie,faux sinon
        TableauBit position = transformerGrilleEnLong();
        return undo.stream().filter(avant -> position.compareTo(avant) == 0).limit(2).count() == 2;
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
        actualiserResultat();
        return tab;
    }

    public void ecritTabBit(PrintWriter pw, TableauBit tab) {
        pw.print(tab.noir1 + ", " + tab.noir2);
        pw.print(", " + tab.blanc1 + ", " + tab.blanc2 + ", ");
        pw.println(tab.roi1 + ", " + tab.roi2);
    }

    public void sauvegardeFichier(String fichier) {
        try {
            File file = cheminSauvegarde(fichier).toFile();

            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            try (PrintWriter pw = new PrintWriter(java.nio.file.Files.newBufferedWriter(file.toPath()))) {
                pw.println("GRILLE");
                ecritTabBit(pw, transformerGrilleEnLong());
                pw.println("JACTIF\n" + Joueur_actif);
                pw.println("RESULTAT\n" + resultatPartie);
                pw.println("J1\n" + pseudo1);
                pw.println("J2\n" + pseudo2);
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
                if (pw.checkError()) throw new IOException("Failed to write save file");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write save: " + fichier, e);
        }
    }

    public boolean annuler() {
        if (!undo.isEmpty()) {
            TableauBit last = undo.pop();
            redo.push(transformerGrilleEnLong());
            transformeLongEnGrille(last);
            fini = false;
            resultatPartie = RESULTAT_AUCUN;
            changerJoueur();
            actualiserResultat();
            return true;
        }
        return false;
    }

    public boolean refaire() {
        if (!redo.isEmpty()) {
            TableauBit last = redo.pop();
            undo.push(transformerGrilleEnLong());
            transformeLongEnGrille(last);
            fini = false;
            resultatPartie = RESULTAT_AUCUN;
            changerJoueur();
            actualiserResultat();
            draw();
            return true;
        }
        return false;
    }

    private TableauBit lirePlateau(String line) {
        if (line.equalsIgnoreCase("GRILLE")) return null;
        String[] parts = line.split(",");
        if (parts.length != 6) throw new IllegalArgumentException("Invalid board record in save file");
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
            throw new IllegalArgumentException("Invalid board record in save file", e);
        }
    }

    private static java.nio.file.Path cheminSauvegarde(String fichier) {
        if (fichier == null || fichier.isBlank() || fichier.equals(".") || fichier.equals("..")
                || fichier.contains("/") || fichier.contains("\\")) {
            throw new IllegalArgumentException("Use a nonempty save filename without directory separators");
        }
        return java.nio.file.Path.of(Config.CHEMIN_SAUVEGARDES).resolve(fichier);
    }

    /** Restore terminal metadata from a snapshot, preserving the existing rules variant. */
    private void actualiserResultat() {
        if (fini) return;
        boolean roiPresent = false;
        for (int l = 0; l < Taille_plateau; l++) {
            for (int c = 0; c < Taille_plateau; c++) {
                if (grille[l][c] == 3) {
                    roiPresent = true;
                    if (l == 0 || c == 0 || l == Taille_plateau - 1 || c == Taille_plateau - 1) {
                        resultatPartie = RESULTAT_BLANCS;
                    }
                }
            }
        }
        if (!roiPresent) resultatPartie = RESULTAT_NOIRS;
        else if (ListePointN.isEmpty()) resultatPartie = RESULTAT_BLANCS;
        else if (resultatPartie == RESULTAT_AUCUN && PlusDeCoup()) {
            resultatPartie = Joueur_actif == 1 ? RESULTAT_BLANCS : RESULTAT_NOIRS;
        }
        fini = resultatPartie != RESULTAT_AUCUN;
    }

    public void nouvellePartie() {
        this.Joueur_actif = 1;
        this.fini = false;
        this.resultatPartie = RESULTAT_AUCUN;
        transformeLongEnGrille(grilleInit);
        this.redo.clear();
        this.undo.clear();
        this.isLoaded = false;
    }

    // draw doit être utilisé après chaque coup joué
    public boolean draw() {// retourne un boolean: true si on doit arreter la partie,faux sinon
        if (!fini && jeuRedondant()) {
            resultatPartie = RESULTAT_NULLE;
            terminerPartie();
            return true;
        }
        return false;
    }
}
