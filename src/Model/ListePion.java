package Model;
import java.util.ArrayList;
import java.util.List;

class ListePion {
    List<Pion> LP;
    int totale = 0;

    public ListePion(){
        LP = new ArrayList<>();
    }

    public void AddListePion(Pion P) {
        LP.add(P);
        totale += 1;
    }
    
    public int find(Pion p){
        return LP.indexOf(p);
    }
    
    public void delete(int i){
        LP.remove(i);
        totale -= 1;
    }

    // copie une liste de coup
    public ListePion CopierLP() {
        ListePion res = new ListePion();
        for (Pion p : LP) {
            res.AddListePion(p);
        }
        return res;
    }
    
    public Pion getIndex(int i){
        return LP.get(i);
    }

    public ListePion ListeBaseBlanc() {
        Pion blanc, roi;
        ListePion LP = new ListePion();
        int centre = 4;

        for (int i = 1; i <= 2; i++) {// initialisation des pions blanc
            blanc = new Pion(centre + i, centre, 2);
            LP.AddListePion(blanc);

            blanc = new Pion(centre - i, centre, 2);
            LP.AddListePion(blanc);

            blanc = new Pion(centre, centre + i, 2);
            LP.AddListePion(blanc);

            blanc = new Pion(centre, centre - i, 2);
            LP.AddListePion(blanc);
        }
        roi = new Pion(centre, centre, 3);
        LP.AddListePion(roi);
        return LP;
    }

    public ListePion ListeBaseNoir() {
        Pion noir;
        ListePion LP = new ListePion();
        for (int i = 0; i < 3; i++) {// Les pion sur le coté extérieur
            noir = new Pion(3 + i, 0, 1);
            LP.AddListePion(noir);

            noir = new Pion(3 + i, 8, 1);
            LP.AddListePion(noir);

            noir = new Pion(0, 3 + i, 1);
            LP.AddListePion(noir);

            noir = new Pion(8, 3 + i, 1);
            LP.AddListePion(noir);

        }
        // ajout des pions noirs intérieurs
        noir = new Pion(4, 1, 1);
        LP.AddListePion(noir);

        noir = new Pion(4, 7, 1);
        LP.AddListePion(noir);

        noir = new Pion(1, 4, 1);
        LP.AddListePion(noir);

        noir = new Pion(7, 4, 1);
        LP.AddListePion(noir);

        return LP;
    }

    public ListePion DeplacerListePion(ListePion Liste, int l1, int c1, int l2, int c2) {
        for (Pion P : Liste.LP) {
            if (P.l == l1 && P.c == c1) {
                P.l = l2;
                P.c = c2;
                break;
            }
        }
        return Liste;

    }
}