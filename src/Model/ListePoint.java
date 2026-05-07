package Model;

import java.util.ArrayList;
import java.util.List;

class ListePoint {
    List<Point> LP;
    int totale;

    public ListePoint() {
        LP = new ArrayList<>();
        totale = 0;
    }
    
    public boolean isEmpty(){
        return totale == 0;
    }
    
    public Point getPoint(int i){
        return LP.get(i);
    }

    public void AddListePoint(Point P) {
        LP.add(P);
        totale++;
    }

    public void SupprimeListePoint(Point Supp) {
        LP.remove(Supp);
//        List<Point> res = new ListePoint();
//        for (Point p : LP) {
//            if (p.l != Supp.l && p.c != Supp.l)
//                res.AddListePoint(p);
//            if (p.l == Supp.l && p.c == Supp.c)
//                totale--;
//        }
//
//        return res;
    }

    public ListePoint ListeBaseBlanc() {
        Point blanc, roi;
        ListePoint LP = new ListePoint();
        int centre = 4;

        for (int i = 1; i <= 2; i++) {// initialisation des Points blanc
            blanc = new Point(centre + i, centre);
            LP.AddListePoint(blanc);

            blanc = new Point(centre - i, centre);
            LP.AddListePoint(blanc);

            blanc = new Point(centre, centre + i);
            LP.AddListePoint(blanc);

            blanc = new Point(centre, centre - i);
            LP.AddListePoint(blanc);
        }
        roi = new Point(centre, centre);
        LP.AddListePoint(roi);
        return LP;
    }

    public ListePoint ListeBaseNoir() {
        Point noir;
        ListePoint LP = new ListePoint();
        for (int i = 0; i < 3; i++) {// Les Point sur le coté extérieur
            noir = new Point(3 + i, 0);
            LP.AddListePoint(noir);

            noir = new Point(3 + i, 8);
            LP.AddListePoint(noir);

            noir = new Point(0, 3 + i);
            LP.AddListePoint(noir);

            noir = new Point(8, 3 + i);
            LP.AddListePoint(noir);

        }
        // ajout des Points noirs intérieurs
        noir = new Point(4, 1);
        LP.AddListePoint(noir);

        noir = new Point(4, 7);
        LP.AddListePoint(noir);

        noir = new Point(1, 4);
        LP.AddListePoint(noir);

        noir = new Point(7, 4);
        LP.AddListePoint(noir);

        return LP;
    }

    public List<Point> DeplacerListePoint(Point a, Point b) {
        int l1 = a.l;
        int c1 = a.c;
        int l2 = b.l;
        int c2 = b.c;
        for (Point P : LP) {
            if (P.l == l1 && P.c == c1) {
                P.l = l2;
                P.c = c2;
                break;
            }
        }
        return LP;

    }
}