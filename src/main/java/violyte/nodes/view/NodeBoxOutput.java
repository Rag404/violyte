package violyte.nodes.view;

import javafx.geometry.Pos;

public class NodeBoxOutput extends NodeBoxField {
    
    public NodeBoxOutput(NodeBox parent, String labelText) {
        super(parent, labelText);
        
        setAlignment(getHandle(), Pos.CENTER_RIGHT);
        setAlignment(getLabel(), Pos.CENTER_RIGHT);

        getHandle().getStyleClass().add("node-box-output-handle");
        getStyleClass().add("node-box-input");
    }
}
