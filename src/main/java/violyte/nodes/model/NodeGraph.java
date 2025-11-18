package violyte.nodes.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a node graph.
 */
public class NodeGraph {
    private final List<NodeInstance> nodes;
    private final List<Connection> connections;
    private final List<NodeGraphListener> listeners;
    private int nextNodeId;

    public NodeGraph() {
        this.nodes = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.nextNodeId = 1;
    }

    /**
     * Retreive a node by its ID.
     * @param nodeId The ID of the node to retrieve
     * @return The NodeInstance with the given ID, or null if not found
     */
    public NodeInstance getNodeById(int nodeId) {
        for (NodeInstance instance : nodes) {
            if (instance.getId() == nodeId) {
                return instance;
            }
        }
        return null;
    }

    /**
     * Add a node instance to the graph.
     */
    public NodeInstance addNode(Node<?> node, double x, double y) {
        NodeInstance instance = new NodeInstance(nextNodeId++, node, x, y);
        nodes.add(instance);
        notifyNodeAdded(instance);
        return instance;
    }

    /**
     * Remove a node instance from the graph.
     */
    public void removeNode(NodeInstance instance) {
        // Remove all connections involving this node
        connections.removeIf(conn -> 
            conn.getSourceNode() == instance.getId() || 
            conn.getTargetNode() == instance.getId()
        );
        nodes.remove(instance);
        notifyNodeRemoved(instance);
    }

    /**
     * Remove multiple nodes from the graph.
     */
    public void removeNodes(List<NodeInstance> instancesToRemove) {
        for (NodeInstance instance : instancesToRemove) {
            removeNode(instance);
        }
    }

    /**
     * Move a node to a new position.
     */
    public void moveNode(NodeInstance instance, double x, double y) {
        instance.setX(x);
        instance.setY(y);
        notifyNodeMoved(instance);
    }

    /**
     * Connect two nodes.
     */
    public Connection connectNodes(NodeInstance source, int outputIndex, 
                                   NodeInstance target, int inputIndex) {
        Connection connection = new Connection(
            source.getId(), outputIndex,
            target.getId(), inputIndex
        );
        connections.add(connection);
        notifyConnectionAdded(connection);
        return connection;
    }

    /**
     * Remove a connection.
     */
    public void removeConnection(Connection connection) {
        connections.remove(connection);
        notifyConnectionRemoved(connection);
    }

    public List<NodeInstance> getNodes() {
        return new ArrayList<>(nodes);
    }

    public List<Connection> getConnections() {
        return new ArrayList<>(connections);
    }

    public void addListener(NodeGraphListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NodeGraphListener listener) {
        listeners.remove(listener);
    }

    // Notification methods
    
    private void notifyNodeAdded(NodeInstance instance) {
        for (NodeGraphListener listener : listeners) {
            listener.onNodeAdded(instance);
        }
    }

    private void notifyNodeRemoved(NodeInstance instance) {
        for (NodeGraphListener listener : listeners) {
            listener.onNodeRemoved(instance);
        }
    }

    private void notifyNodeMoved(NodeInstance instance) {
        for (NodeGraphListener listener : listeners) {
            listener.onNodeMoved(instance);
        }
    }

    private void notifyConnectionAdded(Connection connection) {
        for (NodeGraphListener listener : listeners) {
            listener.onConnectionAdded(connection);
        }
    }

    private void notifyConnectionRemoved(Connection connection) {
        for (NodeGraphListener listener : listeners) {
            listener.onConnectionRemoved(connection);
        }
    }

    /**
     * Listener interface for node graph changes.
     */
    public interface NodeGraphListener {
        void onNodeAdded(NodeInstance instance);
        void onNodeRemoved(NodeInstance instance);
        void onNodeMoved(NodeInstance instance);
        void onConnectionAdded(Connection connection);
        void onConnectionRemoved(Connection connection);
    }

    /**
     * Represents an instance of a node in the graph.
     */
    public static class NodeInstance {
        private final int id;
        private final Node<?> node;
        private double x;
        private double y;
        private boolean selected;

        public NodeInstance(int id, Node<?> node, double x, double y) {
            this.id = id;
            this.node = node;
            this.x = x;
            this.y = y;
            this.selected = false;
        }

        public int getId() {
            return id;
        }

        public Node<?> getNode() {
            return node;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    /**
     * Represents a connection between two nodes.
     */
    public static class Connection {
        private final int sourceNode;
        private final int sourceOutput;
        private final int targetNode;
        private final int targetInput;

        public Connection(int sourceNode, int sourceOutput, 
                         int targetNode, int targetInput) {
            this.sourceNode = sourceNode;
            this.sourceOutput = sourceOutput;
            this.targetNode = targetNode;
            this.targetInput = targetInput;
        }

        public int getSourceNode() {
            return sourceNode;
        }

        public int getSourceOutput() {
            return sourceOutput;
        }

        public int getTargetNode() {
            return targetNode;
        }

        public int getTargetInput() {
            return targetInput;
        }
    }
}
