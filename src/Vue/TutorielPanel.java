package Vue;

import Config.Config;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TutorielPanel extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel cards;
    private final List<String> nomsPages;

    private int pageActuelle = 0;

    private final JButton precedent;
    private final JButton suivant;
    private final JButton retourMenu;

    public TutorielPanel(Runnable retourMenuAction) {
        setLayout(new BorderLayout());
        setBackground(new Color(16, 20, 30));

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        nomsPages = chargerPagesDisponibles();

        if (nomsPages.isEmpty()) {
            cards.add(creerPanelVide(), "vide");
        } else {
            for (int i = 0; i < nomsPages.size(); i++) {
                cards.add(new ImageTutorielPanel(nomsPages.get(i)), "page" + i);
            }
        }

        precedent = new JButton("Precedent");
        suivant = new JButton("Suivant");
        retourMenu = new JButton("Retour menu");

        precedent.addActionListener(e -> pagePrecedente());
        suivant.addActionListener(e -> pageSuivante());

        retourMenu.addActionListener(e -> {
            if (retourMenuAction != null) {
                retourMenuAction.run();
            }
        });

        JPanel panelBoutons = new JPanel();
        panelBoutons.setBackground(new Color(16, 20, 30));

        panelBoutons.add(precedent);
        panelBoutons.add(suivant);
        panelBoutons.add(retourMenu);

        add(cards, BorderLayout.CENTER);
        add(panelBoutons, BorderLayout.SOUTH);

        mettreAJourBoutons();
    }

    public static void ouvrirDialog(Component parent) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Tutoriel", Dialog.ModalityType.APPLICATION_MODAL);

        TutorielPanel tutoriel = new TutorielPanel(dialog::dispose);
        dialog.setContentPane(tutoriel);
        dialog.setSize(1700, 1000);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private List<String> chargerPagesDisponibles() {
        List<String> pages = new ArrayList<>();

        for (int i = 1; ; i++) {
            String chemin = Config.CHEMIN_TUTO + "/tuto" + i + ".png";
            if (!new File(chemin).isFile()) {
                break;
            }
            pages.add(chemin);
        }

        return pages;
    }

    private JPanel creerPanelVide() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(16, 20, 30));

        JLabel message = new JLabel("Aucune image de tutoriel disponible.");
        message.setFont(new Font("Serif", Font.BOLD, 28));
        message.setForeground(new Color(240, 225, 185));
        panel.add(message);

        return panel;
    }

    private void pageSuivante() {
        if (pageActuelle < nomsPages.size() - 1) {
            pageActuelle++;
            cardLayout.show(cards, "page" + pageActuelle);
            mettreAJourBoutons();
        }
    }

    private void pagePrecedente() {
        if (pageActuelle > 0) {
            pageActuelle--;
            cardLayout.show(cards, "page" + pageActuelle);
            mettreAJourBoutons();
        }
    }

    private void mettreAJourBoutons() {
        boolean hasPages = !nomsPages.isEmpty();

        precedent.setEnabled(hasPages && pageActuelle > 0);
        suivant.setEnabled(hasPages && pageActuelle < nomsPages.size() - 1);

        if (!hasPages) {
            suivant.setText("Suivant");
        } else if (pageActuelle == nomsPages.size() - 1) {
            suivant.setText("Fin");
        } else {
            suivant.setText("Suivant");
        }
    }
}
