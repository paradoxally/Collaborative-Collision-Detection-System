/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package collisiondetection;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Nino
 */
public class CDReading {
    private final CopyOnWriteArrayList<VehicleData> vehicleReadings;

    public CDReading() {
        this.vehicleReadings = new CopyOnWriteArrayList<>();
    }

    public CopyOnWriteArrayList<VehicleData> getVehicleReadings() {
        return vehicleReadings;
    }   
    
    public boolean addReading(VehicleData vehicleData) {
        
        
        return true;
    }
}
