package Model;

/*Faire la temporisation*/

public abstract class IA{
    protected Jeu j;
    protected int couleur;
    
    
    public static IA creer(Jeu jeu,int lvl,int couleur){ // lvl : 0 ez, 1 inter, 2 hard
        IA res = null;
        switch(lvl){
            case 0 -> res = new IArandom(jeu,couleur);
            case 1 -> res = new IAinterV2(jeu, couleur);
            case 2 -> res = new IAhard(jeu,couleur);
            default -> {
                System.err.println("Erreur creation IA de type "+lvl);
                System.exit(-2);
            }
        }
        if(res != null){
            res.j = jeu;
            res.couleur = couleur;
        }
        return res;
    }
     
    abstract Coup calculCoup();
    
    public void joue(){
        Coup c = calculCoup();
        j.deplacer(c.a,c.b);
    }

    /**
     * Heuristique spécifique à chaque IA (doit être implémentée par les sous-classes).
     * Retourne un score où + = bon pour les noirs, - = bon pour les blancs (convention actuelle).
     */
    protected abstract int heuristique(Jeu copie);

    /**
     * Minimax simple (sans alpha-beta). Utilisé pour debug / fallback.
     */
    public int minimax(Jeu copie, int profondeur, boolean estmax){
        if (profondeur == 0 || copie.getFini()) {
            return heuristique(copie);
        }

        int joueurActuel = estmax ? 1 : 2;
        ListeCoup coups = copie.CoupJouable(joueurActuel);
        if (coups.Liste.isEmpty()) {
            return heuristique(copie);
        }

        if (estmax) {
            int maxEval = Integer.MIN_VALUE;
            for (Coup c : coups.Liste) {
                copie.deplacer(c.a, c.b);
                int eval = minimax(copie, profondeur - 1, false);
                copie.annuler();
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Coup c : coups.Liste) {
                copie.deplacer(c.a, c.b);
                int eval = minimax(copie, profondeur - 1, true);
                copie.annuler();
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    /**
     * Alpha-Beta with in-place play + undo to avoid allocations.
     * estmax == true -> noirs (1) qui maximisent.
     */
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
                copie.deplacer(c.a, c.b);
                int eval = alphabeta(copie, profondeur - 1, alpha, beta, false);
                copie.annuler();
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
                copie.deplacer(c.a,c.b);
                int eval = alphabeta(copie, profondeur - 1, alpha, beta, true);
                copie.annuler();
                minEval = Math.min(eval, minEval);
                beta = Math.min(minEval, beta);
                if(beta <= alpha){
                    break; // coupure alpha
                }
            }
            return minEval;

        }
    }
}