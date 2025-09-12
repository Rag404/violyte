package violyte.nodes.controler;

import java.util.Set;

import org.controlsfx.control.SearchableComboBox;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import violyte.nodes.model.Node;
import violyte.nodes.model.NodeInput;
import violyte.nodes.view.NodeBox;
import violyte.nodes.view.NodeBoxInput;
import violyte.nodes.view.NodeBoxOutput;
import violyte.nodes.view.NodeEditorView;

public class NodeEditorControler {
    private NodeEditorView view;
    private Set<? extends Node<?>> nodeList;
    private NodeBox selectedNodeBox = null;

    public NodeEditorControler(NodeEditorView view, Set<? extends Node<?>> nodeList) {
        this.view = view;
        this.nodeList = nodeList;

        // Initialize the searchable combo box with the list of available nodes
        view.getSearchBox().getItems().setAll(nodeList);

        // Set handlers for showing/hiding the add node menu
        view.getNodesPane().setOnMouseClicked(new ShowAddNodeHandler());
        view.getSearchBox().setOnHidden(new HideAddNodeHandler());
    }

    public void addNode(NodeBox nodeBox) {
        view.addNode(nodeBox);

        nodeBox.setOnMousePressed(new MoveNodeHandler(nodeBox));

        for (NodeBoxOutput output : nodeBox.getOutputs()) {
            output.getHandle().setOnDragDetected(new WireStartHandler(output));
        }

        for (NodeBoxInput input : nodeBox.getInputs()) {
            input.getHandle().setOnMouseDragReleased(new WireEndHandler(input));
        }
    }

    /**
     * Add a node to the editor at the specified position.
     * @param node The node to add
     * @param x The x position to add the node at
     * @param y The y position to add the node at
     * @return The created NodeBox
     */
    public NodeBox addNode(Node<?> node, double x, double y) {
        NodeBox nodeBox = new NodeBox(node.getLabel());
        for (NodeInput<?> input : node.getInputs()) {
            nodeBox.addField(new NodeBoxInput(input.getLabel()));
        }
        // TODO nodes with multiple outputs
        nodeBox.setLayoutX(x);
        nodeBox.setLayoutY(y);
        addNode(nodeBox);
        return nodeBox;
    }

    /**
     * Add a node to the editor at position (0, 0).
     * @param node The node to add
     * @return The created NodeBox
     */
    public NodeBox addNode(Node<?> node) {
        return this.addNode(node, 0, 0);
    }

    public void selectNodeBox(NodeBox nodeBox) {
        if (selectedNodeBox != null) {
            selectedNodeBox.setSelected(false);
        }
        selectedNodeBox = nodeBox;
        selectedNodeBox.setSelected(true);
    }

    /**
     * Handlers for wire creation when starting drag on a node output.
     */
    private class WireStartHandler implements EventHandler<MouseEvent> {
        private NodeBoxOutput source;

        public WireStartHandler(NodeBoxOutput source) {
            this.source = source;
        }

        public void handle(MouseEvent event) {
            source.startFullDrag();
            event.consume();
        }
    }

    /**
     * Handler for wire creation when releasing drag on a node input.
     */
    private class WireEndHandler implements EventHandler<MouseDragEvent> {
        private NodeBoxInput destination;

        public WireEndHandler(NodeBoxInput destination) {
            this.destination = destination;
        }

        public void handle(MouseDragEvent event) {
            Object source = event.getGestureSource();
            if (source instanceof NodeBoxOutput && source != null) {
                view.addWire((NodeBoxOutput) source, destination);
                event.consume();
            }
        }
    }

    /**
     * Handler for moving nodes when dragging them.
     */
    private class MoveNodeHandler implements EventHandler<MouseEvent> {
        private NodeBox nodeBox;

        public MoveNodeHandler(NodeBox nodeBox) {
            this.nodeBox = nodeBox;
        }

        public void handle(MouseEvent event) {
            selectNodeBox(nodeBox);
            
            double offsetX = event.getSceneX() - nodeBox.getLayoutX();
            double offsetY = event.getSceneY() - nodeBox.getLayoutY();

            nodeBox.setOnMouseDragged(ev -> {
                if (ev.getTarget() instanceof Circle) {
                    return;
                }
                nodeBox.setLayoutX(ev.getSceneX() - offsetX);
                nodeBox.setLayoutY(ev.getSceneY() - offsetY);
                ev.consume();
            });
        }
    }

    /**
     * Handler for double click to add a new node.
     */
    private class ShowAddNodeHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            if (event.getClickCount() == 2) {
                SearchableComboBox<? extends Node<?>> comboBox = view.getSearchBox();
                comboBox.setLayoutX(event.getX());
                comboBox.setLayoutY(event.getY());
                comboBox.setVisible(true);
                comboBox.show();
            }
        }
    }

    /**
     * Handler for hiding the add node menu, either when clicking outside of it or selecting a node.
     * If a node is selected, it is added to the editor at the position of the combo box.
     */
    private class HideAddNodeHandler implements EventHandler<Event> {
        private boolean hasBeenHandled = false;
        
        public void handle(Event event) {
            // Prevent duplicate execution
            if (hasBeenHandled) return;
            hasBeenHandled = true;
            
            view.getSearchBox().setVisible(false);

            SearchableComboBox<Node<?>> comboBox = view.getSearchBox();
            Node<?> selectedNode = comboBox.getSelectionModel().getSelectedItem();
            if (selectedNode != null) {
                NodeBox nodeBox = addNode(selectedNode, comboBox.getLayoutX(), comboBox.getLayoutY());
                selectNodeBox(nodeBox);
                comboBox.getSelectionModel().clearSelection();
            }
            
            // Reset flag after a short delay to allow for next usage
            javafx.application.Platform.runLater(() -> hasBeenHandled = false);
            
            event.consume();
        }
    }
}
