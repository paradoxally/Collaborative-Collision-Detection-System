/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collisiondetection;

import java.util.Iterator;
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

    public void addReading(VehicleData vehicleData) {
        if (this.vehicleReadings.size() < 2) { // 0 or 1 reading is okay, we can add without an issue
            this.vehicleReadings.add(vehicleData);
        } else if (this.vehicleReadings.size() <= 4) {
            CopyOnWriteArrayList<Integer> matchedReadingIndexes = new CopyOnWriteArrayList<>();
            Integer i = 0;
            for (Iterator<VehicleData> it = vehicleReadings.iterator(); it.hasNext(); i++) {
                VehicleData vd = it.next();
                if (vd.getName().equals(vehicleData.getName())) {
                    matchedReadingIndexes.add(i);
                }
            }

            System.out.println("Readings for vehicle " + vehicleData.getName() + " found: " + matchedReadingIndexes.size());
            switch (matchedReadingIndexes.size()) {        
                case 1: {
                    vehicleReadings.add(vehicleData);
                    break;
                }

                case 2: {
                    if (vehicleReadings.get(matchedReadingIndexes.get(1)).getCoordinatesRegisteredDate().after(vehicleReadings.get(matchedReadingIndexes.get(0)).getCoordinatesRegisteredDate())) { // typically, the second element is newer, so we replace it
                        vehicleReadings.set(matchedReadingIndexes.get(1), vehicleData);
                    } else {
                        vehicleReadings.set(matchedReadingIndexes.get(0), vehicleData);
                    }
                    break;
                }
            }
        }
    }
}
