package Model;

class IAinter extends IA{
    
    public IAinter(Jeu j,int couleur){

    }
    
    //Calcul et renvoie un coup à jouer par l'ia (fonction appelé dans IA.java)
    public Coup calculCoup(){
        Coup bestCoup = null;
        int bestScore = Integer.MIN_VALUE;
        ListeCoup coups = j.CoupJouable(couleur);
        
        for (Coup c : coups.Liste) {
            Jeu copie = new Jeu(j); // Utilise le constructeur de copie profonde
            copie.deplacer(c.a, c.b);
            int score = minimax(copie, 2, false);
            copie.annuler();
            if (score > bestScore) {
                bestScore = score;
                bestCoup = c;
            }
        }
        return bestCoup;
    }
    
    private int minimax(Jeu copie, int profondeur, boolean estmax) {
        if (profondeur == 0 || copie.getFini()) {
            return heuristique(copie);
        }
        
        ListeCoup coups = copie.CoupJouable(couleur);
        if (coups.Liste.isEmpty()) {
            return heuristique(copie);
        }
        
        if (estmax) {
            int maxEval = Integer.MIN_VALUE;
            for (Coup c : coups.Liste) {
                Jeu souscopie = new Jeu(copie); // Copie profonde pour chaque branche
                souscopie.deplacer(c.a, c.b);
                int eval = minimax(souscopie, profondeur - 1, false);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Coup c : coups.Liste) {
                Jeu souscopie = new Jeu(copie); // Copie profonde pour chaque branche
                souscopie.deplacer(c.a, c.b);
                int eval = minimax(souscopie, profondeur - 1, true);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }
    
    private int heuristique(Jeu copie) {
        //position actuelle du roi
        Point king = null;
        for (int l = 0; l < 9; l++) {
            for (int c = 0; c < 9; c++) {
                if (copie.grille[l][c] == 3) {
                    king = new Point(c, l); 
                    break;
                }
            }
            if (king != null) break;
        }
        if (king == null) {
            if (couleur == 1) return 1000;
            return -1000;
        }
        
        int kingDist = Math.min(Math.min(king.l, 8 - king.l), Math.min(king.c, 8 - king.c));
        int noir = copie.ListePointN.totale;  
        int blanc = copie.ListePointB.totale;  
        int score = 0;
        
        if (couleur == 1) { // IA noire
            score -= (8 - kingDist) * 10;
            score += (noir * 3) - (blanc * 3);
            if (copie.CaptureRoi(king.l, king.c)) {
                return 1000;
            }
            if (kingDist == 0) {
                return -1000;
            }
            return score;
        } else { // IA blanche
            score += (8 - kingDist) * 10;
            score += (blanc * 3) - (noir * 3);
            if (kingDist == 0) {
                return 1000;
            }
            if (copie.CaptureRoi(king.l, king.c)) {
                return -1000;
            }
            return score;
        }
    }
}
