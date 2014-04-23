/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package collisiondetection;

import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Nino
 */
public class Vehicle implements Runnable {
    private static final int TIME_SPAN = 1000; // 1 second 

    private final String name; // name of the vehicle
    private Point2D.Double coordinates; // current coordinates of the vehicle in 2D space

    public Vehicle(String name, Point2D.Double coordinates) {
        this.name = name;
        this.coordinates = coordinates; // starting coordinates
    }

    public String getName() {
        return name;
    }

    public Point2D.Double getCoordinates() {
        return coordinates;
    }
    
    private void updateCoordinates() {
       this.coordinates = new Point2D.Double(this.coordinates.getX() + 5.0, this.coordinates.getY()); // this should really be a variable, as well as having a direction
       System.out.println("Vehicle " + name + 
                        " coordinates: (" + this.coordinates.getX() + 
                        ", " + this.coordinates.getY() + ")"); 
    }
    
    @Override
    public void run() {
        // Spawn a new thread to control the vehicle's collision detection system
        System.out.println("Vehicle " + name + " initiated.\nStarting collision detection system...");
        
        while(!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(TIME_SPAN);
                updateCoordinates();
                // notify collision detection system of coordinate changes
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
