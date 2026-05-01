package Vue;

import Config.Config;
import Model.BaseJeu;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;

public class GrilleGraphique extends JPanel {

    private Image whiteImage;
    private Image blackImage;
    private Image kingImage;
    private Image throneImage;
    private Image backgroundImagewhite;
    private Image backgroundImageblack;

    private Color entourageColor = new Color(200, 200, 200);
    private BaseJeu jeu;


    public GrilleGraphique(BaseJeu jeu) {
        this.jeu = jeu;
        try {
            whiteImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/white.png"));
            blackImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/black.png"));
            kingImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/king.png"));
            throneImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/throne.png"));
            backgroundImagewhite = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/backgroundwhite.png"));
            backgroundImageblack = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/backgroundblack.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        int width = getWidth();
        int height = getHeight();
        int[][]grille = jeu.getGrille();
        int taille_grille = grille.length;
        int taille_case = Math.min(width, height)/taille_grille;
        int x,y;//coordonnees pour dessiner
        // Dessiner

        int taillePlateau = Math.min(width, height);
        int tailleCase = taillePlateau / taille_grille;



        for (int i = 0; i < taille_grille; i++) {
            for (int j = 0; j < taille_grille; j++) {

                x = j * tailleCase;
                y = i * tailleCase;
                if((x+y)%2==0){
                    g.drawImage(backgroundImageblack,x,y,null);
                }else{
                    g.drawImage(backgroundImagewhite,x,y,null);
                }
                if (i == 4 && j == 4) {
                    g.drawImage(throneImage, x, y, tailleCase, tailleCase, null);
                }

                switch (grille[i][j]) {
                    case 1:
                        g.drawImage(blackImage, x, y, tailleCase, tailleCase, null);
                        break;
                    case 2:
                        g.drawImage(whiteImage, x, y, tailleCase, tailleCase, null);
                        break;
                    case 3:
                        g.drawImage(kingImage, x, y, tailleCase, tailleCase, null);
                        break;
                }
            }
        }

    }
}
