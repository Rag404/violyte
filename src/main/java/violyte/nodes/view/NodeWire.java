package violyte.nodes.view;

import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.CubicCurve;

public class NodeWire extends CubicCurve {
    private NodeBoxOutput startNode;
    private NodeBoxInput endNode;

    public NodeWire(NodeBoxOutput startNode, NodeBoxInput endNode) {
        this.startNode = startNode;
        this.endNode = endNode;
        setStartNode(startNode);
        setEndNode(endNode);

        updateStartPos();
        updateEndPos();

        getStyleClass().add("node-wire");
    }

    public void setStartNode(NodeBoxOutput startNode) {
        this.startNode = startNode;
    }

    public void setEndNode(NodeBoxInput endNode) {
        this.endNode = endNode;
    }

    public void updateStartPos() {
        Bounds start = startNode.localToScene(startNode.getHandle().getBoundsInParent());
        Bounds end = endNode.localToScene(endNode.getHandle().getBoundsInParent());
        double midX = Math.abs(end.getCenterX() - start.getCenterX()) / 2.0;

        setStartX(start.getCenterX());
        setStartY(start.getCenterY());
        setControlX1(start.getCenterX() + midX);
        setControlY1(start.getCenterY());
    }

    public void updateEndPos() {
        Bounds start = startNode.localToScene(startNode.getHandle().getBoundsInParent());
        Bounds end = endNode.localToScene(endNode.getHandle().getBoundsInParent());
        double midX = Math.abs(end.getCenterX() - start.getCenterX()) / 2.0;

        setEndX(end.getCenterX());
        setEndY(end.getCenterY());
        setControlX2(end.getCenterX() - midX);
        setControlY2(end.getCenterY());
    }
}
