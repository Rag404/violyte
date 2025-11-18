package violyte.nodes.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.controlsfx.control.SearchableComboBox;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import violyte.nodes.model.Node;
import violyte.nodes.model.NodeGraph.NodeInstance;
import violyte.nodes.model.NodeInput;
import violyte.nodes.presenter.NodeEditorContract;
import violyte.nodes.utils.Utils;

/**
 * View implementation for the Node Editor.
 */
public class NodeEditorViewImpl extends Pane implements NodeEditorContract.View {
    private NodeEditorContract.Presenter presenter;
    
    // UI Components
    private final Pane nodesPane;
    private final Pane wiresPane;
    private final SearchableComboBox<Node<?>> searchBox;
    private final Rectangle selectionRect;
    
    // Maps to track visual elements
    private final Map<Integer, NodeBox> nodeBoxes;
    private final Map<Integer, NodeWire> wires;

    /**
     * Constructor.
     * @param availableNodeTypes Set of available node types for the node selection menu. This set can be kept and updated by the original caller as needed.
     */
    public NodeEditorViewImpl(Set<Node<?>> availableNodeTypes) {
        this.nodeBoxes = new HashMap<>();
        this.wires = new HashMap<>();
        
        // Initialize UI components
        nodesPane = new Pane();
        wiresPane = new Pane();
        searchBox = new SearchableComboBox<>(FXCollections.observableArrayList(availableNodeTypes));
        selectionRect = new Rectangle();

        searchBox.setVisible(false);
        selectionRect.setVisible(false);
        selectionRect.getStyleClass().add("selection-rectangle");
        
        getChildren().addAll(wiresPane, nodesPane, searchBox, selectionRect);
        getStyleClass().add("node-editor");
        
        setupEventHandlers();
    }

