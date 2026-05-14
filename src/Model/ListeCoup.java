package Model;

import java.util.ArrayList;
import java.util.List;

class ListeCoup {
    List<Coup> Liste;
    int totale = 0;

    public ListeCoup() {
        Liste = new ArrayList<>();
    }

    public void AddListeCoup(Coup C) {
        Liste.add(C);
        totale += 1;
    }

    // copie une liste de coup
    ListeCoup CopierListe() {
        ListeCoup res = new ListeCoup();
        for (Coup c : Liste) {
            res.AddListeCoup(c);
        }
        return res;
    }
}
