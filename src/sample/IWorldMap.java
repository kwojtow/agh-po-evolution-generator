package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public interface IWorldMap {

    /**
     * Changes world state by one era
     */
    void nextEra();

    /**
     * Set animal on given position with given id as followed
     *
     * @param position followed animals position
     * @param id       followed animal id
     * @return returns followed animals genes set
     */
    Genes startFollowAnimal(Vector2D position, long id);

    /**
     * Calculates followed animals stats and return them as list
     *
     * @return returns three-element list of followed animal where first element is: death era, second: children number, third: descendants number
     */
    List<Integer> followedAnimalStats();

    /**
     * Selects all animals which have the same genes set as dominant
     *
     * @return returns list of animals having dominant genes set
     */
    List<Animal> allAnimalsWithDominantGenes();

    /**
     * Set params to generate global stats
     *
     * @param startEra the era when data collecting for global stats starts
     * @param stopEra  the era when data collecting for global stats stops
     */
    void setGlobalStatsParams(int startEra, int stopEra);

    /**
     * @return returns map properties object
     */
    MapProperties getProperties();

    /**
     * @return returns map of animals, where positions are keys and list of animals on given positions are values
     */
    TreeMap<Vector2D, ArrayList<Animal>> getAnimalsList();

    /**
     * @return returns current alive animals number
     */
    int getAnimalsNumber();

    /**
     * @return returns map of plants where positions are keys and plants are values
     */
    TreeMap<Vector2D, Plant> getPlantsList();

    /**
     * @return returns current era on the map
     */
    int getEra();

    /**
     * @return returns dominant genes
     */
    Genes getDominantGenes();

    /**
     * @return returns alive animals energy average
     */
    long getAliveAnimalsEnergyAverage();

    /**
     * @return returns dead animals life time average
     */
    long getDeadAnimalsLifeTimeAverage();


    /**
     * @return returns alive animals children number average
     */
    double getChildrenNumberAverage();

}
