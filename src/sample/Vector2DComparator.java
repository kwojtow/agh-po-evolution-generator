package sample;

import java.util.Comparator;

public class Vector2DComparator implements Comparator<Vector2D> {
    /**
     * Compares two vector with each other
     * @param vector1 first vector to compare
     * @param vector2 second vector to compare
     * @return returns 0 when vectors equals, -1 when first lower than second and 1 when first greater than second
     */
    @Override
    public int compare(Vector2D vector1, Vector2D vector2){
        if((Math.sqrt(Math.pow(vector1.getX(), 2) + Math.pow(vector1.getY(), 2)) < Math.sqrt(Math.pow(vector2.getX(), 2) + Math.pow(vector2.getY(), 2)))){
            return -1;
        }
        else if((Math.sqrt(Math.pow(vector1.getX(), 2) + Math.pow(vector1.getY(), 2)) > Math.sqrt(Math.pow(vector2.getX(), 2) + Math.pow(vector2.getY(), 2)))){
            return 1;
        }
        else if(vector1.getX() == vector2.getX() && vector1.getY() == vector2.getY()){
            return 0;
        }
        else if(vector1.getX() > vector2.getX())
            return 1;
        else{
            return -1;
        }
    }
}
