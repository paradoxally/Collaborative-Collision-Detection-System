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
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // initialize the collision detection global reading storage
        CDReading reading = new CDReading();
        
        // create vehicle threads
        Thread vehicleA = new Thread(new Vehicle(new VehicleData("A", 
                            new VehicleData.Coordinates(new Point2D.Double(0, 300), new Date()), 16.67), reading, Vehicle.Direction.EAST));
        Thread vehicleB = new Thread(new Vehicle(new VehicleData("B", 
                        new VehicleData.Coordinates(new Point2D.Double(200, 133.44), new Date()), 13.88), reading, Vehicle.Direction.SOUTH));
        
        vehicleA.start();
        vehicleB.start();
    }
    
}
