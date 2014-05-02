/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package collisiondetection;

import java.awt.geom.Point2D;
import java.util.Date;

/**
 *
 * @author Nino
 */
public class VehicleData {
    public static class Coordinates {
        private final Point2D.Double coordinates; // current coordinates of the vehicle in 2D space
        private final Date registeredTime;        // current time

        public Coordinates(Point2D.Double coordinates, Date registeredTime) {
            this.coordinates = coordinates;
            this.registeredTime = registeredTime;
        }
    }
    
    private final String name;              // name of the vehicle
    private Coordinates coordinates; 

    public VehicleData(String name, Coordinates coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }
    
    public String getName() {
        return name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    
    public Date getCoordinatesRegisteredDate() {
        return coordinates.registeredTime;
    }
    
    public Point2D.Double getCoordinatesValues() {
        return coordinates.coordinates;
    }
}
