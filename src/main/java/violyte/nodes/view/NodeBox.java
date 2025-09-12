package violyte.nodes.view;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class NodeBox extends VBox {
    private Label titleLabel;
    private VBox fieldsBox;
    private ArrayList<NodeBoxInput> inputs;
    private ArrayList<NodeBoxOutput> outputs;

    public NodeBox(String title) {
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

        if (field instanceof NodeBoxInput) {
            inputs.add((NodeBoxInput) field);
        } else if (field instanceof NodeBoxOutput) {
            outputs.add((NodeBoxOutput) field);
        }
    }

    public void setSelected(boolean selected) {
        if (selected && !getStyleClass().contains("node-box-selected")) {
            getStyleClass().add("node-box-selected");
        } else {
            getStyleClass().remove("node-box-selected");
        }
    }
}
