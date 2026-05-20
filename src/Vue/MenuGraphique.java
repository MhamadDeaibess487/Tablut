package Vue;
import java.io.File;
import Model.BaseJeu;
import Model.Jeu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import static Config.Config.CHEMIN_SAUVEGARDES;

public class MenuGraphique extends JFrame {
    BaseJeu jeu;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    public boolean sauvegarde;
    public MenuGraphique(BaseJeu jeu) {
        this.jeu = jeu;
        setTitle("Menu Principal - Tablut");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        this.sauvegarde = false;
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(creerMenuPrincipal(), "MENU");
        mainPanel.add(creerMenuOptions(), "OPTIONS");


        add(mainPanel);

        setVisible(true);
    }


    private JPanel creerMenuPrincipal() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(40, 40, 45));

        JPanel box = new JPanel();
        box.setLayout(new GridLayout(5, 1, 0, 15));
        box.setOpaque(false);

        JLabel titre = new JLabel("TABLUT", SwingConstants.CENTER);
        titre.setFont(new Font("Serif", Font.BOLD, 36));
        titre.setForeground(Color.WHITE);

        JButton nouvellePartie = new JButton("Nouvelle Partie");
        JButton online = new JButton("Online");
        JButton chargerPartie = new JButton("Charger Partie");
        JButton options = new JButton("Options");
        JButton quitter = new JButton("Quitter");

        nouvellePartie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                FenetreJeu fenetreJeu = new FenetreJeu(jeu);
                fenetreJeu.setVisible(true);
            } 
        });

        chargerPartie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //pas implementé encore
                File dossier = new File(CHEMIN_SAUVEGARDES);
                File[] fichiers = dossier.listFiles();

                if (fichiers == null || fichiers.length == 0) {
                    JOptionPane.showMessageDialog(null,"Aucune sauvegarde trouvée.");
                    return;
                }

                String[] noms = new String[fichiers.length];

                for (int i = 0; i < fichiers.length; i++) {
                    noms[i] = fichiers[i].getName();
                }

                String choix = (String) JOptionPane.showInputDialog(
                        null,
                        "Choisissez une sauvegarde :",
                        "Charger Partie",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        noms,
                        noms[0]
                );

                if (choix == null) {
                    return;
                }

                BaseJeu jeuCharge = new Jeu(CHEMIN_SAUVEGARDES + choix);

                dispose();
                new FenetreJeu(jeuCharge);
            } 
        });

        options.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "OPTIONS"); 
            } 
        });

        quitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            } 
        });
        

        box.add(titre);
        box.add(nouvellePartie);
        box.add(chargerPartie);
        box.add(options);
        box.add(quitter);

        panel.add(box);
        adapterMenu(titre, nouvellePartie, chargerPartie, options, quitter);
        return panel;
    }

    private JPanel creerMenuOptions() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(35, 35, 40));

        JPanel box = new JPanel();
        box.setLayout(new GridLayout(4, 1, 0, 15));
        box.setOpaque(false);

        JLabel titre = new JLabel("Options", SwingConstants.CENTER);
        titre.setFont(new Font("Serif", Font.BOLD, 28));
        titre.setForeground(Color.WHITE);

        JButton modeTut = new JButton("Mode Tutoriel On/Off "); // avec feed forward etc
        JButton theme = new JButton("Thème : diff themes...");
        JButton retour = new JButton("Retour");

        retour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "MENU");
            }
        });

        box.add(titre);
        box.add(modeTut);
        box.add(theme);
        box.add(retour);

        panel.add(box);
        adapterMenu(titre, modeTut, theme, retour);
        return panel;
    }


    private void adapterMenu(JLabel titre, JButton... boutons) { // pour adapter full screen/ windowed / different ordi etc 
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();

                int titleSize = Math.max(36, Math.min(w, h) / 10);
                int buttonFontSize = Math.max(18, Math.min(w, h) / 35);

                titre.setFont(new Font("Serif", Font.BOLD, titleSize));

                int buttonWidth = Math.max(280, w / 4);
                int buttonHeight = Math.max(60, h / 13);

                for (JButton b : boutons) {
                    b.setFont(new Font("SansSerif", Font.BOLD, buttonFontSize));
                    b.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
                }

                revalidate();
                repaint();
            }
        });
    }
}