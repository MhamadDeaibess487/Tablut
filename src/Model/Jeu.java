package Model;

import java.io.*;
import java.util.Stack;
import Config.Config;

/*BUG DUPPLICATION QUAND NOIR MANGE BLANC PUIS UNDO*/

public class Jeu extends BaseJeu {
    Stack<Coup> redo, undo;
    Stack<Historique> pionMange;
    public Jeu() {
        redo = new Stack<>();
        undo = new Stack<>();
        pionMange = new Stack<>();
    }

    public Jeu(String fichier) { // Charge une sauvegarde
        redo = new Stack<>();
        undo = new Stack<>();
        pionMange = new Stack<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(Config.CHEMIN_SAUVEGARDES+fichier));
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
    
    public void sauveHistorique(ListePion l){
        if(!l.isEmpty()){
            Pion p = l.getPion(0);
            pionMange.push(new Historique(l,p.t,undo.size())); 
        }
    }

    public void sauvegarder(Point a, Point b,ListePion l){
        undo.push(new Coup(a, b, Joueur_actif));
        redo.clear();
        sauveHistorique(l);
    }

    @Override
    public ListePion deplacer(Point a, Point b){
        ListePion l = super.deplacer(a, b);
        if (l == null) {
            return null;
        }
        sauvegarder(a, b,l);
        changerJoueur();
        return l;
    }

    public void sauvegardeFichier(String fichier) {
        try {
            File file = new File(Config.CHEMIN_SAUVEGARDES+fichier);

            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
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
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    // Roi  doit retourner sur son throne => impossible si on utilise deplacer
    public void annuler() {
        if (!undo.isEmpty()) {
            if(!pionMange.isEmpty()){
                if(pionMange.peek().numCoup == undo.size()){
                    Historique h = pionMange.pop();
                    Pion p;
                    for(int i = 0;i<h.l.totale;i++){
                        p = h.l.getPion(i);
                        grille[p.p.l][p.p.c] = h.couleur;
                    }
                }
            }
            Coup last = undo.pop();
            redo.push(last);
            Point a = last.a;
            Point b = last.b;
            if(grille[b.l][b.c] == 3 && a.l == trone.l && a.c == trone.c){
                grille[a.l][a.c] = 3;
                grille[b.l][b.c] = 0;
            }
            else
                super.deplacer(last.b, last.a);
            changerJoueur();
        }
    }

    public void refaire(){
        if (!redo.isEmpty()) {
            Coup last = redo.pop();
            undo.push(last);
            ListePion l = super.deplacer(last.a, last.b);
            changerJoueur();
            sauveHistorique(l);
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

}