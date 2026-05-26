package Config;

public final class Config {
    private Config() {
    }

    public static final int TAILLE_GRILLE = 9;

    public static final String THEME_PRINCIPAL = "res/images/Theme_principal";
    public static final String THEME_STAR_WARS = "res/images/Theme_starwars";

    public static String CHEMIN_IMAGES = THEME_PRINCIPAL;

    public static final String CHEMIN_SAUVEGARDES = "res/saves/";

    public static void setThemePrincipal() {
        CHEMIN_IMAGES = THEME_PRINCIPAL;
    }

    public static void setThemeStarWars() {
        CHEMIN_IMAGES = THEME_STAR_WARS;
    }

    public static String getNomThemeActuel() {
        if (CHEMIN_IMAGES.equals(THEME_STAR_WARS)) {
            return "Star Wars";
        }
        return "Principal";
    }

    public static void load() {
    }
}