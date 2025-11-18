package violyte.nodes.utils;

import javafx.geometry.Bounds;
import javafx.scene.Node;

public class Utils {

    /**
     * Get the layout bounds of a node in its parent's coordinate space.
     * Useful to avoid issues with drop shadow.
     * @param node The node to get bounds for
     */
    public static Bounds getLayoutBoundsInParent(Node node) {
        return node.localToParent(node.getLayoutBounds());
    }
}
