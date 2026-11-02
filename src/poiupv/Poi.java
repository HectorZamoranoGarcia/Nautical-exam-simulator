package poiupv;

import javafx.geometry.Point2D;

/** Point on the navigation chart */
public class Poi {

    private String code;
    private String description;
    private Point2D position;

    public Poi() {
    }

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

    public double getX() {
        return position != null ? position.getX() : 0;
    }

    public double getY() {
        return position != null ? position.getY() : 0;
    }

    @Override
    public String toString() {
        return code + " – " + description;
    }
}
