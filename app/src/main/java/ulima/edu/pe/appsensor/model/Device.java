package ulima.edu.pe.appsensor.model;

/**
 * Created by hernan on 2/16/18.
 */

public class Device {
    private String name;
    private String address;

    public Device(String name, String address){
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("Nombre: %s\nDirección: %s", this.getName(), this.getAddress());
    }
}
