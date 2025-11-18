package violyte.nodes.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import violyte.nodes.model.Node;
import violyte.nodes.model.NodeGraph;
import violyte.nodes.model.NodeGraph.Connection;
import violyte.nodes.model.NodeGraph.NodeGraphListener;
import violyte.nodes.model.NodeGraph.NodeInstance;

/**
 * Presenter for the node editor.
 */
public class NodeEditorPresenter implements NodeEditorContract.Presenter {
    private final NodeEditorContract.View view;
    private final NodeGraph model;
    
    // Selection state
    private final List<Integer> selectedNodeIds;
    
    // Wire creation state
    private Integer wireSourceNodeId;
    private int wireSourceOutputIndex;
    
    // Selection rectangle state
    private double selectionStartX;
    private double selectionStartY;
    private boolean isSelectingRectangle;

    public NodeEditorPresenter(NodeEditorContract.View view, NodeGraph model, 
                              Set<Node<?>> availableNodeTypes) {
        this.view = view;
        this.model = model;
        this.selectedNodeIds = new ArrayList<>();
        
        // Listen to model changes
        model.addListener(new ModelListener());
    }

    @Override
    public void onAddNodeRequested(double x, double y) {
        view.showNodeSelectionMenu(x, y);
    }

    @Override
    public void onNodeTypeSelected(Node<?> node, double x, double y) {
        NodeInstance instance = model.addNode(node, x, y);
        clearSelection();
        selectNode(instance.getId());
        view.hideNodeSelectionMenu();
    }

    @Override
    public void onNodeDragStarted(int nodeId, boolean multiSelect) {
        if (!multiSelect) {
            clearSelection();
        }
        selectNode(nodeId);
    }

    @Override
    public void onNodeDragged(int nodeId, double deltaX, double deltaY) {
        // Move all selected nodes
        for (int id : selectedNodeIds) {
            NodeInstance instance = model.getNodeById(id);
            if (instance != null) {
                model.moveNode(instance, instance.getX() + deltaX, instance.getY() + deltaY);
            }
        }
    }

    @Override
    public void onNodeDragFinished(int nodeId) {
        // Nothing to do - model already updated during drag
    }

    @Override
    public void onWireCreationStarted(int sourceNodeId, int outputIndex) {
        this.wireSourceNodeId = sourceNodeId;
        this.wireSourceOutputIndex = outputIndex;
    }

    @Override
    public void onWireCreationFinished(int targetNodeId, int inputIndex) {
        if (wireSourceNodeId != null) {
            NodeInstance source = model.getNodeById(wireSourceNodeId);
            NodeInstance target = model.getNodeById(targetNodeId);
            
            if (source != null && target != null) {
                model.connectNodes(source, wireSourceOutputIndex, target, inputIndex);
            }
            
            wireSourceNodeId = null;
        }
    }

    @Override
    public void onDeleteRequested() {
        List<NodeInstance> nodesToRemove = new ArrayList<>();
        for (int id : selectedNodeIds) {
            NodeInstance instance = model.getNodeById(id);
            if (instance != null) {
                nodesToRemove.add(instance);
            }
        }
        model.removeNodes(nodesToRemove);
        selectedNodeIds.clear();
    }

    @Override
    public void onSelectionRectangleStarted(double x, double y) {
        selectionStartX = x;
        selectionStartY = y;
        isSelectingRectangle = true;
        view.updateSelectionRectangle(x, y, 0, 0);
        view.setSelectionRectangleVisible(true);
    }

    @Override
    public void onSelectionRectangleDragged(double x, double y) {
        if (!isSelectingRectangle) return;
        
        double minX = Math.min(selectionStartX, x);
        double minY = Math.min(selectionStartY, y);
        double width = Math.abs(x - selectionStartX);
        double height = Math.abs(y - selectionStartY);
        
        view.updateSelectionRectangle(minX, minY, width, height);

        clearSelection();
        List<Integer> intersectingNodeIds = view.getNodesInRectangle(minX, minY, width, height);
        for (int nodeId : intersectingNodeIds) {
            selectNode(nodeId);
        }
    }

    @Override
    public void onSelectionRectangleFinished() {
        isSelectingRectangle = false;
        view.setSelectionRectangleVisible(false);
    }

    @Override
    public void onNodeClicked(int nodeId, boolean multiSelect) {
        if (!multiSelect) {
            clearSelection();
        }
        selectNode(nodeId);
    }

    @Override
    public void onBackgroundClicked() {
        clearSelection();
    }

    // Helper methods
    
    private void selectNode(int nodeId) {
        if (!selectedNodeIds.contains(nodeId)) {
            selectedNodeIds.add(nodeId);
            view.setNodeSelected(nodeId, true);
            
            NodeInstance instance = model.getNodeById(nodeId);
            if (instance != null) {
                instance.setSelected(true);
            }
        }
    }

    private void deselectNode(int nodeId) {
        selectedNodeIds.remove(Integer.valueOf(nodeId));
        view.setNodeSelected(nodeId, false);
        
        NodeInstance instance = model.getNodeById(nodeId);
        if (instance != null) {
            instance.setSelected(false);
        }
    }

    private void clearSelection() {
        for (int id : new ArrayList<>(selectedNodeIds)) {
            deselectNode(id);
        }
    }

    /**
     * Listener that updates the view when the model changes.
     */
    private class ModelListener implements NodeGraphListener {
        @Override
        public void onNodeAdded(NodeInstance instance) {
            view.displayNode(instance);
        }

        @Override
        public void onNodeRemoved(NodeInstance instance) {
            view.removeNodeDisplay(instance.getId());
        }

        @Override
        public void onNodeMoved(NodeInstance instance) {
            view.updateNodePosition(instance.getId(), instance.getX(), instance.getY());
        }

        @Override
        public void onConnectionAdded(Connection connection) {
            int connectionId = generateConnectionId(connection);
            view.displayConnection(connectionId, 
                connection.getSourceNode(), connection.getSourceOutput(),
                connection.getTargetNode(), connection.getTargetInput());
        }

        @Override
        public void onConnectionRemoved(Connection connection) {
            int connectionId = generateConnectionId(connection);
            view.removeConnectionDisplay(connectionId);
        }
        
        private int generateConnectionId(Connection connection) {
            // Generate a unique connection ID by combining node IDs and port indices
            return Objects.hash(
                connection.getSourceNode(),
                connection.getSourceOutput(),
                connection.getTargetNode(),
                connection.getTargetInput()
            );
        }
    }
}
