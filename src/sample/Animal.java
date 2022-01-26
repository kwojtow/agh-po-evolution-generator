package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Animal {

    private static long animalID = 0;
    private boolean followed = false;
    private long id;

    private List<Animal> childrenList = new ArrayList<>();
    private List<Animal> childrenBornDuringFollowing = new ArrayList<>();

    private long childrenNumber = 0;
    private int bornEra;
    private int deathEra;
    private int energy;
    private Vector2D position;
    private Direction direction;
    private Genes genes;

    /**
     * No arguments constructor creating animal in simple way
     */
    Animal() {
        this.setId(animalID);
        animalID += 1;

        Random generator = new Random();
        this.setDirection(Direction.values()[generator.nextInt(8)]);
        this.setGenes(new Genes());
    }

    /**
     * Two arguments constructor creating animal basing on parents parameters
     * @param mother first of parents animal
     * @param father second od parents animal
     */
    Animal(Animal mother, Animal father) {
        this.setId(animalID);
        animalID += 1;


        Random generator = new Random();
        this.setDirection(Direction.values()[generator.nextInt(8)]);
        this.setGenes(new Genes(mother.getGenes(), father.getGenes()));
    }

    /**
     * Change animals position in one of eight directions
     * @param direction direction in which animal moves
     * @param cost quantity of energy which is subtracted from animal energy when moves
     * @param mapWidth map width
     * @param mapHeight map height
     */
    void move(Direction direction, int cost, int mapWidth, int mapHeight) {
        Vector2D newPosition = this.getPosition().add(direction.convertToUnitVector());
        newPosition.makeVectorInsideTheBounds(mapWidth, mapHeight);
        this.setPosition(newPosition);
        rotate(direction);
        this.setEnergy(this.getEnergy() - cost);
    }

    /**
     * Rotates animal in given direction
     * @param direction direction in which animal rotates
     */
    void rotate(Direction direction) {
        this.setDirection(direction);
    }

    /**
     * Creates animals child basing on its and partners params
     * @param partner second animal to base child animal params on it
     * @return returns new animal
     */
    Animal breed(Animal partner) {
        int childEnergy = (int) (0.25 * this.getEnergy() + 0.25 * partner.getEnergy());
        this.setEnergy((int) (this.getEnergy() - 0.25 * this.getEnergy()));
        partner.setEnergy((int) (partner.getEnergy() - 0.25 * this.getEnergy()));
        Animal child =  new Animal(this, partner);
        child.setEnergy(childEnergy);

            this.setChildrenNumber(this.getChildrenNumber() + 1);
            partner.setChildrenNumber(partner.getChildrenNumber() + 1);
            this.getChildrenList().add(child);
        if(isFollowed()) {
            child.setFollowed(true);
            getChildrenBornDuringFollowing().add(child);
        }
        return child;
    }

    /**
     * Increases animals energy by given value
     * @param grassEnergy value by which animals energy is increased
     */
    void eat(int grassEnergy) {
        this.setEnergy(this.getEnergy() + grassEnergy);
    }

    /**
     * Counts animals descendants
     * @return number of descendants
     */
    int countAnimalsDescendants() {
        if (this.getChildrenBornDuringFollowing().size() == 0)
            return 0;
        else
            return this.getChildrenBornDuringFollowing().size() + (this.getChildrenBornDuringFollowing().stream().map((child -> child.countAnimalsDescendants())).reduce((number1, number2) -> number1 + number2)).get();
    }

    /**
     * Sets animals as not more followed
     */
    void stopFollowing() {
        this.getChildrenBornDuringFollowing().forEach(child -> {
            child.stopFollowing();
        });
        this.setFollowed(false);
        this.getChildrenBornDuringFollowing().clear();
    }


    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Animal> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(List<Animal> childrenList) {
        this.childrenList = childrenList;
    }

    public List<Animal> getChildrenBornDuringFollowing() {
        return childrenBornDuringFollowing;
    }

    public void setChildrenBornDuringFollowing(List<Animal> childrenBornDuringFollowing) {
        this.childrenBornDuringFollowing = childrenBornDuringFollowing;
    }

    public long getChildrenNumber() {
        return childrenNumber;
    }

    public void setChildrenNumber(long childrenNumber) {
        this.childrenNumber = childrenNumber;
    }

    public int getBornEra() {
        return bornEra;
    }

    public void setBornEra(int bornEra) {
        this.bornEra = bornEra;
    }

    public int getDeathEra() {
        return deathEra;
    }

    public void setDeathEra(int deathEra) {
        this.deathEra = deathEra;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Genes getGenes() {
        return genes;
    }

    public void setGenes(Genes genes) {
        this.genes = genes;
    }
}
