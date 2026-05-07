package Model;

import java.util.Random;

class IAinter extends IA{
    ListePoint blancs;
    ListePoint noirs;
    
    public IAinter(Jeu j){
        blancs = j.ListePointB;
        noirs = j.ListePointN;
    }
    
    /*Fonction Auxilaire à calculCoup*/
        // ...
    /*Fin*/
    
    //Calcul et renvoie un coup à jouer par l'ia (fonction appelé dans IA.java)
    Coup calculCoup(){
        
    }
}