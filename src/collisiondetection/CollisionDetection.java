/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collisiondetection;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nino
 */
public class CollisionDetection implements Runnable {
    private final CDReading readingsList;
    private final ArrayList<String> vehicleNames;

    public CollisionDetection(CDReading readingsList) {
        this.readingsList = readingsList;
        this.vehicleNames = new ArrayList<>();
    }
    
    private synchronized ArrayList<String> fillVehicleNames() {
        for (VehicleData vehicleData : readingsList.getVehicleReadings()) {
            if(!vehicleNames.contains(vehicleData.getName())) {
                System.out.println("Vehicle " + vehicleData.getName() + " is not yet present. Adding...");
                vehicleNames.add(vehicleData.getName());
            }
        }
        return vehicleNames;
    }
    
    private synchronized double[] calculateDistanceTraveled() {
        double distances[] = new double[CDReading.NUMBER_CARS];
        
        // for each vehicle, calculate distance traveled
        int i = 0;
        for (String vehicleName : vehicleNames) {
            ArrayList<Integer> vehicleReadings = readingsList.getReadingsForVehicle(vehicleName);
            System.out.println("Vehicle Readings for " + vehicleName + ": " + vehicleReadings);
            Point2D.Double firstReading, secondReading;
            
            // check which is the first reading based on time
            if(readingsList.getVehicleReadings().get(vehicleReadings.get(0)).getCoordinatesRegisteredDate().before(readingsList.getVehicleReadings().get(vehicleReadings.get(1)).getCoordinatesRegisteredDate())) {
                System.err.println("HELLO");
                firstReading = readingsList.getVehicleReadings().get(vehicleReadings.get(0)).getCoordinatesValues();
                secondReading = readingsList.getVehicleReadings().get(vehicleReadings.get(1)).getCoordinatesValues();
            } else {
                firstReading = readingsList.getVehicleReadings().get(vehicleReadings.get(1)).getCoordinatesValues();
                secondReading = readingsList.getVehicleReadings().get(vehicleReadings.get(0)).getCoordinatesValues();
            }
            
            System.out.println("SR" + secondReading.getX());
            System.out.println("FR" + firstReading.getX());

            distances[i] = Math.sqrt(Math.pow((secondReading.getX() - firstReading.getX()), 2.0) + Math.pow((secondReading.getY() - firstReading.getY()), 2.0));
            System.out.println("Distance traveled for vehicle " + vehicleName + ": " + distances[i]);
            ++i;
        }
         
        return distances;//Math.sqrt(a);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // wait for notifications from the vehicle that owns this CD system (new readings)
                synchronized (Thread.currentThread()) { // the Vehicle class owns the thread object, so we can't use 'this'
                    Thread.currentThread().wait();
                    System.out.println("I was notified: " + Thread.currentThread().getName());
                    //System.out.println("Size: " + readingsList.getVehicleReadings().size());
                    //double[] distances = calculateDistanceTraveled();
                    if(vehicleNames.isEmpty()) {    // arraylist filled only once (when system is asked to calculate distances for the first time) 
                        fillVehicleNames();
                    }
                    calculateDistanceTraveled();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(CollisionDetection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
