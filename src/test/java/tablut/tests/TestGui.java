package tablut.tests;

import tablut.config.Config;
import tablut.controller.Controle;
import tablut.model.Jeu;
import tablut.ui.*;
import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.lang.reflect.Field;
import java.util.concurrent.*;
import javax.imageio.ImageIO;
import static tablut.tests.TestSupport.*;

/** Optional desktop integration check; requires an X display (or xvfb-run). */
public class TestGui {
    public static void main(String[] args) throws Exception {
        try {
            check(!GraphicsEnvironment.isHeadless(), "GUI check requires a display");
            tablut.Tablut.main(new String[0]);
            SwingUtilities.invokeAndWait(() -> {
                boolean launched = false;
                for (Window window : Window.getWindows()) {
                    if (window instanceof MenuGraphique && window.isShowing()) launched = true;
                    window.dispose();
                }
                check(launched, "Application entry point did not open its menu");
            });
            for (boolean starWars : new boolean[]{false,true}) {
                SwingUtilities.invokeAndWait(() -> exerciseWindows(starWars));
                // Let queued paint/resize events complete before checking resource failures.
                Thread.sleep(300);
            }
            SwingUtilities.invokeAndWait(TestGui::verifyCancelledNickname);
            verifyBackgroundSearch();
            verifyBackgroundMove();
            System.out.println("TestGui OK (both themes, nickname dialogs, new game, 11 tutorial pages, background AI, disposal)");
        } finally {
            SwingUtilities.invokeAndWait(() -> {
                for (Window window : Window.getWindows()) window.dispose();
            });
        }
    }

    private static void exerciseWindows(boolean starWars) {
        if (starWars) Config.setThemeStarWars(); else Config.setThemePrincipal();
        Jeu game = new Jeu();
        MenuGraphique menu = new MenuGraphique(game);
        check(menu.isShowing(), "Menu missing");
        capture(menu,starWars ? "menu-star-wars.png" : "menu.png");
        JButton start = find(menu,JButton.class);
        check(start != null && start.getIcon() != null, "New game button missing");
        int[] names = {0};
        Timer respond = new Timer(50,e -> {
            for (Window window : Window.getWindows()) {
                if (window instanceof JDialog dialog && dialog.isShowing()) {
                    JOptionPane pane = find(dialog,JOptionPane.class);
                    if (pane != null && pane.getWantsInput()) {
                        pane.setInputValue("Player " + (++names[0]));
                        pane.setValue(JOptionPane.OK_OPTION);
                    }
                }
            }
        });
        respond.start();
        try { start.doClick(); } finally { respond.stop(); }
        check(names[0] == 2, "New game nickname flow changed");
        FenetreJeu frame = null;
        for (Window w : Window.getWindows()) if (w instanceof FenetreJeu f && w.isShowing()) frame = f;
        check(frame != null, "New game frame missing");
        check(find(frame,GrilleGraphique.class) != null, "Board missing");
        capture(frame,starWars ? "game-star-wars.png" : "game.png");
        TutorielPanel tutorial = new TutorielPanel(() -> {});
        int[] pages = {0};
        while (true) {
            check(!hasText(tutorial,"Image introuvable"), "Tutorial relies on working-directory images");
            pages[0]++;
            JButton next = buttonWithText(tutorial,"Suivant");
            if (next == null || !next.isEnabled()) break;
            next.doClick();
            check(pages[0] < 20, "Tutorial navigation loops");
        }
        check(pages[0] == 11, "Tutorial pages lost");
        frame.dispose();
        menu.dispose();
    }

