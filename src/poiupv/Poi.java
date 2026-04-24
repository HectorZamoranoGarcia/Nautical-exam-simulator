package poiupv;

import javafx.geometry.Point2D;

/**
 * Represents a Point of Interest (POI) on the navigation chart.
 * Each POI has a unique code, a description, and a 2D position on the canvas.
 */
public class Poi {

    private String code;
    private String description;
    private Point2D position;

    /** Creates an empty POI with no data. */
    public Poi() {
    }

    /**
     * Creates a POI with all fields populated.
     *
     * @param code        a short identifier for this POI
     * @param description a human-readable description
     * @param x           the horizontal canvas coordinate
     * @param y           the vertical canvas coordinate
     */
    public Poi(String code, String description, double x, double y) {
        this.code = code;
        this.description = description;
        this.position = new Point2D(x, y);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    /**
     * Returns the horizontal canvas coordinate, or {@code 0} if position is not
     * set.
     *
     * @return the x coordinate
     */
    public double getX() {
        return position != null ? position.getX() : 0;
    }

    /**
     * Returns the vertical canvas coordinate, or {@code 0} if position is not set.
     *
     * @return the y coordinate
     */
    public double getY() {
        return position != null ? position.getY() : 0;
    }

    @Override
    public String toString() {
        return code + " Ã¢â‚¬â€œ " + description;
    }
}
