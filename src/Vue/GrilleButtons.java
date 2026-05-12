package Vue;


import Controleur.Controle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GrilleButtons extends JPanel {

    private JButton toggleIA;

    private JButton undo;
    private JButton redo;

    private JButton harder;
    private JButton easier;
    private JButton plus;
    private JButton minus;
    private JButton parametres;
    private boolean iaMode = false;

    private JPanel aiControlsPanel;
    private JPanel aiOptionsPanel;

    private GrilleGraphique grilleGraphique;
    private Controle controle;

    public GrilleButtons(GrilleGraphique grille, Controle controle) {
        this.grilleGraphique = grille;
        this.controle = controle;
        aiControlsPanel = new JPanel();
        aiControlsPanel.setOpaque(true);
        aiControlsPanel.setBackground(new Color(22, 26, 38));
        aiControlsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 65, 35), 1),
            BorderFactory.createEmptyBorder(12, 10, 12, 10)
        ));
        aiControlsPanel.setLayout(new BoxLayout(aiControlsPanel, BoxLayout.Y_AXIS));


        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(180, 0));
        setBorder(BorderFactory.createEmptyBorder(40, 15, 40, 25));

        toggleIA = new JButton("Toggle IA");

        undo = new JButton("Undo");
        redo = new JButton("Redo");

        plus = new JButton("+");
        minus = new JButton("-");
        harder = new JButton("Harder");
        easier = new JButton("Easier");
        parametres = new JButton("Paramètres");

        JPopupMenu menu = new JPopupMenu();

        JMenuItem resetItem = new JMenuItem("Reset");
        JMenuItem quitItem = new JMenuItem("Quitter");

        resetItem.addActionListener(e -> controle.reset());
        quitItem.addActionListener(e -> controle.demanderSauvegardeEtQuitter());

        menu.add(resetItem);
        menu.add(quitItem);

        parametres.addActionListener(e -> {
            menu.show(parametres, 0, parametres.getHeight());
        });

        aiControlsPanel = new JPanel();
        aiControlsPanel.setOpaque(true);
        aiControlsPanel.setBackground(new Color(251, 251, 251));
        aiControlsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 218, 218), 1),
                BorderFactory.createEmptyBorder(12, 10, 12, 10)
        ));
        aiControlsPanel.setLayout(new BoxLayout(aiControlsPanel, BoxLayout.Y_AXIS));

        aiOptionsPanel = new JPanel();
        aiOptionsPanel.setOpaque(false);
        aiOptionsPanel.setLayout(new BoxLayout(aiOptionsPanel, BoxLayout.Y_AXIS));
        aiOptionsPanel.setVisible(false);

        styleButton(toggleIA, new Dimension(118, 48));
        styleButton(harder, new Dimension(118, 40));
        styleButton(easier, new Dimension(118, 40));

        aiControlsPanel.add(toggleIA);
        aiControlsPanel.add(Box.createVerticalStrut(10));
        aiOptionsPanel.add(harder);
        aiOptionsPanel.add(Box.createVerticalStrut(8));
        aiOptionsPanel.add(easier);
        aiControlsPanel.add(aiOptionsPanel);

        JButton[] boutons = {parametres,undo, redo, plus, minus};

        for (JButton b : boutons) {
            styleButton(b, new Dimension(150, 55));
            add(b);
            add(Box.createVerticalStrut(15));
        }
        if (getComponentCount() > 0) {
            remove(getComponentCount() - 1);
        }

        toggleIA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iaMode = !iaMode;
                aiOptionsPanel.setVisible(iaMode);
                toggleIA.setText(iaMode ? "Hide IA" : "Toggle IA");
                aiControlsPanel.revalidate();
                aiControlsPanel.repaint();
            }
        });

        harder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // IA difficulty increase hook
            }
        });

        easier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // IA difficulty decrease hook
            }
        });


        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controle.undo();
            }
        });

        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controle.redo();
            }
        });



        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
            // Increase taille grille
        });

        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
            // Decrease taille grille
        });

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

    private void styleButton(JButton button, Dimension size) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
    }

    public JPanel getAiControlsPanel() {
        return aiControlsPanel;
    }
}
