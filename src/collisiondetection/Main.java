/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package collisiondetection;

import java.awt.geom.Point2D;

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
        Thread vehicleA = new Thread(new Vehicle(new VehicleData("A", new Point2D.Double(16.63, 300.0)), reading));
        Thread vehicleB = new Thread(new Vehicle(new VehicleData("B", new Point2D.Double(200.0, 161.2)), reading));
        
        vehicleA.start();
        vehicleB.start();
    }
    
}
