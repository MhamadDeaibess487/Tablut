package Vue;


import Controleur.Controle;
import Config.Config;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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



        this.undo = new RoundedButton(Config.CHEMIN_IMAGES + "/undo.png", 100);
        this.redo = new RoundedButton(Config.CHEMIN_IMAGES + "/redo.png", 100);
        this.parametres = new RoundedButton(Config.CHEMIN_IMAGES + "/parametres.png", 120);

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




        JButton[] boutons = {parametres,undo, redo};

        for (JButton b : boutons) {
            styleButton(b, new Dimension(125, 125));
            add(b);
            add(Box.createVerticalStrut(15));
        }
        if (getComponentCount() > 0) {
            remove(getComponentCount() - 1);
        }




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

    }



}
