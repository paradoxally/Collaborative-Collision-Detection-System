/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package collisiondetection;

import java.awt.geom.Point2D;

/**
 *
 * @author HBK
 */
public class VehicleData {
    private final String name;          // name of the vehicle
    private Point2D.Double coordinates; // current coordinates of the vehicle in 2D space

    public VehicleData(String name, Point2D.Double coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }
    
    public String getName() {
        return name;
    }

    public void setCoordinates(Point2D.Double coordinates) {
        this.coordinates = coordinates;
    }

    public Point2D.Double getCoordinates() {
        return coordinates;
    }
}
