/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collisiondetection;

import java.awt.geom.Point2D;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nino
 */
public class Vehicle implements Runnable {

    private static final int TIME_SPAN = 2000; // 1 second
    private final Thread collisionDetection;

    private final VehicleData data;
    private final CDReading readingsList; // will be instantiated with the same object for all threads

    public Vehicle(VehicleData data, CDReading readingsList) {
        this.data = data;
        this.readingsList = readingsList;
        collisionDetection = new Thread(new CollisionDetection(readingsList));
    }

    private void updateCoordinates() {
        synchronized (collisionDetection) {
            this.data.setCoordinates(new VehicleData.Coordinates(new Point2D.Double(this.data.getCoordinatesValues().getX() + 5.0, this.data.getCoordinatesValues().getY()), new Date())); // this should really be a variable, as well as having a direction
            System.out.println("\nVehicle " + data.getName()
                    + " coordinates: (" + this.data.getCoordinatesValues().getX()
                    + ", " + this.data.getCoordinatesValues().getY() + ")\nDate: " + this.data.getCoordinatesRegisteredDate());

            readingsList.addReading(data);

            // notify collision detection system of coordinate changes if list is full
            if (readingsList.getVehicleReadings().size() == CDReading.NUMBER_CARS * 2 && readingsList.getVehicleReadings().size() % 2 == 0) {
                //collisionDetection.notify();
            }
        }
    }

    @Override
    public void run() {
        // Spawn a new thread to control the vehicle's collision detection system
        System.out.println("Vehicle " + data.getName() + " initiated.\nStarting collision detection system...");
        collisionDetection.start();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(TIME_SPAN);
                updateCoordinates();
                //System.out.println("Readings count: " + readingsList.getVehicleReadings().size());

            } catch (InterruptedException ex) {
                Logger.getLogger(Vehicle.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
