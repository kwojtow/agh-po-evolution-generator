package sample;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MapTest {

    @Test
    public void removeDeadAnimals() {
    }

    @Test
    public void genratePlants() {
        WorldMap worldMap1 = new WorldMap();
        worldMap1.generatePlants();
        assertEquals(2, worldMap1.getPlantsList().size());
    }

    @Test
    public void moveAnimals() {
        WorldMap worldMap1 = new WorldMap();
        Animal animal1 = new Animal();
        animal1.setPosition(new Vector2D(1, 1));
        Animal animal2 = new Animal();
        animal2.setPosition(new Vector2D(1, 1));


        worldMap1.getAnimalsList().put(animal1.getPosition(), new ArrayList<>());
        worldMap1.getAnimalsList().get(animal1.getPosition()).add(animal1);
        worldMap1.getAnimalsList().get(animal2.getPosition()).add(animal2);

        worldMap1.moveAnimals();

        assertNotEquals(new Vector2D(1,1), animal1.getPosition());
        assertNotEquals(new Vector2D(1,1), animal2.getPosition());

    }

    @Test
    public void feedAnimals() {
        WorldMap worldMap1 = new WorldMap();
        Plant plant1 = new Plant();
        Animal animal1 = new Animal();
        Animal animal2 = new Animal();
        animal1.setEnergy(0);
        animal2.setEnergy(0);

        Vector2D position1 = new Vector2D(1,1);

        worldMap1.getPlantsList().put(position1, plant1);
        worldMap1.getAnimalsList().put(position1, new ArrayList<Animal>());
        worldMap1.getAnimalsList().get(position1).add(animal1);
        worldMap1.feedAnimals();
        assertEquals(worldMap1.getProperties().getPlantEnergy(), animal1.getEnergy());

        worldMap1.getPlantsList().put(position1, plant1);
        worldMap1.getAnimalsList().get(position1).add(animal2);
        worldMap1.feedAnimals();
        assertEquals(2* worldMap1.getProperties().getPlantEnergy(), animal1.getEnergy());
        assertEquals(0, animal2.getEnergy());

        worldMap1.getPlantsList().put(position1, plant1);
        animal1.setEnergy(0);
        animal2.setEnergy(animal1.getEnergy());
        worldMap1.feedAnimals();
        assertEquals((int) (0.5 * worldMap1.getProperties().getPlantEnergy()), animal1.getEnergy());
        assertEquals((int) (0.5 * worldMap1.getProperties().getPlantEnergy()), animal2.getEnergy());



    }

    @Test
    public void breedAnimals() {
        WorldMap worldMap1 = new WorldMap();
        Animal animal1 = new Animal();
        Animal animal2 = new Animal();
        animal1.setEnergy(500);
        animal2.setEnergy(500);

        Vector2D position1 = new Vector2D(1,1);

        worldMap1.getAnimalsList().put(position1, new ArrayList<Animal>());
        worldMap1.getAnimalsList().get(position1).add(animal1);
        worldMap1.getAnimalsList().get(position1).add(animal2);
        worldMap1.breedAnimals();

        List<Vector2D> newAnimalPosition = new ArrayList<>();

        worldMap1.getAnimalsList().forEach((position, list)-> {
            if(position != position1){
                newAnimalPosition.add(position);
            }
        });

        assertFalse(newAnimalPosition.isEmpty());
        newAnimalPosition.forEach(position ->{
            assertTrue(position.greaterThan(position.add(Direction.SOUTHWEST.convertToUnitVector()).add(Direction.SOUTHWEST.convertToUnitVector())));
            assertTrue(position.lessThan(position.add(Direction.NORTHEAST.convertToUnitVector()).add(Direction.NORTHEAST.convertToUnitVector())));
        });
    }

    @Test
    public void placePlant() {
        WorldMap worldMap1 = new WorldMap();
        assertTrue(worldMap1.getPlantsList().isEmpty());
        Plant plant = new Plant();
        worldMap1.placePlant(plant, true);
        assertFalse(worldMap1.getPlantsList().isEmpty());
        assertEquals(plant, worldMap1.getPlantsList().get(plant.getPosition()));
    }

    @Test
    public void placeAnimal() {
        WorldMap worldMap1 = new WorldMap();
        assertTrue(worldMap1.getPlantsList().isEmpty());
        Animal animal = new Animal();
        worldMap1.placeAnimal(animal, null);
        assertFalse(worldMap1.getAnimalsList().isEmpty());
        assertEquals(animal, worldMap1.getAnimalsList().get(animal.getPosition()).get(0));
    }
}