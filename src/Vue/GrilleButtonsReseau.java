package Vue;

import Controleur.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

public class GrilleButtonsReseau extends JPanel {
    private JButton undo,abandonne,nulle;
    
    private ControleReseau controle;
    private GrilleGraphique grilleGraphique;


    public GrilleButtonsReseau(GrilleGraphique grille,ControleReseau controle) {
        this.controle = controle;
        grilleGraphique = grille;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(180, 0));
        setBorder(BorderFactory.createEmptyBorder(40, 15, 40, 25));

        undo = new JButton("Undo");
        abandonne = new JButton("Surrend");
        nulle = new JButton("Draw");
        
        JButton[] boutons = {undo,abandonne,nulle};

        for (JButton b : boutons) {
            styleButton(b, new Dimension(150, 55));
            add(b);
            add(Box.createVerticalStrut(15));
        }
        if (getComponentCount() > 0) {
            remove(getComponentCount() - 1);
        }
        
        /*Passe par le controle qui redirige vers une fonction de
            creation de request dans client.java*/
        undo.addActionListener((ActionEvent e) -> {
            controle.undo();
        });
        
        abandonne.addActionListener((ActionEvent e) -> {
           controle.abandonne(); 
        });
        
        nulle.addActionListener((ActionEvent e) -> {
           controle.demandeNul(); 
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
