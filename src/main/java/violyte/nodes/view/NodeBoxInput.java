package violyte.nodes.view;

import javafx.geometry.Pos;

public class NodeBoxInput extends NodeBoxField {

    public NodeBoxInput(NodeBox parent, String labelText) {
        super(parent, labelText);
        
        setAlignment(getHandle(), Pos.CENTER_LEFT);
        setAlignment(getLabel(), Pos.CENTER_LEFT);

        getHandle().getStyleClass().add("node-box-input-handle");
        getStyleClass().add("node-box-input");
    }
}
