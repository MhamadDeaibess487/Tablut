package Utils;
import Model.TableauBit;

/*
Reponse type :
- -1 = Error
- 0 = Ok (Ack)
- 1 = Update
- 2 = Annule
- 3 = Victory
- 4 = Defeat
*/

public class Reponse{
    public int repType;
    public TableauBit grille;
    
    public Reponse(int type,TableauBit grille){
        repType = type;
        this.grille = grille;
    }
}