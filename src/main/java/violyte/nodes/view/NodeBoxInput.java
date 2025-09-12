package violyte.nodes.view;

import javafx.geometry.Pos;

public class NodeBoxInput extends NodeBoxField {    
    public NodeBoxInput(String labelText) {
        super(labelText);
        
        setAlignment(getHandle(), Pos.CENTER_LEFT);
        setAlignment(getLabel(), Pos.CENTER_LEFT);

        getHandle().getStyleClass().add("node-box-input-handle");
        getStyleClass().add("node-box-input");
    }
}
