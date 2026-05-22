package Model;

class IAhard extends IA{
    
    public IAhard(Jeu j,int couleur){

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

    // alphabeta is implemented in the base IA class and reused here
    
    //Calcul et renvoie un coup à jouer par l'ia (fonction appelé dans IA.java)
    public Coup calculCoup(){
        Coup bestCoup = null;
        int bestScore;
        if (couleur == 1) bestScore = Integer.MIN_VALUE; // IA noire cherche à maximiser
        else bestScore = Integer.MAX_VALUE; // IA blanche cherche à minimiser
        ListeCoup coups = j.CoupJouable(couleur);
        
        for (Coup c : coups.Liste) {
            Jeu copie = new Jeu(j); // Utilise le constructeur de copie profonde
            copie.deplacer(c.a, c.b);
            int score = alphabeta(copie, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, couleur==1);

            
            //copie.annuler(); // pas besoin d'annuler si on utilise une copie profonde à chaque fois

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
        return bestCoup;
    }
    
    // use IA.minimax inherited
    
    @Override
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
        score -= (8 - kingDist) * 25;

        // avantage numérique
        score += (noir - blanc)*8;

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

        if (NBmenace == 1) score += 60;
        if (NBmenace == 2) score += 600;
        if (NBmenace == 3) score += 9000; // capture assurée au prochain tour si rien est fait



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
            score -= 8000;
        }else{
            // sinon, on valorise les routes libres
            if (routeN) score -= 140;
            if (routeS) score -= 140;
            if (routeE) score -= 140;
            if (routeO) score -= 140;
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

        score -= mobiliteRoi * 15; // plus le roi a de mobilité, mieux c'est pour les blancs


        return score;
    }
}