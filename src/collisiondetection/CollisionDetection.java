/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collisiondetection;

import collisiondetection.VehicleData.Direction;
import java.awt.geom.Point2D;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Nino
 */
public class CollisionDetection implements Runnable {

    public static final double MIN_COORDINATE = 0.0;
    public static final double MAX_COORDINATE = 310.0;

    private static final double SAFE_DISTANCE = 15.0;

    private enum SafetyConditions {

        DIFFERENT_DIRECTIONS, DIFFERENT_LANES, NOT_WITHIN_DISTANCE, WITHIN_DISTANCE
    }

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

    private synchronized SafetyConditions withinSafetyDistance(VehicleData firstVehicle, VehicleData secondVehicle) {
        Direction direction;
        // check if vehicles are both going in the same direction
        if ((direction = firstVehicle.getDirection()) == secondVehicle.getDirection()) {
            Point2D.Double c1, c2;
            c1 = firstVehicle.getCoordinatesValues();
            c2 = secondVehicle.getCoordinatesValues();
            double safetyDistance = firstVehicle.getRoadCondition().getSafetyDistance();

            switch (direction) {
                case NORTH:
                case SOUTH: {
                    // depending on direction, check where they are located (must be same lane) and safety distance
                    if (c1.getX() == c2.getX()) {
                        if (abs(c2.getY() - c1.getY()) >= safetyDistance) {
                            return SafetyConditions.WITHIN_DISTANCE;
                        }
                    } else {
                        return SafetyConditions.DIFFERENT_LANES;
                    }
                    break;
                }

                case EAST:
                case WEST: {
                    if (c1.getY() == c2.getY()) {
                        System.out.format("C1 X: %f\nC2 X: %f\n", c1.getX(), c2.getX());
                        System.out.println("Distances: " + abs(c2.getX() - c1.getX()));
                        if (abs(c2.getX() - c1.getX()) >= safetyDistance) {
                            return SafetyConditions.WITHIN_DISTANCE;
                        }
                    } else {
                        return SafetyConditions.DIFFERENT_LANES;
                    }
                    break;
                }
            }

            return SafetyConditions.NOT_WITHIN_DISTANCE;
        } else {
            return SafetyConditions.DIFFERENT_DIRECTIONS;
        }
    }

