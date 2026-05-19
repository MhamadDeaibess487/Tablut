package Vue;

import Controleur.Controle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

public class GrilleButtonsReseau extends JPanel {
    private JButton undo;
    private JButton parametres;
    
    private Controle controle;
    private GrilleGraphique grilleGraphique;


    public GrilleButtonsReseau(GrilleGraphique grille,Controle controle) {
        this.controle = controle;
        grilleGraphique = grille;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(180, 0));
        setBorder(BorderFactory.createEmptyBorder(40, 15, 40, 25));


        undo = new JButton("Undo");
        parametres = new JButton("Paramètres");

        JPopupMenu menu = new JPopupMenu();

        JMenuItem surrendItem = new JMenuItem("Abandonner");
//        JMenuItem quitItem = new JMenuItem("Quitter"); = abandonner ?

        surrendItem.addActionListener(e -> controle.reset());
//        quitItem.addActionListener(e -> controle.demanderSauvegardeEtQuitter());

        menu.add(surrendItem);
//        menu.add(quitItem);

        parametres.addActionListener(e -> {
            menu.show(parametres, 0, parametres.getHeight());
        });


        JButton[] boutons = {parametres,undo};

        for (JButton b : boutons) {
            styleButton(b, new Dimension(150, 55));
            add(b);
            add(Box.createVerticalStrut(15));
        }
        if (getComponentCount() > 0) {
            remove(getComponentCount() - 1);
        }

        undo.addActionListener((ActionEvent e) -> {
            /*Passe par le controle qui redirige vers une fonction de
            creation de request dans client.java*/
            controle.undoReseau(); // Not Implemented
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

}
