package tablut.network.protocol;
import tablut.model.TableauBit;

/*
Reponse type :
- 10 = Update
- 11 = Turn Update
- 14 = Annule
- 12 = Game Over
- 13 = Assign Player
- 14 = Rep Annule
*/

public class Reponse{
    public int repType;
    public TableauBit grille;
    
    public Reponse(int type,TableauBit grille){
        repType = type;
        this.grille = grille;
    }
}
