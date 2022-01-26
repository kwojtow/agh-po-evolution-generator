package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private final int mapImageWidth = 500;
    private final long eraTime = 10_000_000;

    @Override
    public void start(Stage primaryStage) throws Exception{
        VBox root = new VBox();

        primaryStage.setTitle("Darwin's Evolution Simulator");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        primaryStage.setScene(new Scene(root));

        VBox mainBox = new VBox();
        root.getChildren().add(mainBox);

        List<SingleMapVisualisation> singleMapVisualisationList = new ArrayList<>();
        singleMapVisualisationList.add(new SingleMapVisualisation(this, mainBox));
        singleMapVisualisationList.add(new SingleMapVisualisation(this, mainBox));


        mainBox.setSpacing(20);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public int getMapImageWidth() {
        return mapImageWidth;
    }

    public long getEraTime() {
        return eraTime;
    }
}
