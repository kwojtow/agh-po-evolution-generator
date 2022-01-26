package sample;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AnimalTest {

    @Test
    public void move() {
        List<Vector2D> endAnimalPositions = new ArrayList<>();
        endAnimalPositions.add(new Vector2D(2,3));
        endAnimalPositions.add(new Vector2D(3,3));
        endAnimalPositions.add(new Vector2D(3,2));
        endAnimalPositions.add(new Vector2D(3,1));
        endAnimalPositions.add(new Vector2D(2,1));
        endAnimalPositions.add(new Vector2D(1,1));
        endAnimalPositions.add(new Vector2D(1,2));
        endAnimalPositions.add(new Vector2D(1,3));

        int iterator = 0;
        Animal animal = new Animal();
        for (Vector2D endAnimalPosition : endAnimalPositions){
            animal.setPosition(new Vector2D(2,2));
            int energy = animal.getEnergy();
            animal.move(Direction.values()[iterator], 1, 100, 100);
            assertEquals(endAnimalPosition.getX(), animal.getPosition().getX());
            assertEquals(energy - 1, animal.getEnergy());
            iterator ++;
        }
    }

    @Test
    public void rotate() {
        Animal animal = new Animal();
        for (int i = 0; i < Direction.values().length; i++){
            animal.setDirection(Direction.SOUTH);
            animal.rotate(Direction.values()[i]);
            assertEquals(Direction.values()[i], animal.getDirection());
        }
    }

    @Test
    public void breed() {
        Animal fistAnimal = new Animal();
        Animal secondAnimal = new Animal();
        assertNotNull(fistAnimal.breed(secondAnimal));
        assertEquals((int) (fistAnimal.getEnergy() * 0.25 + secondAnimal.getEnergy() * 0.25), fistAnimal.breed(secondAnimal).getEnergy());
    }

    @Test
    public void eat() {
    }
}