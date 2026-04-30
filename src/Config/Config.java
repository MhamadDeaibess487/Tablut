package Config;
public final class Config {
    private Config() {
        
    }

    public static final int TAILLE_GRILLE = 9;
    public static final String CHEMIN_IMAGES = "res/images/";
    public static final String CHEMIN_SAUVEGARDES = "res/saves/";

    // public static final int LARGEUR_FENETRE = 800; // si besoin
    // public static final int HAUTEUR_FENETRE = 800;

    // tous les details de l'apli, de base donné et même chose comme port des resaux c'est la 
   
    // si besoin; on charge une configuration from a properties file or environment (peut être utile ppour la variant apres)
    public static void load() {
    }
}
