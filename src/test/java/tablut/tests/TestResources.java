package tablut.tests;

import tablut.ui.ImageResources;
import java.nio.file.Path;
import java.util.jar.JarFile;
import static tablut.tests.TestSupport.*;

public class TestResources {
    public static void main(String[] args) throws Exception {
        Path artifact = Path.of(ImageResources.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        check(artifact.toString().endsWith(".jar"), "Run this resource check against the packaged JAR");
        int images = 0;
        try (JarFile jar = new JarFile(artifact.toFile())) {
            for (var entry : jar.stream().filter(e -> e.getName().endsWith(".png")).toList()) {
                check(ImageResources.load(entry.getName()).getWidth() > 0, "Unreadable packaged image");
                images++;
            }
            check(jar.stream().noneMatch(e -> e.getName().startsWith("tablut/tests/")), "Tests included in application artifact");
        }
        check(images == 82, "Not all university images were preserved in the JAR");
        try { ImageResources.load("missing.png"); throw new AssertionError("Missing resource not detected"); }
        catch (java.io.IOException expected) { }
        System.out.println("TestResources OK (82 packaged images, no test classes in application JAR)");
    }
}
