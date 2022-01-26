package sample;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class MapProperties {
    private int width;
    private int height;
    private int startEnergy;
    private int moveEnergy;
    private int plantEnergy;
    private double jungleRatio;
    private int startAnimalsNumber;

    public MapProperties(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, int startAnimalsNumber) {
        this.width = width;
        this.height = height;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.jungleRatio = jungleRatio;
        this.startAnimalsNumber = startAnimalsNumber;
    }

    /**
     * Simple no params constructor
     */
    public MapProperties(){
    }

    /**
     * One param constructor which can loads properties from file
     * @param fromFile determines that properties should be loaded form file
     */
    public MapProperties(boolean fromFile){
        this(5, 5, 50, 1, 30, 0.1, 2);
        Gson gson = new Gson();
        JsonReader reader = null;
        MapProperties properties;

        try {
            reader = new JsonReader(new FileReader("resources/parameters.json"));
            properties = gson.fromJson(reader, MapProperties.class);

            this.width = properties.getWidth();
            this.height = properties.getHeight();
            this.startEnergy = properties.getStartEnergy();
            this.moveEnergy = properties.getMoveEnergy();
            this.plantEnergy = properties.getPlantEnergy();
            this.jungleRatio = properties.getJungleRatio();
            this.startAnimalsNumber = properties.getStartAnimalsNumber();

        } catch (FileNotFoundException e) {
            System.out.println("Can not open file: " + e.getMessage() + " - map was initialized with default values");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getStartEnergy() {
        return startEnergy;
    }

    public int getMoveEnergy() {
        return moveEnergy;
    }

    public int getPlantEnergy() {
        return plantEnergy;
    }

    public double getJungleRatio() {
        return jungleRatio;
    }

    public int getStartAnimalsNumber() {
        return startAnimalsNumber;
    }
}
