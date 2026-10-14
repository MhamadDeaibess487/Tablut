package tablut.network.protocol;

import tablut.model.Coup;

/*
Request type :
- 1 = joue
- 2 = annuler
- 3 = quit
- 4 = Abandonne
*/

public class Request{
    public int reqType;
    public Coup coup;
    
    public Request(int type,Coup coup){
        reqType = type;
        this.coup = coup;
    }
}
