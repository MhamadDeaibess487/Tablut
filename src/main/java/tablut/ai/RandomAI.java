package tablut.ai;

import tablut.model.Coup;
import tablut.model.Jeu;
import java.util.List;
import java.util.Random;

/** Historical random baseline, retained for experimentation; absent from the UI factory. */
class RandomAI extends IA {
    private final Random random = new Random();

    public RandomAI(Jeu jeu, int couleur) {
        this.j = jeu;
        this.couleur = couleur;
    }

    @Override
    Coup calculCoup() {
        List<Coup> coups = j.getLegalMoves(couleur);
        return coups.isEmpty() ? null : coups.get(random.nextInt(coups.size()));
    }
}
