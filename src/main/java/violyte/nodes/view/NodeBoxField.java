package violyte.nodes.view;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * A base class for node box fields, providing a label and centering its children vertically.
 */
public abstract class NodeBoxField extends StackPane {
    public static final double HANDLE_RADIUS = 4.5;
    public NodeBox parentNodeBox;
    private Label label;
    private Circle handleCircle;
    private NodeWire wire;
    
    public NodeBoxField(NodeBox parent, String labelText) {
        this.parentNodeBox = parent;
        label = new Label(labelText);
        handleCircle = new Circle(HANDLE_RADIUS);

        label.getStyleClass().add("node-box-label");
        handleCircle.getStyleClass().add("node-box-handle");

        getChildren().addAll(label, handleCircle);
        getStyleClass().add("node-box-field");
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double height = getLayoutBounds().getHeight();
        for (Node child : getChildren()) {
            double childWidth = child.prefWidth(-1);
            double childHeight = child.prefHeight(-1);
            double x = child.getLayoutX();
            double y = (height - childHeight) / 2;
            child.resizeRelocate(x, y, childWidth, childHeight);
        }
    }

    public NodeBox getParentNodeBox() {
        return parentNodeBox;
    }

    public Label getLabel() {
        return label;
    }

    public Circle getHandle() {
        return handleCircle;
    }

    public NodeWire getWire() {
        return wire;
    }

    public void setWire(NodeWire wire) {
        this.wire = wire;
    }
}
