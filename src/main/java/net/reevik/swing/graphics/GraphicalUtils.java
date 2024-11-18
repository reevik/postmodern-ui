package net.reevik.swing.graphics;

import com.kitfox.svg.app.beans.SVGIcon;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * Encapsulates graphics-related utility methods.
 */
public class GraphicalUtils {

    public static SVGIcon loadIcon(String path) {
        SVGIcon svgIcon = new SVGIcon();
        try {
            URL resource = GraphicalUtils.class.getResource(path);
            if (resource != null) {
                svgIcon.setSvgURI(resource.toURI());
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return svgIcon;
    }
}
