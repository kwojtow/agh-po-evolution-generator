package sample;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class WorldMap implements IWorldMap {
    private MapProperties properties = new MapProperties(true);
    private int animalsNumber = 0;
    private int era;

    private TreeMap<Vector2D, ArrayList<Animal>> animalsList = new TreeMap<>(new Vector2DComparator());
    private TreeMap<Vector2D, Plant> plantsList = new TreeMap<>(new Vector2DComparator());

    private HashMap<Genes, Integer> dominantGenes = new HashMap<>();
    private Set<Vector2D> freePlaces = new HashSet<>();

    private long aliveAnimalsEnergySum = 0;
    private long deadAnimalsNumber = 0;
    private long deadAnimalsLifeTimeSum = 0;
    private long aliveAnimalsChildrenNumberSum = 0;

    private Animal followedAnimal;

    private int startEraForStats = 0;
    private int endEraForStats = 0;

    private long globalAnimalsNumber;
    private long globalPlantsNumber;
    private HashMap<Genes, Integer> globalDominantGenes = new HashMap<>();
    private long globalEnergyAveragesSum;
    private long globalLifeTimeAverageSum;
    private long globalChildrenNumberAverageSum;


    public WorldMap() {
        for (int i = 0; i < this.getProperties().getWidth(); i++) {
            for (int j = 0; j < this.getProperties().getHeight(); j++) {
                freePlaces.add(new Vector2D(i, j));
            }
        }
        generateFirstAnimals();
    }

    /**
     * Sorts map where keys are genes and values are integer number of animals having given genes
     *
     * @param genesSet map of genes and number of animals having them
     * @return returns map sorted by value
     */
    public static Map<Genes, Integer> sortByValue(final Map<Genes, Integer> genesSet) {

        return genesSet.entrySet()
                .stream()
                .sorted((Map.Entry.<Genes, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * Method with simulate one era, it does in sequence each steps: moves animals, feeds animals, breeds animals, generates plants and generates stats if necessary
     */
    @Override
    public void nextEra() {
        this.era = this.getEra() + 1;
        this.moveAnimals();
        this.feedAnimals();
        this.breedAnimals();
        this.generatePlants();
        this.generateGlobalStats();
    }

    /**
     * Set the animal on given position with given id as followed
     *
     * @param position position of animal to follow
     * @param id       id of animal to follow
     * @return returns genes of followed animal
     */
    @Override
    public Genes startFollowAnimal(Vector2D position, long id) {
        AtomicReference<Genes> results = new AtomicReference<>();
        this.getAnimalsList().get(position).forEach((animal -> {
            if (animal.getId() == id)
                this.followedAnimal = animal;
            animal.setFollowed(true);
            results.set(animal.getGenes());
        }));
        return results.get();
    }

    /**
     * Calculates followed animals stats and return them as list
     *
     * @return returns three-element list of followed animal where first element is: death era, second: children number, third: descendants number
     */
    @Override
    public List<Integer> followedAnimalStats() {
        List<Integer> results = new ArrayList<>();
        if (this.followedAnimal.getDeathEra() > 0 && this.followedAnimal.getDeathEra() < this.getEra()) {
            results.add(this.followedAnimal.getDeathEra());
        } else {
            results.add(null);
        }
        results.add(this.followedAnimal.getChildrenBornDuringFollowing().size());
        results.add(followedAnimal.countAnimalsDescendants());

        followedAnimal.stopFollowing();
        this.followedAnimal = null;

        return results;
    }

    /**
     * Selects all animals which have the same genes set as dominant
     *
     * @return returns list of animals having dominant genes set
     */
    @Override
    public List<Animal> allAnimalsWithDominantGenes() {
        List<Animal> animalsWithDominantGenes = new ArrayList<>();
        this.getAnimalsList().forEach((position, animalsOnTheSamePosition) -> {
            animalsOnTheSamePosition.forEach((animal -> {
                if (animal.getGenes().equals(dominantGenes.keySet().toArray()[0])) {
                    animalsWithDominantGenes.add(animal);
                }
            }));
        });
        return animalsWithDominantGenes;
    }

    /**
     * Set params to generate global stats
     *
     * @param startEra the era when data collecting for global stats starts
     * @param stopEra  the era when data collecting for global stats stops
     */
    @Override
    public void setGlobalStatsParams(int startEra, int stopEra) {
        this.startEraForStats = startEra;
        this.endEraForStats = stopEra;
    }

    /**
     * Generates global stats and saves them to file
     */
    private void generateGlobalStats() {
        if (this.getEra() >= this.startEraForStats && this.getEra() <= this.endEraForStats) {
            this.globalAnimalsNumber += this.getAnimalsNumber();
            this.globalPlantsNumber += this.getPlantsList().size();

            Genes currentlyDominantGenes = (Genes) this.dominantGenes.keySet().toArray()[0];
            if (!this.globalDominantGenes.containsKey(currentlyDominantGenes))
                this.globalDominantGenes.put(currentlyDominantGenes, 0);
            this.globalDominantGenes.replace(currentlyDominantGenes, this.globalDominantGenes.get(currentlyDominantGenes), this.globalDominantGenes.get(currentlyDominantGenes) + 1);

            this.globalEnergyAveragesSum += getAliveAnimalsEnergyAverage();
            this.globalLifeTimeAverageSum += getDeadAnimalsLifeTimeAverage();
            this.globalChildrenNumberAverageSum += getChildrenNumberAverage();
        } else {
            this.globalAnimalsNumber = 0;
            this.globalPlantsNumber = 0;
            this.globalDominantGenes.clear();
            this.globalEnergyAveragesSum = 0;
            this.globalLifeTimeAverageSum = 0;
            this.globalChildrenNumberAverageSum = 0;
        }
        if (this.getEra() == this.endEraForStats) {
            FileWriter newFile = null;
            try {
                newFile = new FileWriter("resources/statsFrom" + this.startEraForStats + "To" + this.endEraForStats + ".txt");
                long periodOfTime = this.endEraForStats - this.startEraForStats;

                sortByValue(this.globalDominantGenes);

                if (periodOfTime > 0) {
                    newFile.write("Animals number: " + this.globalAnimalsNumber / periodOfTime + " \n" +
                            "Plants number: " + this.globalPlantsNumber / periodOfTime + " \n" +
                            "Dominant genes: " + this.globalDominantGenes.keySet().toArray()[0] + " \n" +
                            "Animals energy average: " + this.globalEnergyAveragesSum / periodOfTime + " \n" +
                            "Dead animals life time average: " + this.globalLifeTimeAverageSum / periodOfTime + " \n" +
                            "Animals children number average: " + this.globalChildrenNumberAverageSum / periodOfTime + " \n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    newFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Method generating plants in each era - one inside and one outside the jungle
     */
    void generatePlants() {
        placePlant(new Plant(), true);
        placePlant(new Plant(), false);
    }

    /**
     * Method placing grass on the map
     *
     * @param plant    plant to be placed
     * @param inJungle points the area to place the plant
     */
    void placePlant(Plant plant, boolean inJungle) {
        int jungleWidth = (int) (this.getProperties().getWidth() * Math.sqrt(this.getProperties().getJungleRatio()));
        int jungleHeight = (int) (this.getProperties().getHeight() * Math.sqrt(this.getProperties().getJungleRatio()));
        Vector2D junglePosition = new Vector2D((this.getProperties().getWidth() - jungleWidth) / 2, (this.getProperties().getHeight() - jungleHeight) / 2);

        if (freePlaces.size() > 0) {

            Random generator = new Random();
            Vector2D position = null;

            HashSet<Vector2D> freePlacesInJungle = new HashSet<>();
            for (Vector2D freePlace : freePlaces) {
                if (freePlace.greaterThan(junglePosition) && freePlace.lessThan(junglePosition.add(new Vector2D(jungleWidth, jungleHeight)))) {
                    freePlacesInJungle.add(freePlace);
                }
            }
            HashSet<Vector2D> freePlacesOutsideJungle = new HashSet<>();
            freePlacesOutsideJungle.addAll(freePlaces);
            freePlacesOutsideJungle.removeAll(freePlacesInJungle);
            if (inJungle && freePlacesInJungle.size() > 0) {
                position = (Vector2D) freePlacesInJungle.toArray()[generator.nextInt(freePlacesInJungle.size())];
            } else if (freePlacesOutsideJungle.size() > 0) {
                position = (Vector2D) freePlacesOutsideJungle.toArray()[generator.nextInt(freePlacesOutsideJungle.size())];
            }

            if (position != null) {
                plant.setPosition(position);
                this.getPlantsList().put(position, plant);
                freePlaces.remove(position);
            }
        }
    }

    /**
     * Method called when simulation starts. Generates first animals.
     */
    private void generateFirstAnimals() {
        for (int i = 0; i < this.getProperties().getStartAnimalsNumber(); i++) {
            this.placeAnimal(new Animal(), null);
        }
    }

    /**
     * Places animal at a random free position on the map if possible, when all positions occupied, places animal on random occupied position. If parent position given places animal near the parent position.
     *
     * @param animal          animal to place
     * @param parentsPosition parent animal position, null when it is one of initial animals
     */
    void placeAnimal(Animal animal, Vector2D parentsPosition) {
        Vector2D position = null;
        if (parentsPosition == null) {
            animal.setEnergy(this.getProperties().getStartEnergy());
            Random generator = new Random();
            position = (Vector2D) freePlaces.toArray()[generator.nextInt(freePlaces.size())];
        } else {
            List<Vector2D> freePositions = new ArrayList<>();
            boolean allOccupied = true;
            for (Direction direction : Direction.values()) {
                if (freePlaces.contains(parentsPosition.add(direction.convertToUnitVector()))) {
                    allOccupied = false;
                    freePositions.add(parentsPosition.add(direction.convertToUnitVector()));
                }
            }
            if (!allOccupied) {
                position = freePositions.get(new Random().nextInt(freePositions.size()));
            } else {
                position = new Vector2D(parentsPosition.getX(), parentsPosition.getY());
                position.translate(Direction.values()[(new Random()).nextInt(8)].convertToUnitVector());
            }
        }
        position.makeVectorInsideTheBounds(getProperties().getWidth(), getProperties().getHeight());
        animal.setPosition(position);
        animal.setBornEra(this.getEra());
        if (!this.getAnimalsList().containsKey(position))
            this.getAnimalsList().put(new Vector2D(position.getX(), position.getY()), new ArrayList<>());
        this.getAnimalsList().get(position).add(animal);
        if (!this.dominantGenes.containsKey(animal.getGenes()))
            this.dominantGenes.put(animal.getGenes(), 0);
        this.dominantGenes.replace(animal.getGenes(), (this.dominantGenes.get(animal.getGenes()) + 1));
        sortByValue(dominantGenes);
        animalsNumber = animalsNumber + 1;
        freePlaces.remove(position);
    }

    /**
     * Moves all animals in random directions
     */
    void moveAnimals() {
        List<Animal> movedAnimals = new ArrayList<>();
        getAnimalsList().forEach((position, animalsOnTheSamePosition) -> {
            animalsOnTheSamePosition.forEach(animal -> {
                Random generator = new Random();
                Direction translation = Direction.values()[animal.getGenes().getGenesSet().get(generator.nextInt(32))];
                freePlaces.add(animal.getPosition());
                animal.move(translation, this.getProperties().getMoveEnergy(), this.getProperties().getWidth(), this.getProperties().getHeight());
                movedAnimals.add(animal);
            });

        });
        updateAnimalsList(movedAnimals);
    }

    /**
     * Updates data structure after animals positions changes
     *
     * @param movedAnimals list of animals which changes position
     */
    private void updateAnimalsList(List<Animal> movedAnimals) {
        getAnimalsList().clear();
        dominantGenes.clear();
        movedAnimals.forEach((animal) -> {
            if (animal.getEnergy() > 0) {
                freePlaces.remove(animal.getPosition());
                if (!getAnimalsList().containsKey(animal.getPosition()))
                    getAnimalsList().put(new Vector2D(animal.getPosition().getX(), animal.getPosition().getY()), new ArrayList<>());
                getAnimalsList().get(animal.getPosition()).add(animal);
                if (!this.dominantGenes.containsKey(animal.getGenes()))
                    this.dominantGenes.put(animal.getGenes(), 0);
                this.dominantGenes.replace(animal.getGenes(), (this.dominantGenes.get(animal.getGenes()) + 1));
            } else {
                animalsNumber -= 1;
                deadAnimalsNumber += 1;
                deadAnimalsLifeTimeSum = deadAnimalsLifeTimeSum + (getEra() - animal.getBornEra());
                animal.setDeathEra(getEra());
            }
            sortByValue(dominantGenes);
        });
    }

    /**
     * Breed every pair of animals which meet the conditions
     */
    void breedAnimals() {
        HashMap<Vector2D, Animal> newAnimals = new HashMap<>();
        this.getAnimalsList().forEach((position, list) -> {
            if (list.size() >= 2) {
                List<Animal> theStrongestPair = selectTheStrongestPair(position);
                if (theStrongestPair.get(1).getEnergy() >= (int) (0.5 * this.getProperties().getStartEnergy())) {
                    newAnimals.put(position, theStrongestPair.get(0).breed(theStrongestPair.get(1)));
                }
            }
        });
        newAnimals.forEach((parentsPosition, animal) -> this.placeAnimal(animal, parentsPosition));

        aliveAnimalsChildrenNumberSum = 0;
        aliveAnimalsEnergySum = 0;
        this.getAnimalsList().forEach((position, animalsOnTheSamePosition) -> {
            animalsOnTheSamePosition.forEach((animal -> {
                aliveAnimalsChildrenNumberSum += animal.getChildrenNumber();
                aliveAnimalsEnergySum += animal.getEnergy();
            }));
        });
    }

    /**
     * Select pair of animals having the most energy on given position
     *
     * @param position position to choose animal on
     * @return two-element list of animals
     */
    private List<Animal> selectTheStrongestPair(Vector2D position) {
        List<Animal> theStrongestPair = new ArrayList<>();
        this.getAnimalsList().get(position).sort((first, second) -> second.getEnergy() - first.getEnergy());
        theStrongestPair.add(this.getAnimalsList().get(position).get(0));
        theStrongestPair.add(this.getAnimalsList().get(position).get(1));
        return theStrongestPair;
    }

    /**
     * Feed every animal which have plant on the same position, increase animals energy
     */
    void feedAnimals() {
        this.getAnimalsList().forEach((position, animalsOnTheSamePosition) -> {
            if (!animalsOnTheSamePosition.isEmpty()) {
                if (this.getPlantsList().containsKey(position) && this.getPlantsList().get(position) != null) {
                    List<Animal> theStrongest = this.selectTheStrongest(position);
                    theStrongest.forEach(animal -> {
                        animal.eat(this.getProperties().getPlantEnergy() / theStrongest.size());
                    });
                    this.getPlantsList().remove(position);
                    freePlaces.add(position);
                }
            }
        });
    }

    /**
     * Selects the animal having the most energy on given position
     *
     * @param position position to choose animal on
     * @return the animal having the most energy
     */
    private List<Animal> selectTheStrongest(Vector2D position) {
        List<Animal> theStrongest = new ArrayList<>();
        if (this.getAnimalsList().containsKey(position) && this.getAnimalsList().get(position).size() > 0) {
            this.getAnimalsList().get(position).sort((first, second) -> second.getEnergy() - first.getEnergy());
            this.getAnimalsList().get(position).forEach(animal -> {
                if (animal.getEnergy() == this.getAnimalsList().get(position).get(0).getEnergy())
                    theStrongest.add(animal);
            });
        }
        return theStrongest;
    }

    /**
     * @return returns map properties object
     */
    @Override
    public MapProperties getProperties() {
        return properties;
    }

    /**
     * @return returns map of animals, where positions are keys and list of animals on given positions are values
     */
    @Override
    public TreeMap<Vector2D, ArrayList<Animal>> getAnimalsList() {
        return animalsList;
    }

    /**
     * @return returns current number of alive animals
     */
    @Override
    public int getAnimalsNumber() {
        return this.animalsNumber;
    }

    /**
     * @return returns map of plants where positions are keys and plants are values
     */
    @Override
    public TreeMap<Vector2D, Plant> getPlantsList() {
        return plantsList;
    }

    /**
     * @return returns current era on the map
     */
    @Override
    public int getEra() {
        return this.era;
    }

    /**
     * @return returns dominant genes
     */
    @Override
    public Genes getDominantGenes() {
        if (dominantGenes.size() > 0)
            return (Genes) dominantGenes.keySet().toArray()[0];
        return null;
    }

    /**
     * @return returns average energy of alive animals
     */
    @Override
    public long getAliveAnimalsEnergyAverage() {
        if (this.getAnimalsNumber() > 0)
            return this.aliveAnimalsEnergySum / this.getAnimalsNumber();
        return 0;
    }

    /**
     * @return returns average of dead animals life time
     */
    @Override
    public long getDeadAnimalsLifeTimeAverage() {
        if (this.deadAnimalsNumber > 0)
            return this.deadAnimalsLifeTimeSum / this.deadAnimalsNumber;
        return 0;
    }

    /**
     * @return returns average of alive animals children number
     */
    @Override
    public double getChildrenNumberAverage() {
        if (this.getAnimalsNumber() > 0)
            return (double) this.aliveAnimalsChildrenNumberSum / (double) this.getAnimalsNumber();
        return 0;
    }
}
