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
public class VehicleData implements Cloneable {
    public static class Coordinates {
        private final Point2D.Double coordinates; // current coordinates of the vehicle in 2D space
        private final Date registeredTime;        // current time
        
        public Coordinates(Point2D.Double coordinates, Date registeredTime) {
            this.coordinates = coordinates;
            this.registeredTime = registeredTime;
        }
    }
    
    public enum RoadCondition {
        DRY_ASPHALT(10.0),
        WET_ASPHALT(15.0),
        DRY_CONCRETE(20.0),
        WET_CONCRETE(25.0),
        SNOW(45.0),
        ICE(70.0);
        
        private final double safetyDistance;
        
        RoadCondition(double safetyDistance) {
            this.safetyDistance = safetyDistance;
        }

        public double getSafetyDistance() {
            return safetyDistance;
        }
    }
    
    private final String name;              // name of the vehicle
    private Coordinates coordinates; 
    private double speed; 
    private RoadCondition roadCondition; // condition of the road (Wet, Dry, Snow, Ice)

    public VehicleData(String name, Coordinates coordinates, double speed, RoadCondition roadCondition) {
        this.name = name;
        this.coordinates = coordinates;
        this.speed = speed;
        this.roadCondition = roadCondition;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public String getName() {
        return name;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public RoadCondition getRoadCondition() {
        return roadCondition;
    }

    public void setRoadCondition(RoadCondition roadCondition) {
        this.roadCondition = roadCondition;
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

    @Override
    public String toString() {
        return String.format("Road condition: %s (safety distance: %.1f)", 
                this.getRoadCondition(), 
                this.getRoadCondition().getSafetyDistance());
    }
    
    
}