    /**
     * Set the presenter for this view.
     */
    public void setPresenter(NodeEditorContract.Presenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Setup all event handlers.
     */
    private void setupEventHandlers() {
        // Double-click on background to add node
        nodesPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                presenter.onAddNodeRequested(event.getX(), event.getY());
            }
        });

        // Selection rectangle
        SelectionRectangleHandler selectionHandler = new SelectionRectangleHandler();
        nodesPane.addEventFilter(MouseEvent.MOUSE_PRESSED, selectionHandler);
        nodesPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, selectionHandler);
        nodesPane.addEventFilter(MouseEvent.MOUSE_RELEASED, selectionHandler);

        // SearchBox selection
        searchBox.setOnHidden(new SearchBoxHideHandler());

        // Keyboard shortcuts
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                presenter.onDeleteRequested();
            }
        });
    }

    // ===== Contract.View Implementation =====

    @Override
    public void displayNode(NodeInstance instance) {
        NodeBox nodeBox = new NodeBox(instance.getId(), instance.getNode().getLabel());
        
        // Add inputs
        for (NodeInput<?> input : instance.getNode().getInputs()) {
            NodeBoxInput inputField = nodeBox.addInput(input.getLabel());
            
            // Setup wire creation end handler
            inputField.getHandle().setOnMouseDragReleased(event -> {
                if (event.getGestureSource() instanceof NodeBoxOutput) {
                    presenter.onWireCreationFinished(
                        instance.getId(), 
                        nodeBox.getInputs().indexOf(inputField)
                    );
                }
                event.consume();
            });
        }
        
        // Add output (TODO: support multiple outputs)
        NodeBoxOutput outputField = nodeBox.addOutput("Output");
        
        // Setup wire creation start handler
        outputField.getHandle().setOnDragDetected(event -> {
            outputField.startFullDrag();
            presenter.onWireCreationStarted(instance.getId(), 0);
            event.consume();
        });
        
        // Setup node dragging
        nodeBox.setOnMousePressed(new NodeDragHandler(nodeBox));
        
        nodeBox.setLayoutX(instance.getX());
        nodeBox.setLayoutY(instance.getY());
        
        nodeBoxes.put(instance.getId(), nodeBox);
        nodesPane.getChildren().add(nodeBox);
    }

    @Override
    public void removeNodeDisplay(int nodeId) {
        NodeBox nodeBox = nodeBoxes.remove(nodeId);
        if (nodeBox != null) {
            nodesPane.getChildren().remove(nodeBox);
        }
    }

    @Override
    public void updateNodePosition(int nodeId, double x, double y) {
        NodeBox nodeBox = nodeBoxes.get(nodeId);
        if (nodeBox != null) {
            nodeBox.setLayoutX(x);
            nodeBox.setLayoutY(y);
        }
    }

    @Override
    public void displayConnection(int connectionId, int sourceNodeId, int sourceOutput,
                                  int targetNodeId, int targetInput) {
        NodeBox sourceBox = nodeBoxes.get(sourceNodeId);
        NodeBox targetBox = nodeBoxes.get(targetNodeId);
        
        if (sourceBox != null && targetBox != null && 
            sourceOutput < sourceBox.getOutputs().size() &&
            targetInput < targetBox.getInputs().size()) {
            
            NodeBoxOutput source = sourceBox.getOutputs().get(sourceOutput);
            NodeBoxInput target = targetBox.getInputs().get(targetInput);
            
            NodeWire wire = new NodeWire(source, target);
            wires.put(connectionId, wire);
            wiresPane.getChildren().add(wire);
        }
    }

    @Override
    public void removeConnectionDisplay(int connectionId) {
        NodeWire wire = wires.remove(connectionId);
        if (wire != null) {
            wiresPane.getChildren().remove(wire);
        }
    }

    @Override
    public void setNodeSelected(int nodeId, boolean selected) {
        NodeBox nodeBox = nodeBoxes.get(nodeId);
        if (nodeBox != null) {
            nodeBox.setSelected(selected);
        }
    }

    @Override
    public void showNodeSelectionMenu(double x, double y) {
        searchBox.setLayoutX(x);
        searchBox.setLayoutY(y);
        searchBox.setVisible(true);
        searchBox.show();
    }

    @Override
    public void hideNodeSelectionMenu() {
        searchBox.setVisible(false);
        searchBox.getSelectionModel().clearSelection();
    }

    @Override
    public void updateSelectionRectangle(double x, double y, double width, double height) {
        selectionRect.setX(x);
        selectionRect.setY(y);
        selectionRect.setWidth(width);
        selectionRect.setHeight(height);
    }

    @Override
    public void setSelectionRectangleVisible(boolean visible) {
        selectionRect.setVisible(visible);
    }

    @Override
    public java.util.List<Integer> getNodesInRectangle(double x, double y, double width, double height) {
        java.util.List<Integer> intersectingNodes = new java.util.ArrayList<>();
        
        for (java.util.Map.Entry<Integer, NodeBox> entry : nodeBoxes.entrySet()) {
            NodeBox nodeBox = entry.getValue();
            
            // Get layout bounds to prevent issues with drop shadows
            javafx.geometry.Bounds nodeBounds = Utils.getLayoutBoundsInParent(nodeBox);
            
            if (nodeBounds.intersects(x, y, width, height)) {
                intersectingNodes.add(entry.getKey());
            }
        }
        
        return intersectingNodes;
    }

    // ===== Event Handlers =====

    /**
     * Handler for node dragging.
     */
    private class NodeDragHandler implements EventHandler<MouseEvent> {
        private final NodeBox nodeBox;

        public NodeDragHandler(NodeBox nodeBox) {
            this.nodeBox = nodeBox;
        }

        @Override
        public void handle(MouseEvent event) {
            if (!Utils.getLayoutBoundsInParent(nodeBox).contains(event.getSceneX(), event.getSceneY())) {
                return;
            }

            int nodeId = nodeBox.getNodeId();
            presenter.onNodeDragStarted(nodeId, event.isShiftDown());
            
            DoubleProperty lastX = new SimpleDoubleProperty(event.getSceneX());
            DoubleProperty lastY = new SimpleDoubleProperty(event.getSceneY());

            nodeBox.setOnMouseDragged(ev -> {
                if (ev.getTarget() instanceof Circle) {
                    return;
                }
                
                double deltaX = ev.getSceneX() - lastX.get();
                double deltaY = ev.getSceneY() - lastY.get();
                
                presenter.onNodeDragged(nodeId, deltaX, deltaY);
                
                lastX.set(ev.getSceneX());
                lastY.set(ev.getSceneY());
                ev.consume();
            });

            nodeBox.setOnMouseReleased(ev -> {
                presenter.onNodeDragFinished(nodeId);
                nodeBox.setOnMouseDragged(null);
                nodeBox.setOnMouseReleased(null);
            });

            event.consume();
        }
    }

    /**
     * Handler for selection rectangle.
     */
    private class SelectionRectangleHandler implements EventHandler<MouseEvent> {
        private boolean isDragging = false;

        @Override
        public void handle(MouseEvent event) {
            if (event.getButton() != MouseButton.PRIMARY || event.getClickCount() != 1) {
                return;
            }

            if (event.getEventType() == MouseEvent.MOUSE_PRESSED && 
                event.getTarget() == nodesPane) {
                isDragging = true;
                presenter.onSelectionRectangleStarted(event.getX(), event.getY());
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && isDragging) {
                presenter.onSelectionRectangleDragged(event.getX(), event.getY());
            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (isDragging) {
                    presenter.onSelectionRectangleFinished();
                    isDragging = false;
                }
            }
        }
    }

    /**
     * Handler for search box hide event.
     */
    private class SearchBoxHideHandler implements EventHandler<Event> {
        private boolean hasBeenHandled = false;
        
        @Override
        public void handle(Event event) {
            if (hasBeenHandled) return;
            hasBeenHandled = true;
            
            Node<?> selectedNode = searchBox.getSelectionModel().getSelectedItem();
            if (selectedNode != null) {
                presenter.onNodeTypeSelected(selectedNode, 
                    searchBox.getLayoutX(), searchBox.getLayoutY());
            }
            
            searchBox.setVisible(false);
            
            javafx.application.Platform.runLater(() -> hasBeenHandled = false);
            event.consume();
        }
    }
}
