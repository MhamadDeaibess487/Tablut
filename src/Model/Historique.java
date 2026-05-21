package Model;


// PLUS UTILE DANS JEU
/*Historique des pions mangés*/
public final class Historique{
    TableauBit tab;
    int numCoup;
    
    public Historique(TableauBit tab, int numCoup){
        this.tab = tab;
        this.numCoup = numCoup;
    }
}