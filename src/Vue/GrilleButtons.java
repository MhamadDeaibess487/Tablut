package Vue;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GrilleButtons extends JPanel {

    private GrilleGraphique grille;
    private JButton toggleIA;
    private JButton reset;
    private JButton undo;
    private JButton redo;
    private JButton quit;
    private JButton harder;
    private JButton easier;
    private JButton plus;
    private JButton minus;
    private boolean iaMode = false;

    public GrilleButtons() {
        grille = new GrilleGraphique();

        toggleIA = new JButton("Toggle IA");
        reset = new JButton("Reset");
        undo = new JButton("Undo");
        redo = new JButton("Redo");
        quit = new JButton("Quit");
        plus = new JButton("+");
        minus = new JButton("-");

        toggleIA.addActionListener(e -> {
            iaMode = !iaMode;
            if (iaMode) {
                harder = new JButton("Harder");
                easier = new JButton("Easier");
                // Add harder and easier to the panel
                add(harder);
                add(easier);
                revalidate();
                repaint();
            } else {
                if (harder != null) {
                    remove(harder);
                    remove(easier);
                    revalidate();
                    repaint();
                }
            }
        });

        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {}
        });

        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
        });

        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
        });

        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
        });

        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
            // Increaset taille grille
        });

        minus.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent e) {}
            // Decrease taille grille
        });

        grille.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Afficher possibilités en vert
            }
        });

        add(toggleIA);
        add(reset);
        add(undo);
        add(redo);
        add(quit);
        add(plus);
        add(minus);
        add(grille);
    }
}