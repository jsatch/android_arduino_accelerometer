package ulima.edu.pe.appsensor.utils;

import java.util.Date;
import java.util.Scanner;

import ulima.edu.pe.appsensor.model.XYZData;

/**
 * Created by hernan on 2/16/18.
 */

public class SensorUtils {
    public static XYZData parseData(String cadena, long initTimestamp){
        XYZData data = new XYZData();
        Scanner s = new Scanner(cadena);
        String temp = s.next();
        data.setX(s.nextFloat());
        temp = s.next();
        data.setY(s.nextFloat());
        temp = s.next();
        data.setZ(s.nextFloat());
        data.setTimestamp((new Date().getTime() - initTimestamp) / 1000);

        return data;
    }

    public static XYZData parseData(byte[] posiciones, long initTimestamp){
        XYZData data = new XYZData();
        data.setX((float)posiciones[0]);
        data.setY((float)posiciones[1]);
        data.setZ((float)posiciones[2]);
        data.setTimestamp((new Date().getTime() - initTimestamp) / 1000);
        return data;
    }
}
