package Vue;

import Model.BaseJeu;
import Controleur.Controle;

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


    private BaseJeu jeu;
    private GrilleGraphique grilleGraphique;
    private Controle controle;

    public GrilleButtons(BaseJeu jeu , GrilleGraphique grille,Controle controle) {
        this.jeu = jeu;
        this.grilleGraphique = grille;
        this.controle = controle;

        toggleIA = new JButton("Toggle IA");
        reset = new JButton("Reset");
        undo = new JButton("Undo");
        redo = new JButton("Redo");
        quit = new JButton("Quit");
        plus = new JButton("+");
        minus = new JButton("-");

        toggleIA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
        }});

        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controle.reset();
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

        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controle.demanderSauvegardeEtQuitter();

            }
        });

        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
            // Increaset taille grille
        });

        minus.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent e) {}
            // Decrease taille grille
        });

        grilleGraphique.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (!grilleGraphique.estDansPlateau(x, y) ) {
                    return;
                }

                int ligne = grilleGraphique.getLigneDepuisY(y);
                int colonne = grilleGraphique.getColonneDepuisX(x);

                controle.handleGridClick(ligne, colonne);
            }
        });

        add(toggleIA);
        add(reset);
        add(undo);
        add(redo);
        add(quit);
        add(plus);
        add(minus);

    }
}