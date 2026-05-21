package Vue;

import Config.Config;
import Model.BaseJeu;
import Model.Point;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GrilleGraphique extends JPanel {

    private Image whiteImage;
    private Image nap_on;
    private Image ImageVictoire;
    private Image blackWinImage;
    private Image whiteWinImage;

    private Image blackImage;
    private Image kingImage;
    private Image throneImage;
    private Image backgroundImagewhite;
    private Image backgroundImageblack;
    private Image selected;
    private Image casecentrale;

    private Color entourageColor = new Color(200, 200, 200);
    private BaseJeu jeu;
    private int decalageX ;
    private int decalageY ;
    private int tailleCase;
    private int taillePlateau;
    private int taille_grille;
    private ArrayList<Point> casesValides;
    public GrilleGraphique(BaseJeu jeu) {
        this.jeu = jeu;
        setBackground(new Color(20, 25, 35));
        this.decalageX = 0;
        this.decalageY = 0;
        this.tailleCase = 0;
        this.taillePlateau = 0;
        this.taille_grille = 0;
        this.casesValides = new ArrayList<>();
        try {
            blackWinImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/blackWin.png"));
            whiteWinImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/whiteWin.png"));
            whiteImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/white.png"));
            selected = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/selected.png"));
            nap_on = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/throne+king.png"));
            blackImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/black.png"));
            kingImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/king.png"));
            throneImage = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/throne.png"));
            backgroundImagewhite = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/backgroundwhite.png"));
            backgroundImageblack = ImageIO.read(new File(Config.CHEMIN_IMAGES + "/backgroundblack.png"));
            casecentrale =  ImageIO.read(new File(Config.CHEMIN_IMAGES + "/centrale.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int getTailleCase() {
        return tailleCase;
    }
    public int getTaillePlateau() {
        return taillePlateau;
    }
    public int getTaille_grille() {
        return taille_grille;
    }
    public int getDecalageX() {
        return decalageX;
    }
    public int getDecalageY() {
        return decalageY;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        int width = getWidth();
        int height = getHeight();
        int[][]grille = jeu.getGrille();
        taille_grille = grille.length;
        int x,y;//coordonnees pour dessiner
        // Dessiner
        int marge = 0;
        taillePlateau = Math.min(width, height);//pour ajuster l'espace autour c'est juste pour l'instant a ameliore apres
        tailleCase = taillePlateau / taille_grille;

        decalageX = (width - taillePlateau) / 2;
        decalageY = (height - taillePlateau) / 2;
        //case arriere
        for (int i = 0; i < taille_grille; i++) {
            for(int j=0;j<taille_grille;j++){
                x = j * tailleCase +  decalageX;
                y = i * tailleCase +  decalageY;
                if (i == taille_grille/2 && j == taille_grille/2) {
                    g2.drawImage(casecentrale, x, y, tailleCase, tailleCase, null);
                }
                else if((i+j)%2==0){
                    g2.drawImage(backgroundImagewhite,x,y,tailleCase,tailleCase,null);
                }else{
                    g2.drawImage(backgroundImageblack,x,y,tailleCase,tailleCase,null);
                }
            }
        }

        //pions
        for (int i = 0; i < taille_grille; i++) {
            for (int j = 0; j < taille_grille; j++) {

                x = j * tailleCase +  decalageX;
                y = i * tailleCase +  decalageY;



                switch (grille[i][j]) {
                    case 1:

                        dessinerImageCase(g2,blackImage,x,y,marge);
                        break;
                    case 2:
                        dessinerImageCase(g2,whiteImage,x,y,marge);
                    break;
                    case 3:
                        if (i == taille_grille/2 && j == taille_grille/2) {
                            dessinerImageCase(g2,nap_on,x,y,marge);
                        }else{
                            dessinerImageCase(g2,kingImage,x,y,marge);
                        }
                        break;
                    case 4:
                        dessinerImageCase(g2,throneImage,x,y,marge);
                    default:
                        break;
                }
            }

        }

        //cases valides
        peindreCasesValides(g2);

        //gagnant
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);



        double pourcentage = 0.85;

        int tailleVict = (int) (taillePlateau * pourcentage);
        int joueur_actif = jeu.getJoueur();
        if(jeu.getFini()){
            if(joueur_actif==2){

                    ImageVictoire = blackWinImage;


            }else{

                    ImageVictoire = whiteWinImage;

            }
            int xVict = (width - tailleVict) / 2;
            int yVict = (height - tailleVict) / 2;

            g2.drawImage(ImageVictoire, xVict, yVict, tailleVict, tailleVict, null);
        }


    }

    private void dessinerImageCase(Graphics2D g2, Image image, int x, int y, int marge) {

        g2.drawImage(image, x + marge, y + marge, tailleCase - 2 * marge, tailleCase - 2 * marge, null);
    }
    private void peindreCasesValides(Graphics2D g2) {
        if (casesValides == null) {
            return;
        }
        int x,y;
        for (Point p : casesValides) {
             x = p.getC() * tailleCase + decalageX;
             y = p.getL() * tailleCase + decalageY;

            g2.drawImage(selected, x, y, tailleCase, tailleCase, null);
        }
    }
    public boolean estDansPlateau(int x, int y) {
        return x >= decalageX
                && y >= decalageY
                && x < decalageX + tailleCase * taille_grille
                && y < decalageY + tailleCase * taille_grille;
    }

    public int getLigneDepuisY(int y) {
        return (y - decalageY) / tailleCase;
    }

    public int getColonneDepuisX(int x) {
        return (x - decalageX) / tailleCase;
    }

    public void dessiner_valide(ArrayList<Point> liste_case_valide) {
        this.casesValides = liste_case_valide;
        repaint();
    }

    public void effacerCasesValides() {
        this.casesValides.clear();
        repaint();
    }

    public void affichergrille(int [][]grillle){
        for(int i=0;i<taille_grille;i++){
            for(int j=0;j<taille_grille;j++){
                System.out.print(grillle[i][j]+" ");
            }
            System.out.println();
        }
    }
}
