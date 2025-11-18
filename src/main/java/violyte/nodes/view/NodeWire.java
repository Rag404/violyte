package violyte.nodes.view;

import javafx.geometry.Bounds;
import javafx.scene.shape.CubicCurve;

public class NodeWire extends CubicCurve {
    public static final double MIN_CONTROL_OFFSET = 60.0;
    private NodeBoxOutput startNode;
    private NodeBoxInput endNode;

    public NodeWire(NodeBoxOutput startNode, NodeBoxInput endNode) {
        this.startNode = startNode;
        this.endNode = endNode;
        setStartNode(startNode);
        setEndNode(endNode);

        // Listen to parent node box movements
        startNode.getParentNodeBox().localToParentTransformProperty().addListener((obs, oldVal, newVal) -> {
            updateStartPos();
        });
        endNode.getParentNodeBox().localToParentTransformProperty().addListener((obs, oldVal, newVal) -> {
            updateEndPos();
        });

        getStyleClass().add("node-wire");
    }

    public void setStartNode(NodeBoxOutput startNode) {
        this.startNode = startNode;
        updateStartPos();
    }

    public void setEndNode(NodeBoxInput endNode) {
        this.endNode = endNode;
        updateEndPos();
    }

    public void updateStartPos() {
        Bounds start = startNode.localToScene(startNode.getHandle().getBoundsInParent());
        Bounds end = endNode.localToScene(endNode.getHandle().getBoundsInParent());
        double midX = Math.max(MIN_CONTROL_OFFSET, (end.getCenterX() - start.getCenterX()) / 2.0);

        setStartX(start.getCenterX());
        setStartY(start.getCenterY());
        setControlX1(start.getCenterX() + midX);
        setControlY1(start.getCenterY());
        setControlX2(end.getCenterX() - midX);
        setControlY2(end.getCenterY());
    }

    public void updateEndPos() {
        Bounds start = startNode.localToScene(startNode.getHandle().getBoundsInParent());
        Bounds end = endNode.localToScene(endNode.getHandle().getBoundsInParent());
        double midX = Math.max(MIN_CONTROL_OFFSET, (end.getCenterX() - start.getCenterX()) / 2.0);

        setEndX(end.getCenterX());
        setEndY(end.getCenterY());
        setControlX1(start.getCenterX() + midX);
        setControlY1(start.getCenterY());
        setControlX2(end.getCenterX() - midX);
        setControlY2(end.getCenterY());
    }
}
