package Utils;

import Model.Coup;

/*
Request type :
- 1 = joue
- 2 = annuler
- 3 = quit
- 4 = Abandonne
*/

public class Request{
    int reqType;
    Coup c;
    
    public Request(int type,Coup coup){
        reqType = type;
        c = coup;
    }
    
    public int getType(){
        return reqType;
    }
    
    public Coup getCoup(){
        return c;
    }
}