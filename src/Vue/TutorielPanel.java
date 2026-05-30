package Vue;

import Config.Config;

import javax.swing.*;
import java.awt.*;

public class TutorielPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel cards;

    private int pageActuelle = 0;

    private String[] nomsPages = {
            "page1",
            "page2",
            "page3",
            "page4",
            "page5",
            "page6",
            "page7",
            "page8",
            "page9",
            "page10",
            "page11",
            "page12",
            "page13",
            "page14",
            "page15"
    };

    private JButton precedent;
    private JButton suivant;
    private JButton retourMenu;

    public TutorielPanel(Runnable retourMenuAction) {
        setLayout(new BorderLayout());
        setBackground(new Color(16, 20, 30));

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        for (int i = 0; i < nomsPages.length; i++) {
            String chemin = Config.CHEMIN_TUTO + "/tuto" + (i + 1) + ".png";
            cards.add(new ImageTutorielPanel(chemin), nomsPages[i]);
        }

        precedent = new JButton("Précédent");
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

    private void pageSuivante() {
        if (pageActuelle < nomsPages.length - 1) {
            pageActuelle++;
            cardLayout.show(cards, nomsPages[pageActuelle]);
            mettreAJourBoutons();
        }
    }

    private void pagePrecedente() {
        if (pageActuelle > 0) {
            pageActuelle--;
            cardLayout.show(cards, nomsPages[pageActuelle]);
            mettreAJourBoutons();
        }
    }

    private void mettreAJourBoutons() {
        precedent.setEnabled(pageActuelle > 0);

        if (pageActuelle == nomsPages.length - 1) {
            suivant.setText("Terminer");
        } else {
            suivant.setText("Suivant");
        }
    }
}