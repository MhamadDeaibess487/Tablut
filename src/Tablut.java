import Vue.MenuGraphique;
import javax.swing.SwingUtilities;
 


public class Tablut {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MenuGraphique::new);
    }
}