package sample;

import java.util.Objects;

public class Vector2D {
    private int x;
    private int y;

    /**
     * Two parameters constructor
     * @param x vector size on x axis
     * @param y vector size on y axis
     */
    Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Modifies vector by translating its by given value
     * @param translation value to translate vector by
     */
    void translate(Vector2D translation) {
        this.x = this.getX() + translation.getX();
        this.y = this.getY() + translation.getY();
    }

    /**
     * Checks if vector is less than given
     * @param vector vector to compare with
     * @return returns true when less tha given
     */
    boolean lessThan(Vector2D vector) {
        return this.getX() < vector.getX() && this.getY() < vector.getY();
    }

    /**
     * Checks if vector is greater than given
     * @param vector vector to compare with
     * @return returns true if greater than given
     */
    boolean greaterThan(Vector2D vector) {
        return this.getX() > vector.getX() && this.getY() > vector.getY();
    }

    /**
     * Creates new vector by adding given vector
     * @param vector vector to add
     * @return returns new vector
     */
    Vector2D add(Vector2D vector) {
        return new Vector2D(this.getX() + vector.getX(), this.getY() + vector.getY());
    }

    /**
     * Makes vector inside the bounds
     * @param xBound bound on x axis
     * @param yBound bound on y axis
     */
    public void makeVectorInsideTheBounds(int xBound, int yBound) {
        this.x = this.getX() % xBound;
        this.y = this.getY() % yBound;
        if (this.getX() < 0)
            this.x = this.getX() + xBound;
        if (this.getY() < 0)
            this.y = this.getY() + yBound;
    }


    /**
     * Compares vector go given
     * @param o vector to compare with
     * @return returns true if equals
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2D vector2D = (Vector2D) o;
        return getX() == vector2D.getX() &&
                getY() == vector2D.getY();
    }

    /**
     * Calculates hash code of vector
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
