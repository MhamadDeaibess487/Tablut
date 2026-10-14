package tablut.ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/** Classpath image loading shared by the menu, board and tutorial, including JAR execution. */
public final class ImageResources {
    private static final Map<String, BufferedImage> CACHE = new HashMap<>();

    private ImageResources() {}

    public static synchronized BufferedImage load(String path) throws IOException {
        String resource = path.startsWith("/") ? path.substring(1) : path;
        BufferedImage cached = CACHE.get(resource);
        if (cached != null) return cached;
        try (InputStream stream = ImageResources.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) throw new IOException("Image resource not found: " + resource);
            BufferedImage image = ImageIO.read(stream);
            if (image == null) throw new IOException("Unsupported image resource: " + resource);
            CACHE.put(resource, image);
            return image;
        }
    }
}
