package devimap.com.miguiaprevia;

import java.io.Serializable;

/**
 * Created by nossopc on 23/09/16.
 */
public class Tag implements Serializable {

    private int id;
    private int posi_X;
    private  int posi_Y;
    private String key;
    private String place;
    private boolean intersection;

    public Tag() {}

    public Tag(int id, int posi_X, int posi_Y, String key, String place, boolean intersection) {
        this.id = id;
        this.posi_X = posi_X;
        this.posi_Y = posi_Y;
        this.key = key;
        this.place = place;
        this.intersection = intersection;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getPosi_X() {
        return posi_X;
    }
    public void setPosi_X(int posi_X) {
        this.posi_X = posi_X;
    }
    public int getPosi_Y() {
        return posi_Y;
    }
    public void setPosi_Y(int posi_Y) {
        this.posi_Y = posi_Y;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public boolean getIntersection() {
        return intersection;
    }
    public void setIntersection(boolean intersection) {
        this.intersection = intersection;
    }
    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public boolean isIntersection() {
        return intersection;
    }

}
