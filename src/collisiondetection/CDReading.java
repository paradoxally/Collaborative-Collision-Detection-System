/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collisiondetection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Nino
 */
public class CDReading {

    public static final int NUMBER_CARS = 2; // 2 cars by default

    private final CopyOnWriteArrayList<VehicleData> vehicleReadings;

    public CDReading() {
        this.vehicleReadings = new CopyOnWriteArrayList<>();
    }

    public CopyOnWriteArrayList<VehicleData> getVehicleReadings() {
        return vehicleReadings;
    }

    public synchronized ArrayList<Integer> getReadingsForVehicle(String vehicleName) {
        ArrayList<Integer> matchedReadingIndexes = new ArrayList<>();
        Integer i = 0;
        for (Iterator<VehicleData> it = vehicleReadings.iterator(); it.hasNext(); i++) {
            VehicleData vd = it.next();
            if (vd.getName().equals(vehicleName)) {
                matchedReadingIndexes.add(i);
            }
        }

        return matchedReadingIndexes;
    }

    public synchronized void addReading(VehicleData vehicleData) {
        System.out.println("Number of readings before insertion: " + this.vehicleReadings.size());

        if (this.vehicleReadings.size() < NUMBER_CARS) { // less than the # of cars is okay, we can add without an issue
            this.vehicleReadings.add(vehicleData);
        } else if (this.vehicleReadings.size() <= NUMBER_CARS * 2) {
            //System.err.println("Okay, I have to replace the latest reading of vehicle " + vehicleData.getName() + "...");
            ArrayList<Integer> matchedReadingIndexes = getReadingsForVehicle(vehicleData.getName());

            //System.out.println("Readings for vehicle " + vehicleData.getName() + " found: " + matchedReadingIndexes.size());
            switch (matchedReadingIndexes.size()) {
                case 0:
                case 1: {
                    vehicleReadings.add(vehicleData);
                    break;
                }

                case 2: {
                    if (vehicleReadings.get(matchedReadingIndexes.get(1)).getCoordinatesRegisteredDate().after(vehicleReadings.get(matchedReadingIndexes.get(0)).getCoordinatesRegisteredDate())) { // typically, the second element is newer, so we replace it
                        // save the old reading to the first reading
                        vehicleReadings.set(matchedReadingIndexes.get(0), vehicleReadings.get(matchedReadingIndexes.get(1)));
                        // and now we can replace it knowing we've saved the older reading
                        vehicleReadings.set(matchedReadingIndexes.get(1), vehicleData);
                        
                    } else {
                        // same, but reversed
                        vehicleReadings.set(matchedReadingIndexes.get(1), vehicleReadings.get(matchedReadingIndexes.get(0)));
                        vehicleReadings.set(matchedReadingIndexes.get(0), vehicleData);
                    }
                    break;
                }
            }
        }
        
        for (VehicleData vehicleReading : vehicleReadings) {
            System.out.println("Vehicle Readings: " + vehicleReading.getCoordinatesValues() + " " + vehicleReading.getName());
        }
    }
}
