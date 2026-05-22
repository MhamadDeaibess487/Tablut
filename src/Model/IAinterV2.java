package Model;

class IAinterV2 extends IA{
    
    public IAinterV2(Jeu j,int couleur){
    }

    public Point PosRoi(Jeu copie){
        for(int l = 0; l < 9; l++){
            for(int c = 0; c < 9; c++){
                if(copie.grille[l][c] == 3){
                    return new Point(l,c);
                }
            }
        }

        return null;
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

    // a faire, tri des coups pour explorer en priorité les coups les plus prometteurs (ex: menace roi, bloquer menace, eloigne roi ...)
    
    // use IA.alphabeta inherited

    //Calcul et renvoie un coup à jouer par l'ia (fonction appelé dans IA.java)
    public Coup calculCoup(){
        System.out.println("IA INTER CALCUL COUP");
        Coup bestCoup = null;
        int bestScore;
        if (couleur == 1) bestScore = Integer.MIN_VALUE; // IA noire cherche à maximiser
        else bestScore = Integer.MAX_VALUE; // IA blanche cherche à minimiser
        ListeCoup coups = j.CoupJouable(couleur);

        // créer une seule copie de travail pour réduire les allocations
        Jeu travail = new Jeu(j);
        for (Coup c : coups.Liste) {
            travail.deplacer(c.a, c.b);
            // note: estmax == true pour le NOIR (1)
            int score = alphabeta(travail, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, couleur != 1);
            travail.annuler();

            if (couleur == 1){ // NOIR cherche à maximiser
                if(score>bestScore){
                    bestScore = score;
                    bestCoup = c;
                }
                if(score == bestScore && Math.random() < 0.5){ // En cas d'égalité, choisir aléatoirement entre les coups
                    bestCoup = c;
                }

            }else{ // BLANC cherche à minimiser
                if(score<bestScore){
                    bestScore = score;
                    bestCoup = c;
                }
                if(score == bestScore && Math.random() < 0.5){ // En cas d'égalité, choisir aléatoirement entre les coups
                    bestCoup = c;
                }
            }
        }
        System.out.println("IA INTER COUP CHOISI : "+bestCoup);
        return bestCoup;
    }
    
    // use IA.minimax inherited
    
   
    public int heuristique(Jeu copie) {
        // + score = bon pour les noirs
        // - score = bon pour les blancs

        int score = 0;

        //position actuelle du roi
        Point king = PosRoi(copie);

        // ––– ETATS TERMINAUX ––– //
        // ––– capture du roi ou roi en bordure ––– //

        //roi capturé
        if (king == null){ 
            return 10000;
        }

        //roi sur bordure
        if (king.l == 0 || king.l == 8 || king.c == 0 || king.c == 8){
            return -10000;
        }

    
        // ––––––––––––––––––– //


        
        int kingDist = Math.min(Math.min(king.l, 8 - king.l), Math.min(king.c, 8 - king.c));
        int noir = copie.ListePointN.totale;  
        int blanc = copie.ListePointB.totale;  
        

        // roi proche du bord
        score -= (8 - kingDist) * 20;

        // avantage numérique
        score += (noir - blanc) * 5;

        //menace au roi (pion noir à coté du roi)
        // 1 noir = petite menace, 2 noir = menace importante, 3 noir = capture assurée au prochain tour
        // donc courbe non linéaire pour valoriser la 2ème pièce noire à coté du roi, et valoriser énormément la 3ème
        int NBmenace = 0;
        if(copie.grille[king.l-1][king.c]==1){ 
            NBmenace++;
        }
        if(copie.grille[king.l+1][king.c]==1) {
            NBmenace++;
        }
        if(copie.grille[king.l][king.c-1]==1) {
            NBmenace++;
        }
        if(copie.grille[king.l][king.c+1]==1) {
            NBmenace++;
        }

        if (NBmenace == 1) score += 50;
        if (NBmenace == 2) score += 500;
        if (NBmenace == 3) score += 8000; // capture assurée au prochain tour si rien est fait



        // route menant au bord libre
        boolean routeN = true, routeS = true, routeE = true, routeO = true;
        // Route nord
        for (int i = king.l - 1; i >= 0; i--) {
            if (copie.grille[i][king.c] != 0) { routeN = false; break; }
        }
        // Route sud
        for (int i = king.l + 1; i <= 8; i++) {
            if (copie.grille[i][king.c] != 0) { routeS = false; break; }
        }
        // Route est
        for (int i = king.c + 1; i <= 8; i++) {
            if (copie.grille[king.l][i] != 0) { routeE = false; break; }
        }
        // Route ouest
        for (int i = king.c - 1; i >= 0; i--) {
            if (copie.grille[king.l][i] != 0) { routeO = false; break; }
        }
        // si deux route opposé libre, alors l'adversaire ne peut pas bloquer les deux en même temps donc victoire assurée pour les blancs
        if ((routeN && routeS) || (routeE && routeO)){
            score -= 5000;
        }else{
            // sinon, on valorise les routes libres
            if (routeN) score -= 100;
            if (routeS) score -= 100;
            if (routeE) score -= 100;
            if (routeO) score -= 100;
        }


        // mobilité du roi (nombre de cases vides autours du roi, pour l'instant que les cases autour)
        int mobiliteRoi = 0;
        if(copie.grille[king.l-1][king.c]==0){
            mobiliteRoi++;
        }
        if(copie.grille[king.l+1][king.c]==0) {
            mobiliteRoi++;
        }
        if(copie.grille[king.l][king.c-1]==0) {
            mobiliteRoi++;
        }
        if(copie.grille[king.l][king.c+1]==0) {
            mobiliteRoi++;
        }

        score -= mobiliteRoi * 50; // plus le roi a de mobilité, mieux c'est pour les blancs


        return score;
    }
}