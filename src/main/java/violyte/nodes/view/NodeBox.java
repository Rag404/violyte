package violyte.nodes.view;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class NodeBox extends VBox {
    private int nodeId;
    private Label titleLabel;
    private VBox fieldsBox;
    private ArrayList<NodeBoxInput> inputs;
    private ArrayList<NodeBoxOutput> outputs;

    public NodeBox(int nodeId, String title) {
        this.nodeId = nodeId;
        titleLabel = new Label(title);
        fieldsBox = new VBox();
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();

        StackPane headerPane = new StackPane(titleLabel);
        headerPane.getStyleClass().add("node-box-header");
        titleLabel.getStyleClass().add("node-box-title");
        fieldsBox.getStyleClass().add("node-box-content");
        fieldsBox.setFillWidth(true);

        getChildren().addAll(headerPane, fieldsBox);
        getStyleClass().add("node-box");
    }

    public int getNodeId() {
        return nodeId;
    }

    /**
     * Return all the input fields of this node.
     * @return An unorganized list of input fields
     */
    public ArrayList<NodeBoxInput> getInputs() {
        return inputs;
    }

    /**
     * Return all the output fields of this node.
     * @return An unorganized list of output fields
     */
    public ArrayList<NodeBoxOutput> getOutputs() {
        return outputs;
    }

    /**
     * Add a field to this node's content.
     * If the specified field is a {@code NodeBoxInput} or {@code NodeBoxOuput}, it will get added to this node's
     * list of input/output fields.
     * @param field The field to add to this node's content
     */
    public void addField(NodeBoxField field) {
        fieldsBox.getChildren().add(field);

        if (field instanceof NodeBoxInput nodeBoxInput) {
            inputs.add(nodeBoxInput);
        } else if (field instanceof NodeBoxOutput nodeBoxOutput) {
            outputs.add(nodeBoxOutput);
        }
    }

    /**
     * Create and add an input field to this node.
     * @param labelText The label text of the input field
     * @return The created NodeBoxInput
     */
    public NodeBoxInput addInput(String labelText) {
        NodeBoxInput input = new NodeBoxInput(this, labelText);
        addField(input);
        return input;
    }

    /**
     * Create and add an output field to this node.
     * @param labelText The label text of the output field
     * @return The created NodeBoxOutput
     */
    public NodeBoxOutput addOutput(String labelText) {
        NodeBoxOutput output = new NodeBoxOutput(this, labelText);
        addField(output);
        return output;
    }

    /**
     * Set whether this node is selected or not.
     * @param selected True to mark this node as selected, false to unselect it
     */
    public void setSelected(boolean selected) {
        if (selected) {
            if (!getStyleClass().contains("node-box-selected")) {
                getStyleClass().add("node-box-selected");
            }
        } else {
            getStyleClass().remove("node-box-selected");
        }
    }
}
