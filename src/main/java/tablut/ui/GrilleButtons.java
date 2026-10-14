package tablut.ui;

import tablut.controller.Controle;
import tablut.config.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GrilleButtons extends JPanel {

    public JButton undo;
    public JButton redo;
    public JButton parametres;

    private GrilleGraphique grilleGraphique;
    private Controle controle;

    public GrilleButtons(GrilleGraphique grille, Controle controle) {
        this.grilleGraphique = grille;
        this.controle = controle;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(180, 0));
        setBorder(BorderFactory.createEmptyBorder(40, 15, 40, 25));

        undo = new RoundedButton(Config.CHEMIN_IMAGES + "/undo.png", 100);
        redo = new RoundedButton(Config.CHEMIN_IMAGES + "/redo.png", 100);
        parametres = new RoundedButton(Config.CHEMIN_IMAGES + "/parametres.png", 120);

        parametres.addActionListener(e -> ouvrirMenuPause());
        undo.addActionListener(e -> controle.undo());
        redo.addActionListener(e -> controle.redo());

        JButton[] boutons = {parametres, undo, redo};

        for (JButton b : boutons) {
            styleButton(b, new Dimension(125, 125));
            add(b);
            add(Box.createVerticalStrut(15));
        }

        if (getComponentCount() > 0) {
            remove(getComponentCount() - 1);
        }

        grilleGraphique.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (!grilleGraphique.estDansPlateau(x, y)) {
                    return;
                }

                int ligne = grilleGraphique.getLigneDepuisY(y);
                int colonne = grilleGraphique.getColonneDepuisX(x);

                controle.handleGridClick(ligne, colonne);
            }
        });
    }

    private void ouvrirMenuPause() {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Menu",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(360, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(18, 22, 32));
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 160, 80), 3));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("MENU");
        title.setFont(new Font("Serif", Font.BOLD, 42));
        title.setForeground(new Color(240, 225, 185));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton resume = pauseButton("Resume");
        JButton nouvelle = pauseButton("Nouvelle Partie");
        JButton tutorial = pauseButton("Show Tutorial");
        JButton quitter = pauseButton("Quitter");

        resume.addActionListener(e -> dialog.dispose());

        nouvelle.addActionListener(e -> {
            dialog.dispose();
            controle.reset();
        });

        tutorial.addActionListener(e -> {
            TutorielPanel.ouvrirDialog(this);
        });
        quitter.addActionListener(e -> {
            dialog.dispose();
            controle.demanderSauvegardeEtQuitter();
        });

        panel.add(Box.createVerticalStrut(30));
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        panel.add(resume);
        panel.add(Box.createVerticalStrut(15));
        panel.add(nouvelle);
        panel.add(Box.createVerticalStrut(15));
        panel.add(tutorial);
        panel.add(Box.createVerticalStrut(15));
        panel.add(quitter);
        panel.add(Box.createVerticalGlue());

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private JButton pauseButton(String text) {
        JButton b = new JButton(text);

        b.setFont(new Font("Serif", Font.BOLD, 22));
        b.setForeground(new Color(240, 225, 185));
        b.setBackground(new Color(28, 34, 52));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorder(BorderFactory.createLineBorder(new Color(200, 160, 80), 2));
        b.setMaximumSize(new Dimension(260, 55));
        b.setPreferredSize(new Dimension(260, 55));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return b;
    }

    private void styleButton(JButton button, Dimension size) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
    }
}
