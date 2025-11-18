package violyte.nodes.presenter;

import java.util.List;

import violyte.nodes.model.Node;
import violyte.nodes.model.NodeGraph.NodeInstance;

/**
 * Contract defining the interface between View and Presenter.
 * This is the core of the MVP pattern.
 */
public interface NodeEditorContract {

    /**
     * View interface - what the Presenter can call on the View
     */
    interface View {
        /**
         * Display a node at the specified position.
         * @param instance The node instance to display
         */
        void displayNode(NodeInstance instance);

        /**
         * Remove a node from the display.
         * @param nodeId The ID of the node to remove
         */
        void removeNodeDisplay(int nodeId);

        /**
         * Update a node's position in the display.
         * @param nodeId The ID of the node to update
         * @param x The new x coordinate
         * @param y The new y coordinate
         */
        void updateNodePosition(int nodeId, double x, double y);

        /**
         * Display a connection between two nodes.
         * @param connectionId The ID of the connection
         * @param sourceNodeId The ID of the source node
         * @param sourceOutput The output index on the source node
         * @param targetNodeId The ID of the target node
         * @param targetInput The input index on the target node
         */
        void displayConnection(int connectionId, int sourceNodeId, int sourceOutput,
                              int targetNodeId, int targetInput);

        /**
         * Remove a connection from the display.
         * @param connectionId The ID of the connection to remove
         */
        void removeConnectionDisplay(int connectionId);

        /**
         * Highlight a node as selected.
         * @param nodeId The ID of the node to highlight
         * @param selected True to highlight, false to unhighlight
         */
        void setNodeSelected(int nodeId, boolean selected);

        /**
         * Show the node selection menu at the specified position.
         * @param x The x coordinate
         * @param y The y coordinate
         */
        void showNodeSelectionMenu(double x, double y);

        /**
         * Hide the node selection menu.
         */
        void hideNodeSelectionMenu();

        /**
         * Update selection rectangle display.
         * @param x The x coordinate of the rectangle
         * @param y The y coordinate of the rectangle
         * @param width The width of the rectangle
         * @param height The height of the rectangle
         */
        void updateSelectionRectangle(double x, double y, double width, double height);

        /**
         * Show or hide the selection rectangle.
         * @param visible True to show, false to hide
         */
        void setSelectionRectangleVisible(boolean visible);

        /**
         * Get the list of node IDs that intersect with the given rectangle.
         * This allows the View to handle visual bounds calculations.
         * @param x The x coordinate of the rectangle
         * @param y The y coordinate of the rectangle
         * @param width The width of the rectangle
         * @param height The height of the rectangle
         * @return List of node IDs that intersect with the rectangle
         */
        List<Integer> getNodesInRectangle(double x, double y, double width, double height);
    }

    /**
     * Presenter interface - what the View can call on the Presenter (user actions)
     */
    interface Presenter {
        /**
         * User requested to add a node at the specified position.
         * @param x The x coordinate
         * @param y The y coordinate
         */
        void onAddNodeRequested(double x, double y);

        /**
         * User selected a node type to add.
         * @param node The node type selected
         * @param x The x coordinate
         * @param y The y coordinate
         */
        void onNodeTypeSelected(Node<?> node, double x, double y);

        /**
         * User started dragging a node.
         * @param nodeId The ID of the node being dragged
         * @param multiSelect True if multi-selection is active
         */
        void onNodeDragStarted(int nodeId, boolean multiSelect);

        /**
         * User is dragging a node.
         * @param nodeId The ID of the node being dragged
         * @param deltaX Change in x position
         * @param deltaY Change in y position
         */
        void onNodeDragged(int nodeId, double deltaX, double deltaY);

        /**
         * User finished dragging a node.
         * @param nodeId The ID of the node that was dragged
         */
        void onNodeDragFinished(int nodeId);

        /**
         * User started creating a wire from an output.
         * @param sourceNodeId The ID of the source node
         * @param outputIndex The output index on the source node
         */
        void onWireCreationStarted(int sourceNodeId, int outputIndex);

        /**
         * User finished creating a wire at an input.
         * @param targetNodeId The ID of the target node
         * @param inputIndex The input index on the target node
         */
        void onWireCreationFinished(int targetNodeId, int inputIndex);

        /**
         * User requested to delete selected nodes.
         */
        void onDeleteRequested();

        /**
         * User started a selection rectangle.
         * @param x The x coordinate
         * @param y The y coordinate
         */
        void onSelectionRectangleStarted(double x, double y);

        /**
         * User is dragging the selection rectangle.
         * @param x The x coordinate
         * @param y The y coordinate
         */
        void onSelectionRectangleDragged(double x, double y);

        /**
         * User finished the selection rectangle.
         */
        void onSelectionRectangleFinished();

        /**
         * User clicked on a node.
         * @param nodeId The ID of the clicked node
         * @param multiSelect True if multi-selection is active
         */
        void onNodeClicked(int nodeId, boolean multiSelect);

        /**
         * User clicked on empty space.
         */
        void onBackgroundClicked();
    }
}
