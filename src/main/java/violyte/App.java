package violyte;

import java.util.Set;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import violyte.nodes.model.Node;
import violyte.nodes.model.NodeGraph;
import violyte.nodes.model.NodeInput;
import violyte.nodes.presenter.NodeEditorPresenter;
import violyte.nodes.view.NodeEditorViewImpl;

/**
 * Main application demo
 */
public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Violyte");

        // Create available node types
        Set<Node<?>> availableNodes = Set.of(new MyNode(), new AnotherNode());

        // Create Model
        NodeGraph model = new NodeGraph();

        // Create View
        NodeEditorViewImpl view = new NodeEditorViewImpl(availableNodes);
        view.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Create Presenter and wire it to View and Model
        NodeEditorPresenter presenter = new NodeEditorPresenter(view, model, availableNodes);
        view.setPresenter(presenter);

        // Create Scene
        Scene scene = new Scene(view, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Add some initial nodes through the model
        model.addNode(new MyNode(), 200, 100);
        model.addNode(new AnotherNode(), 400, 200);
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Example node implementation.
     */
    private static class MyNode extends Node<Integer> {
        private final NodeInput<Integer> input1 = new NodeInput<>("A");
        private final NodeInput<Integer> input2 = new NodeInput<>("B");

        @Override
        public String getLabel() {
            return "My Node";
        }

        @Override
        public NodeInput<?>[] getInputs() {
            return new NodeInput[]{input1, input2};
        }

        @Override
        public Integer execute() {
            Integer val1 = input1.getValue();
            Integer val2 = input2.getValue();
            return (val1 != null ? val1 : 0) + (val2 != null ? val2 : 0);
        }
    }

    /**
     * Another example node.
     */
    private static class AnotherNode extends Node<String> {
        private final NodeInput<String> input = new NodeInput<>("Text");

        @Override
        public String getLabel() {
            return "Another Node";
        }

        @Override
        public NodeInput<?>[] getInputs() {
            return new NodeInput[]{input};
        }

        @Override
        public String execute() {
            String val = input.getValue();
            return val != null ? val.toUpperCase() : "";
        }
    }
}
