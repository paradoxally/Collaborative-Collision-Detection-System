/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collisiondetection;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nino
 */
public class CollisionDetection implements Runnable {

    private final CDReading readingsList;
    private final VehicleData data;
    private final ArrayList<String> vehicleNames;

    public CollisionDetection(CDReading readingsList, VehicleData data) {
        this.readingsList = readingsList;
        this.data = data;
        this.vehicleNames = new ArrayList<>();
    }

    private synchronized ArrayList<String> fillVehicleNames() {
        for (VehicleData vehicleData : readingsList.getVehicleReadings()) {
            if (!vehicleNames.contains(vehicleData.getName())) {
                System.out.println("Vehicle " + vehicleData.getName() + " is not yet present. Adding...");
                vehicleNames.add(vehicleData.getName());
            }
        }
        return vehicleNames;
    }

    private synchronized Point2D.Double[][] calculateDistanceTraveled() {   // calculates distance traveled and returns coordinates according to parametric equations of type P(t) = P(0) + t(u), where P(0) is the first reading and u is the calculated distance between P1 - P0.
        Point2D.Double distances[][] = new Point2D.Double[CDReading.NUMBER_CARS][2];

        // for each vehicle, calculate distance traveled
        int i = 0;
        for (String vehicleName : vehicleNames) {
            ArrayList<Integer> vehicleReadings = readingsList.getReadingsForVehicle(vehicleName);
            System.out.println("Vehicle Readings for " + vehicleName + ": " + vehicleReadings);
            Point2D.Double firstReading, secondReading;

            // check which is the first reading based on time
            if (readingsList.getVehicleReadings().get(vehicleReadings.get(0)).getCoordinatesRegisteredDate().before(readingsList.getVehicleReadings().get(vehicleReadings.get(1)).getCoordinatesRegisteredDate())) {
                firstReading = readingsList.getVehicleReadings().get(vehicleReadings.get(0)).getCoordinatesValues();
                secondReading = readingsList.getVehicleReadings().get(vehicleReadings.get(1)).getCoordinatesValues();
            } else {
                firstReading = readingsList.getVehicleReadings().get(vehicleReadings.get(1)).getCoordinatesValues();
                secondReading = readingsList.getVehicleReadings().get(vehicleReadings.get(0)).getCoordinatesValues();
            }

            distances[i][0] = firstReading;
            distances[i][1] = new Point2D.Double(secondReading.getX() - firstReading.getX(), secondReading.getY() - firstReading.getY());
            ++i;
        }

        return distances;
    }

    private synchronized double calculateClosestPoint(Point2D.Double[][] distances) {
        double xW0 = -(distances[0][0].getX() - distances[1][0].getX());
        System.out.println("xWO: " + xW0);
        double yW0 = -(distances[0][0].getY() - distances[1][0].getY());
        System.out.println("yWO: " + yW0);
        double u = distances[0][1].getX() + distances[1][1].getX();
        System.out.println("u: " + u);
        double v = -(distances[0][1].getY() + distances[1][1].getY());
        System.out.println("v: " + v);
        System.out.println("Numerator: " + ((xW0 * u) + (xW0 * v) - (yW0 * u) + (yW0 * v)));
        System.out.println("Denominator: " + (Math.pow(u, 2) + Math.pow(v, 2)));
        double closestPoint = Math.abs(((xW0 * u) + (xW0 * v) - (yW0 * u) + (yW0 * v))) / (Math.pow(u, 2) + Math.pow(v, 2));
        System.out.println("Closest point is: " + closestPoint);

        return closestPoint;
    }
    
    private synchronized Point2D.Double[] calculateVehiclesPositionAtPoint(double closestPointSeconds, Point2D.Double[][] distances) {
        Point2D.Double[] positions = new Point2D.Double[CDReading.NUMBER_CARS];
        
        int i = 0;
        for (Point2D.Double[] distance : distances) {   // for each car
            positions[i] = new Point2D.Double(distance[0].getX() + closestPointSeconds * distance[1].getX(),
                                              distance[0].getY() + closestPointSeconds * distance[1].getY());
            System.out.println("Position in " + closestPointSeconds + ": " + positions[i]);
            ++i;
        }
        
        return positions;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // wait for notifications from the vehicle that owns this CD system (new readings)
                synchronized (Thread.currentThread()) { // the Vehicle class owns the thread object, so we can't use 'this'
                    Thread.currentThread().wait();
                    System.out.println("I was notified: " + Thread.currentThread().getName());

                    if (vehicleNames.isEmpty()) {    // arraylist filled only once (when system is asked to calculate distances for the first time) 
                        fillVehicleNames();
                    }

                    Point2D.Double distances[][] = calculateDistanceTraveled();
                    if (distances.length == CDReading.NUMBER_CARS) {
                        double closestPointSeconds = calculateClosestPoint(distances);
                        //this.data.setSpeed(0);
                        Point2D.Double[] positions = calculateVehiclesPositionAtPoint(closestPointSeconds, distances);
                        
                        // assuming only TWO cars in this check
                        double distance = positions[0].distance(positions[1]);
                        System.out.println("Distance: " + distance + "\nSpeed: " + data.getSpeed());
                        if (distance <= 7.5) {    // too close for confort, let's reduce the speed of the vehicle (assuming distance is in meters)
                            System.err.format("Vehicles will be too close to each other in %.1f seconds. Reducing speed by 5%%...", closestPointSeconds);
                            data.setSpeed(data.getSpeed() - data.getSpeed() * 0.05);
                        }
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(CollisionDetection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
