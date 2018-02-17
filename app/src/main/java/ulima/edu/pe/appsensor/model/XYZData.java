package ulima.edu.pe.appsensor.model;

/**
 * Created by hernan on 2/16/18.
 */

public class XYZData {
    private Float x;
    private Float y;
    private Float z;
    private long timestamp;


    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getZ() {
        return z;
    }

    public void setZ(Float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("%f %f %f", this.getX(), this.getY(), this.getZ());
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
