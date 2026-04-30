package Vue;
import Config.Config;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuGraphique extends JFrame {
    private JButton nouvellePartieButton;
    private JButton chargerPartieButton;
    private JButton quitterButton;

    public MenuGraphique() {
        setTitle("Menu Principal");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        nouvellePartieButton = new JButton("Nouvelle Partie");
        chargerPartieButton = new JButton("Charger Partie");
        quitterButton = new JButton("Quitter");

        add(nouvellePartieButton);
        add(chargerPartieButton);
        add(quitterButton);

        nouvellePartieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // nouvelle partie
            }
        });

        chargerPartieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // charger partie
            }
        });

        quitterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}