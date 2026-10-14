package tablut.ui;

import tablut.controller.ControleReseau;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GrilleButtonsReseau extends JPanel {
    private static final Color PANEL_BG = new Color(18, 22, 32);
    private static final Color BUTTON_BG = new Color(28, 34, 52);
    private static final Color BUTTON_HOVER = new Color(42, 50, 73);
    private static final Color GOLD = new Color(200, 160, 80);
    private static final Color TEXT = new Color(240, 225, 185);

    private final JButton undo;
    private final JButton abandonne;
    private final JButton nulle;
    private final ControleReseau controle;
    private final GrilleGraphique grilleGraphique;

    public GrilleButtonsReseau(GrilleGraphique grille, ControleReseau controle) {
        this.controle = controle;
        this.grilleGraphique = grille;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(220, 0));
        setBorder(BorderFactory.createEmptyBorder(32, 16, 32, 24));

        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(PANEL_BG);
        menuPanel.setBorder(BorderFactory.createLineBorder(GOLD, 3));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.setMaximumSize(new Dimension(180, 310));

        JLabel title = new JLabel("ONLINE");
        title.setFont(new Font("Serif", Font.BOLD, 34));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        undo = createActionButton("Undo");
        abandonne = createActionButton("Surrender");
        nulle = createActionButton("Draw");

        menuPanel.add(Box.createVerticalStrut(24));
        menuPanel.add(title);
        menuPanel.add(Box.createVerticalStrut(24));
        menuPanel.add(undo);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(abandonne);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(nulle);
        menuPanel.add(Box.createVerticalStrut(24));

        add(menuPanel);
        add(Box.createVerticalGlue());

        undo.addActionListener((ActionEvent e) -> {
            if (confirmAction(
                    "Send an undo request to the other player?",
                    "Request undo"
            )) {
                controle.undo();
            }
        });

        abandonne.addActionListener((ActionEvent e) -> {
            if (confirmAction(
                    "Surrender the current online game?",
                    "Surrender"
            )) {
                controle.abandonne();
            }
        });

        nulle.addActionListener((ActionEvent e) -> {
            if (confirmAction(
                    "Propose a draw to the other player?",
                    "Propose draw"
            )) {
                controle.demandeNul();
            }
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

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Serif", Font.BOLD, 22));
        button.setForeground(TEXT);
        button.setBackground(BUTTON_BG);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createLineBorder(GOLD, 2));
        button.setMaximumSize(new Dimension(260, 55));
        button.setPreferredSize(new Dimension(260, 55));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_BG);
            }
        });
        return button;
    }

    private boolean confirmAction(String message, String title) {
        return JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }
}
