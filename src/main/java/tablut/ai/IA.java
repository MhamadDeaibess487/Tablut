package tablut.ai;

import tablut.model.Coup;
import tablut.model.Jeu;

/** Strategy API: choose a move without changing the supplied game, then apply it. */
public abstract class IA {
    protected Jeu j;
    protected int couleur;

    public static IA creer(Jeu jeu, int niveau, int couleur) {
        if (couleur != 1 && couleur != 2) {
            throw new IllegalArgumentException("A player must be 1 (black) or 2 (white)");
        }
        return switch (niveau) {
            case 0 -> new EasyAI(jeu, couleur);
            case 1 -> new IntermediateAI(jeu, couleur);
            case 2 -> new HardAI(jeu, couleur);
            default -> throw new IllegalArgumentException("Unknown AI level: " + niveau);
        };
    }

    abstract Coup calculCoup();

    /** Call on a snapshot when searching in a background thread. */
    public final Coup choisirCoup() {
        return j.getFini() ? null : calculCoup();
    }

    public void joue() {
        if (j.getJoueur() != couleur || j.getFini()) return;
        Coup coup = choisirCoup();
        if (coup != null) j.deplacer(coup.a, coup.b);
    }
}
