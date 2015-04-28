/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collisiondetection;

import collisiondetection.VehicleData.Direction;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nino
 */
public class Vehicle implements Runnable {
    private static final int TIME_SPAN = 5000; // 5 seconds
    private final Thread collisionDetection;

    private final VehicleData data;
    private final CDReading readingsList; // will be instantiated with the same object for all threads
    private boolean justStartedCar;

    public Vehicle(VehicleData data, CDReading readingsList) {
        this.data = data;
        this.readingsList = readingsList;
        collisionDetection = new Thread(new CollisionDetection(readingsList, data));
        collisionDetection.setName("CD for vehicle " + data.getName());
        this.justStartedCar = true;
    }

    public VehicleData getVehicleData() {
        return data;
    }

    private void updateCoordinates() {
        synchronized (collisionDetection) {
            double speed = data.calculateSpeed();

            if (!data.willHitEdge()) {
                if (this.justStartedCar) {
                    // if the car just started, use the coordinates given
                    this.justStartedCar = false;
                } else {
                    Direction direction = data.getDirection();
                    this.data.setCoordinates(new VehicleData.Coordinates(
                            new Point2D.Double((direction == Direction.WEST || direction == Direction.EAST
                                            ? this.data.getCoordinatesValues().getX() + speed
                                            : this.data.getCoordinatesValues().getX()),
                                    (direction == Direction.NORTH || direction == Direction.SOUTH
                                            ? this.data.getCoordinatesValues().getY() + speed
                                            : this.data.getCoordinatesValues().getY())),
                            new Date()));

                }
            } else {
                // change direction, maybe? or stop?
                System.err.println("Vehicle " + this.data.getName() + " will hit the edge of the area. Stopping...");
                Thread.currentThread().interrupt();
                return;
            }

            System.out.println("\nVehicle " + data.getName()
                    + " coordinates: (" + this.data.getCoordinatesValues().getX()
                    + ", " + this.data.getCoordinatesValues().getY() + ")\nDate: " + this.data.getCoordinatesRegisteredDate());

            try {
                readingsList.addReading((VehicleData) data.clone());
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
            }

            // notify collision detection system of coordinate changes if list is full
            if (readingsList.getVehicleReadings().size() == CDReading.NUMBER_CARS * 2 && readingsList.getVehicleReadings().size() % 2 == 0) {
                collisionDetection.notify();
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
                updateCoordinates();
                Thread.sleep(TIME_SPAN);
                //System.out.println("Readings count: " + readingsList.getVehicleReadings().size());

            } catch (InterruptedException ex) {
                // Dispose of objects safely
                collisionDetection.interrupt();
                System.err.println("Vehicle " + this.data.getName() + " has stopped.");
                return;
            }
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