    private static void verifyBackgroundSearch() throws Exception {
        Config.setThemePrincipal();
        Controle[] controller = new Controle[1];
        FenetreJeu[] frame = new FenetreJeu[1];
        Jeu game = new Jeu();
        game.isLoaded = true;
        SwingUtilities.invokeAndWait(() -> {
            frame[0] = new FenetreJeu(game);
            try {
                Field field = FenetreJeu.class.getDeclaredField("controle");
                field.setAccessible(true);
                controller[0] = (Controle)field.get(frame[0]);
                controller[0].setNiveauIANoir(2);
                controller[0].setIaNoir(true);
                controller[0].verifierTourIA();
            } catch (Exception e) { throw new AssertionError(e); }
        });
        Thread.sleep(2400);
        CountDownLatch responsive = new CountDownLatch(1);
        SwingUtilities.invokeLater(responsive::countDown);
        check(responsive.await(2,TimeUnit.SECONDS), "AI search blocked Swing event dispatch");
        SwingUtilities.invokeAndWait(() -> frame[0].dispose());
        var disposedBoard = game.transformerGrilleEnLong();
        int historySize = game.getUndo().size();
        Thread.sleep(300);
        check(game.getUndo().size() == historySize && disposedBoard.compareTo(game.transformerGrilleEnLong()) == 0,
                "Cancelled background search changed a disposed game");
    }

    private static void verifyCancelledNickname() {
        MenuGraphique menu = new MenuGraphique(new Jeu());
        Timer cancel = new Timer(50,e -> {
            for (Window w : Window.getWindows()) if (w instanceof JDialog dialog && w.isShowing()) {
                JOptionPane pane = find(dialog,JOptionPane.class);
                if (pane != null) pane.setValue(JOptionPane.CANCEL_OPTION);
            }
        });
        cancel.start();
        try { find(menu,JButton.class).doClick(); } finally { cancel.stop(); }
        for (Window w : Window.getWindows()) {
            check(!(w instanceof FenetreJeu && w.isShowing()), "Cancelled nickname opened an empty game window");
            w.dispose();
        }
    }

    private static void verifyBackgroundMove() throws Exception {
        Jeu game = new Jeu();
        game.isLoaded = true;
        FenetreJeu[] frame = new FenetreJeu[1];
        CountDownLatch moved = new CountDownLatch(1);
        Timer[] observe = new Timer[1];
        SwingUtilities.invokeAndWait(() -> {
            frame[0] = new FenetreJeu(game);
            try {
                Field field = FenetreJeu.class.getDeclaredField("controle");
                field.setAccessible(true);
                Controle controller = (Controle)field.get(frame[0]);
                controller.setNiveauIANoir(0);
                controller.setIaNoir(true);
                controller.verifierTourIA();
            } catch (Exception e) { throw new AssertionError(e); }
            observe[0] = new Timer(100,e -> {
                if (!game.getUndo().isEmpty()) moved.countDown();
            });
            observe[0].start();
        });
        try {
            check(moved.await(15,TimeUnit.SECONDS), "Background AI did not apply its move");
            check(game.getUndo().size() == 1 && game.getJoueur() == 2, "Background AI applied more than one move or lost turn");
        } finally {
            SwingUtilities.invokeAndWait(() -> { observe[0].stop(); frame[0].dispose(); });
        }
    }

    private static void capture(Window window,String filename) {
        String directory = System.getProperty("tablut.screenshots.dir");
        if (directory == null) return;
        try {
            Path folder = Path.of(directory);
            Files.createDirectories(folder);
            var image = new java.awt.image.BufferedImage(window.getWidth(),window.getHeight(),java.awt.image.BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            window.paint(graphics);
            graphics.dispose();
            ImageIO.write(image,"png",folder.resolve(filename).toFile());
        } catch (Exception e) { throw new AssertionError(e); }
    }

    private static <T> T find(Container container,Class<T> type) {
        if (type.isInstance(container)) return type.cast(container);
        for (Component component : container.getComponents()) {
            if (type.isInstance(component)) return type.cast(component);
            if (component instanceof Container child) {
                T match = find(child,type);
                if (match != null) return match;
            }
        }
        return null;
    }

    private static JButton buttonWithText(Container container,String text) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton button && text.equals(button.getText())) return button;
            if (component instanceof Container child) {
                JButton result = buttonWithText(child,text);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static boolean hasText(Container container,String text) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel label && text.equals(label.getText())) return true;
            if (component instanceof Container child && hasText(child,text)) return true;
        }
        return false;
    }
}