    private synchronized Point2D.Double[][] calculateDistanceTraveled(String[] vehicleNames) {   // calculates distance traveled and returns coordinates according to parametric equations of type P(t) = P(0) + t(u), where P(0) is the first reading and u is the calculated distance between P1 - P0.
        Point2D.Double distances[][] = new Point2D.Double[2][3];

        // for each vehicle, calculate distance traveled
        int i = 0;
        for (String vehicleName : vehicleNames) {
            ArrayList<Integer> vehicleReadings = readingsList.getReadingsForVehicle(vehicleName);
            System.out.println("\nVehicle Readings for " + vehicleName + ": " + vehicleReadings);
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

        double xW0 = -(distances[0][0].getX() - distances[1][0].getX()); //W=P0-Q0
        System.out.println("xWO: " + xW0);
        double yW0 = -(distances[0][0].getY() - distances[1][0].getY());
        System.out.println("yWO: " + yW0);
        double u = distances[0][1].getX() + distances[1][1].getX();

        System.out.println("u: " + u);// u= P1-P0

        double v = -(distances[0][1].getY() + distances[1][1].getY());
        System.out.println("v: " + v); //v= Q1-Q0
        System.out.println("Numerator: " + Math.abs((xW0 * u) + (xW0 * v) - (yW0 * u) + (yW0 * v)));
        System.out.println("Denominator: " + (Math.pow(u, 2) + Math.pow(v, 2)));
        double closestPoint = Math.abs(((xW0 * u) + (xW0 * v) - (yW0 * u) + (yW0 * v))) / (Math.pow(u, 2) + Math.pow(v, 2));
        System.out.println("Closest point is: " + closestPoint);

        return closestPoint;
    }

    private synchronized Point2D.Double[] calculateVehiclesPositionAtPoint(double closestPointSeconds, Point2D.Double[][] distances) {
        Point2D.Double[] positions = new Point2D.Double[2];

        int i = 0;
        for (Point2D.Double[] distance : distances) {   // for each car
            positions[i] = new Point2D.Double(distance[0].getX() + closestPointSeconds * distance[1].getX(),
                    distance[0].getY() + closestPointSeconds * distance[1].getY());
            System.out.println("Position in " + closestPointSeconds + ": " + positions[i]);
            ++i;
        }

        return positions;
    }

    private synchronized HashMap<String, SafetyConditions> checkSafetyDistancesForAllVehicles() {
        HashMap<String, SafetyConditions> safetyDistanceChecks = new HashMap<>();
        for (String vehicleName : vehicleNames) {
            if (!vehicleName.equals(this.data.getName())) {
                safetyDistanceChecks.put(vehicleName, withinSafetyDistance(readingsList.getVehicleDataForName(this.data.getName()), readingsList.getVehicleDataForName(vehicleName)));

            }
        }

        return safetyDistanceChecks;
    }

    private void enforceSafetyDistanceChecks(HashMap<String, SafetyConditions> safetyDistanceChecks) {
        if (safetyDistanceChecks.size() > 0) {
            // debug statement
            System.out.println("CD for vehicle " + this.data.getName() + ": " + safetyDistanceChecks);

            for (Map.Entry vehicleSafetyRelationship : safetyDistanceChecks.entrySet()) {
                /* This is the only condition where we want slow down randomly by a small percentage of the vehicles' speed.
                 // Randomly assigning a slowdown speed percentage is best because slowing down both vehicles by the same given
                 // constant will not work.
                 //
                 // if they are headed in different directions, lanes, or within the necessary safety distances, this is fine.
                 */
                if (vehicleSafetyRelationship.getValue() == SafetyConditions.NOT_WITHIN_DISTANCE) {
                    double randomSlowdownPercentage = ThreadLocalRandom.current().nextInt(5, 31) / 100.0;
                    double calculatedSpeed = data.getSpeed() - data.getSpeed() * randomSlowdownPercentage;
                    System.err.format("Safety distance not respected. Reducing speed by %.1f%% "
                            + "(%.1f m/s -> %.1f m/s)...\n",
                            randomSlowdownPercentage * 100.0, this.data.getSpeed(), calculatedSpeed);
                    this.data.setSpeed(calculatedSpeed);
                }
            }
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // wait for notifications from the vehicle that owns this CD system (new readings)
                synchronized (Thread.currentThread()) { // the Vehicle class owns the thread object, so we can't use 'this'
                    Thread.currentThread().wait();
                    System.out.println("\nI was notified: " + Thread.currentThread().getName());

                    if (vehicleNames.isEmpty()) {    // arraylist filled only once (when system is asked to calculate distances for the first time) 
                        fillVehicleNames();
                    }

                    // Deal with safety distance checking
                    HashMap<String, SafetyConditions> safetyDistanceChecks = checkSafetyDistancesForAllVehicles();
                    enforceSafetyDistanceChecks(safetyDistanceChecks);

                    // If different directions, check head-on collision
                    for (Map.Entry vehicleSafetyRelationship : safetyDistanceChecks.entrySet()) {
                        if (vehicleSafetyRelationship.getValue() == SafetyConditions.DIFFERENT_DIRECTIONS) {
                            Direction thisVehicleDirection = readingsList.getVehicleDataForName(this.data.getName()).getDirection();
                            Direction thatVehicleDirection = readingsList.getVehicleDataForName((String) vehicleSafetyRelationship.getKey()).getDirection();
                            
                            // opposite direction check
                            if (thisVehicleDirection == Direction.NORTH && thatVehicleDirection == Direction.SOUTH
                                    || thisVehicleDirection == Direction.SOUTH && thatVehicleDirection == Direction.NORTH
                                    || thisVehicleDirection == Direction.WEST && thatVehicleDirection == Direction.EAST
                                    || thisVehicleDirection == Direction.EAST && thatVehicleDirection == Direction.WEST) {
                                System.err.println("HEAD ON COLLISION!!!!!");
                                String[] vehicles = {this.data.getName(), (String) vehicleSafetyRelationship.getKey()};

                                Point2D.Double distances[][] = calculateDistanceTraveled(vehicles);

                                if (distances.length == 2) {
                                    double closestPointSeconds = calculateClosestPoint(distances);
                                    Point2D.Double[] positions = calculateVehiclesPositionAtPoint(closestPointSeconds, distances);

                                    // assuming only TWO cars in this check
                                    double closestPoint = calculateClosestPoint(distances);
                                    double distance = positions[0].distance(positions[1]);
                                    System.out.println("Distance: " + distance / 20.0 + "\nSpeed: " + data.getSpeed());

                                    if (closestPoint < SAFE_DISTANCE) { // Head-on collision condition
                                        Direction newDirection = null;
                                        boolean validDirection = false;
                                        for (int i = 0; i < 3; i++) {
                                            if ((newDirection = Direction.randomDirection()) != this.data.getDirection()
                                                    && !this.data.willHitEdge(newDirection)) {
                                                validDirection = true;
                                                break;
                                            }
                                        }
                                        if (validDirection) {
                                            // change direction to a valid one if no other car has changed direction within the TIME_SPAN
                                            synchronized (readingsList) {
                                                if (!readingsList.hasDirectionChanged()) {
                                                    System.out.println("The vehicles are in risk of collision, changing direction to vehicle " + this.data.getName());
                                                    this.data.setDirection(newDirection);
                                                    readingsList.setChangedDirection(true);
                                                }
                                            }
                                        } else {
                                            System.err.println("Unfortunately no alternate direction is possible...");
                                        }
                                        //this.data.setDirection(Direction.SOUTH);
                                    }
                                }
                            }
                        }
                    }

                    /*Point2D.Double distances[][] = calculateDistanceTraveled();
                     if (distances.length == CDReading.NUMBER_CARS) {
                     double closestPointSeconds = calculateClosestPoint(distances);
                     Point2D.Double[] positions = calculateVehiclesPositionAtPoint(closestPointSeconds, distances);

                     // assuming only TWO cars in this check
                     double distance = positions[0].distance(positions[1]);
                     System.out.println("Distance: " + distance + "\nSpeed: " + data.getSpeed());
                     if (distance <= SAFE_DISTANCE) {    // too close for confort, let's reduce the speed of the vehicle (assuming distance is in meters)
                     System.err.format("Vehicles will be too close to each other in %.1f seconds. Reducing speed by 5%%...", closestPointSeconds);
                     data.setSpeed(data.getSpeed() - data.getSpeed() * SPEED_REDUCTION);
                     }
                     roadCondition(rCondition);
                     }*/
                }

            } catch (InterruptedException ex) {
                System.err.println("Vehicle " + this.data.getName() + "'s collision detection system has terminated.");
                return;

            }
        }
    }
}
