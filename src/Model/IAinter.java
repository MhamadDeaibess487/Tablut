package Model;

class IAinter extends IA{
    
    public IAinter(Jeu j,int couleur){

    }

    public int alphabeta(Jeu copie, int profondeur, int alpha, int beta, boolean estmax){
        if (profondeur == 0 || copie.getFini()){
            return heuristique(copie);
        }
        int joueurActuel = estmax ? 1 : 2;

        ListeCoup coups = copie.CoupJouable(joueurActuel);

        if(coups.Liste.isEmpty()){
            return heuristique(copie);
        }

        if(estmax){ // NOIR cherche à maximiser
            int maxEval = Integer.MIN_VALUE;
            for(Coup c : coups.Liste){
                Jeu souscopie = new Jeu(copie);
                souscopie.deplacer(c.a, c.b);
                int eval = alphabeta(souscopie, profondeur - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, maxEval);
                if(beta <= alpha){
                    break; // coupure beta
                }
            }
            return maxEval;
        }else{ //BLANC cherche a minimiser
            int minEval = Integer.MAX_VALUE;
            for(Coup c : coups.Liste){
                Jeu souscopie = new Jeu(copie);
                souscopie.deplacer(c.a,c.b);
                int eval = alphabeta(souscopie, profondeur - 1, alpha, beta, true);
                minEval = Math.min(eval, minEval);
                beta = Math.min(minEval, beta);
                if(beta <= alpha){
                    break; // coupure alpha
                }
            }
            return minEval;

        }
    }
    
    //Calcul et renvoie un coup à jouer par l'ia (fonction appelé dans IA.java)
    public Coup calculCoup(){
        Coup bestCoup = null;
        int bestScore = Integer.MIN_VALUE;
        ListeCoup coups = j.CoupJouable(couleur);
        Jeu travail = new Jeu(j);
        for (Coup c : coups.Liste) {
            travail.deplacer(c.a, c.b);
            int score = alphabeta(travail, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, couleur != 1);
            travail.annuler();
            if (score > bestScore) {
                bestScore = score;
                bestCoup = c;
            }
        }
        return bestCoup;
    }
    
    public int heuristique(Jeu copie) {
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