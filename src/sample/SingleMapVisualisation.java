package sample;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;

public class SingleMapVisualisation {
    private IWorldMap simulationMap;

    private Label plantsNumberLabel;
    private Label animalsNumberLabel;
    private Label dominantGenesLabel;
    private Label energyAverageLabel;
    private Label lifeTimeAverageLabel;
    private Label childrenNumberAverageLabel;

    private Label followedAnimalDeathEra;
    private Label followedAnimalChildrenNumber;
    private Label followedAnimalDescendantsNumber;

    private GraphicsContext simulationAreaGraphicsContext;

    private XYChart.Series animalSeries;
    private XYChart.Series plantsSeries;

    private Vector2D followedPosition;
    private int followTime;

    private AnimationTimer timer;


    private double scale;
    private int mapImageWidth;
    private int mapImageHeight;


    /**
     * Two arguments constructor
     * @param simulation parent application to this visualisation
     * @param parent parent node int layout tree in which this visualisation is placed
     */
    SingleMapVisualisation(Main simulation, VBox parent){
        this.simulationMap = new WorldMap();
        this.followedPosition = null;

        this.mapImageWidth = simulation.getMapImageWidth();
        this.scale = this.mapImageWidth / getSimulationMap().getProperties().getWidth();
        this.mapImageHeight = (int) (scale * getSimulationMap().getProperties().getHeight());


        Button startButton = new Button("Start simulation");
        Button stopButton = new Button("Stop simulation");


        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Era");
        xAxis.setForceZeroInRange(false);

        final LineChart<Number, Number> animalsAndPlantsNumberChart = new LineChart<Number, Number>(xAxis, yAxis);
        animalsAndPlantsNumberChart.setCreateSymbols(false);
        animalsAndPlantsNumberChart.setMaxSize(300, 300);

        animalSeries = new XYChart.Series();
        animalSeries.setName("Animals number");
        animalsAndPlantsNumberChart.getData().add(animalSeries);

        plantsSeries = new XYChart.Series();
        plantsSeries.setName("Plants number");
        animalsAndPlantsNumberChart.getData().add(plantsSeries);


        this.plantsNumberLabel = new Label();
        this.animalsNumberLabel = new Label();
        this.dominantGenesLabel = new Label();
        this.energyAverageLabel = new Label();
        this.lifeTimeAverageLabel = new Label();
        this.childrenNumberAverageLabel = new Label();


        Label animalToFollowLabel = new Label("Animal to follow (first point it on the map): ");
        ChoiceBox followedAnimalChoiceBox = new ChoiceBox();
        Label animalFollowingTimeLabel = new Label("Following time (default 1 era): ");
        TextField animalFollowingTimeField = new TextField();
        Button followAnimalButton = new Button("Follow selected animal");
        followAnimalButton.setDisable(true);

        this.followedAnimalDeathEra = new Label();
        this.followedAnimalChildrenNumber = new Label();
        this.followedAnimalDescendantsNumber = new Label();


        Button showAnimalsHavingDominantGenesButton = new Button("Show all animals having dominant genes");


        Label startCollectingStatsLabel = new Label("Generate global stats from era:");
        TextField startCollectingStatsField = new TextField();
        Label stopCollectingStatsLabel = new Label("to era: ");
        TextField stopCollectingStatsField = new TextField();
        Button applyCollectingStatsInterval = new Button("Confirm entered data");


        Canvas simulationArea = new Canvas(this.mapImageWidth, this.mapImageHeight);
        this.simulationAreaGraphicsContext = simulationArea.getGraphicsContext2D();
        simulationAreaGraphicsContext.setFill(Color.rgb(222, 245, 179));
        simulationAreaGraphicsContext.fillRect(0, 0, this.mapImageWidth, this.mapImageHeight);


        HBox mainBox = new HBox();
        VBox descriptionFirstColumnBox = new VBox();
        VBox descriptionSecondColumnBox = new VBox();
        HBox startStopBox = new HBox();
        VBox graphBox = new VBox();
        VBox currentStatsBox = new VBox();
        VBox followedAnimalBox = new VBox();
        VBox followedAnimalControlBox = new VBox();
        VBox followedAnimalStatsBox = new VBox();
        VBox dominantGenesButtonBox = new VBox();
        VBox generalStatsBox = new VBox();
        VBox simulationAreaBox = new VBox();


        parent.getChildren().add(mainBox);

        mainBox.getChildren().addAll(descriptionFirstColumnBox, descriptionSecondColumnBox, simulationAreaBox);

        descriptionFirstColumnBox.getChildren().addAll(startStopBox, graphBox, currentStatsBox);

        descriptionSecondColumnBox.getChildren().addAll(followedAnimalBox, dominantGenesButtonBox, generalStatsBox);

        followedAnimalBox.getChildren().addAll(followedAnimalControlBox, followedAnimalStatsBox);

        startStopBox.getChildren().addAll(startButton, stopButton);
        graphBox.getChildren().add(animalsAndPlantsNumberChart);

        currentStatsBox.getChildren().addAll(animalsNumberLabel, plantsNumberLabel, dominantGenesLabel, energyAverageLabel, lifeTimeAverageLabel, childrenNumberAverageLabel);

        followedAnimalControlBox.getChildren().addAll(animalToFollowLabel, followedAnimalChoiceBox, animalFollowingTimeLabel, animalFollowingTimeField, followAnimalButton);

        followedAnimalStatsBox.getChildren().addAll(followedAnimalDeathEra, followedAnimalChildrenNumber, followedAnimalDescendantsNumber);

        dominantGenesButtonBox.getChildren().add(showAnimalsHavingDominantGenesButton);

        generalStatsBox.getChildren().addAll(startCollectingStatsLabel, startCollectingStatsField, stopCollectingStatsLabel, stopCollectingStatsField, applyCollectingStatsInterval);

        simulationAreaBox.getChildren().add(simulationArea);


        mainBox.setSpacing(50);
        mainBox.setAlignment(Pos.CENTER);
        descriptionFirstColumnBox.setPrefWidth(400);
        descriptionSecondColumnBox.setSpacing(50);



        timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long l) {
                if (l - lastUpdate >= simulation.getEraTime()) {
                    redraw();
                    lastUpdate = l;
                }
            }
        };


        simulationArea.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int xPosition = (int) (mouseEvent.getX() / scale);
                int yPosition = (int) (mouseEvent.getY() / scale);

                if (getSimulationMap().getAnimalsList().containsKey(new Vector2D(xPosition, yPosition)) &&
                        !getSimulationMap().getAnimalsList().get(new Vector2D(xPosition, yPosition)).isEmpty()) {

                    Object[] idList = getSimulationMap().getAnimalsList().get(new Vector2D(xPosition, yPosition)).stream().map((animal -> animal.getId() + "")).toArray();

                    followedAnimalChoiceBox.getItems().clear();
                    followedAnimalChoiceBox.getItems().addAll(idList);
                    followedAnimalChoiceBox.getSelectionModel().selectFirst();
                    followAnimalButton.setDisable(false);
                    followedPosition =  new Vector2D(xPosition, yPosition);
                }
            }
        });


        startButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                timer.start();
            }
        });
        stopButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                timer.stop();
            }
        });

        followAnimalButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int id = Integer.valueOf(followedAnimalChoiceBox.getSelectionModel().getSelectedItem().toString());
                followedAnimalChoiceBox.getSelectionModel().clearSelection();
                followedAnimalChoiceBox.getItems().clear();
                followAnimalButton.setDisable(true);
                try {
                    if (animalFollowingTimeField.getText() != "")
                        followTime = Integer.valueOf(animalFollowingTimeField.getText());
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
                if(followTime <= 0 || followTime >= Integer.MAX_VALUE)
                    followTime = 1;

                getSimulationMap().startFollowAnimal(followedPosition, id);
                timer.start();
            }
        });

        showAnimalsHavingDominantGenesButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                getSimulationMap().allAnimalsWithDominantGenes().forEach((animal -> {
                    simulationAreaGraphicsContext.setFill(Color.YELLOW);
                    simulationAreaGraphicsContext.fillRect(animal.getPosition().getX() * scale, animal.getPosition().getY() * scale, scale, scale);
                }));
            }
        });

        applyCollectingStatsInterval.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String start = startCollectingStatsField.getText();
                String stop = stopCollectingStatsField.getText();

                int startEra = 0;
                int stopEra = 0;

                if (start != "" && stop != "") {
                    startEra = Integer.valueOf(start);
                    stopEra = Integer.valueOf(stop);
                }
                if (startEra < 0 || stopEra < startEra) {
                    startEra = 0;
                    stopEra = 0;
                }

                getSimulationMap().setGlobalStatsParams(startEra, stopEra);
                timer.start();
            }

        });

    }


    /**
     * Draws current simulation map state on graphic interface
     */
    void redraw() {
        this.getSimulationMap().nextEra();

        this.simulationAreaGraphicsContext.setFill(Color.rgb(222, 245, 179));
        this.simulationAreaGraphicsContext.fillRect(0, 0, mapImageWidth, mapImageWidth);
        getSimulationMap().getPlantsList().forEach((position, plant) -> {
            if (position != null) {
                this.simulationAreaGraphicsContext.setFill(Color.rgb(44, 171, 5));
                this.simulationAreaGraphicsContext.fillRect(position.getX() * scale, position.getY() * scale, scale, scale);
            }
        });
        getSimulationMap().getAnimalsList().forEach((position, animalList) -> {
            animalList.forEach(animal -> {
                double animalStrength = 0.3 + 0.7 * (double) animal.getEnergy() / (double) (getSimulationMap().getProperties().getStartEnergy() * 2);
                if (animalStrength > 1) {
                    animalStrength = 1;
                }
                if (animalStrength < 0) {
                    animalStrength = 0;
                }
                if (position != null) {
                    this.simulationAreaGraphicsContext.setFill(Color.rgb(120, 5, 22, animalStrength));
                    this.simulationAreaGraphicsContext.fillRect(position.getX() * scale, position.getY() * scale, scale, scale);
                }


            });

        });


        this.plantsNumberLabel.setText("Plants number: " + getSimulationMap().getPlantsList().size());
        this.animalsNumberLabel.setText("Animals number: " + getSimulationMap().getAnimalsNumber());

        if(getSimulationMap().getDominantGenes() != null)
            dominantGenesLabel.setText("Dominant genes: " +  getSimulationMap().getDominantGenes().toString());
        else{
            dominantGenesLabel.setText("Dominant genes: -/-");
        }

        if (getSimulationMap().getAnimalsNumber() > 0)
            lifeTimeAverageLabel.setText("Life time average: " + String.valueOf(getSimulationMap().getDeadAnimalsLifeTimeAverage()));
        else
            lifeTimeAverageLabel.setText("Life time average: -/-");

        if (getSimulationMap().getAnimalsNumber() > 0) {
            energyAverageLabel.setText("Energy average: " + String.valueOf(getSimulationMap().getAliveAnimalsEnergyAverage()));
            childrenNumberAverageLabel.setText("Children number average: " + String.format("%.2f", getSimulationMap().getChildrenNumberAverage()));
        } else {
            energyAverageLabel.setText("Energy average: -/-");
            childrenNumberAverageLabel.setText("Children number average: -/-");
        }


        //populating the series with data
        animalSeries.getData().add(new XYChart.Data(getSimulationMap().getEra(), getSimulationMap().getAnimalsNumber()));
        plantsSeries.getData().add(new XYChart.Data(getSimulationMap().getEra(), getSimulationMap().getPlantsList().size()));

        while (animalSeries.getData().size() > 500) {
            animalSeries.getData().remove(0);
        }
        while (plantsSeries.getData().size() > 500) {
            plantsSeries.getData().remove(0);
        }

        if(followedPosition != null){
            followTime --;
            if(followTime == 0){
                timer.stop();
                List<Integer> stats = getSimulationMap().followedAnimalStats();

                followedAnimalDeathEra.setText("Death era: " + stats.get(0));
                followedAnimalChildrenNumber.setText("Children number: " + stats.get(1));
                followedAnimalDescendantsNumber.setText("Descendants number: " + stats.get(2));
                followedPosition = null;
            }
        }
    }

    public IWorldMap getSimulationMap() {
        return simulationMap;
    }
}
