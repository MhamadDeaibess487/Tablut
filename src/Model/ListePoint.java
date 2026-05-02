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

    public void AddListePoint(Point P) {
        LP.add(P);
        totale++;
    }
}