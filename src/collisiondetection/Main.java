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
                            new VehicleData.Coordinates(new Point2D.Double(0.0, 150.0), new Date()), 
                15.0, VehicleData.RoadCondition.DRY_ASPHALT), reading, Vehicle.Direction.EAST));
        vehicleA.setName("Vehicle A");
       
       
        Thread vehicleB = new Thread(new Vehicle(new VehicleData("B", 
                          new VehicleData.Coordinates(new Point2D.Double(310.0, 150.0), new Date()),
                20.0, VehicleData.RoadCondition.DRY_ASPHALT), reading, Vehicle.Direction.WEST));
        vehicleA.setName("Vehicle B");
        
        
        //Thread vehicleC= new Thread(new Vehicle(new VehicleData("C", 
                          //new VehicleData.Coordinates(new Point2D.Double(150, 100), new Date()), 15.00,"Ice"),reading,Vehicle.Direction.NORTH));
        
        //veículo 3
        Thread vehicleC= new Thread(new Vehicle(new VehicleData("C", 
                new VehicleData.Coordinates(new Point2D.Double(150, 100), new Date()), 15.00,VehicleData.RoadCondition.SNOW),reading,Vehicle.Direction.NORTH));
        vehicleC.setName("Vehicle C");    
        
        vehicleA.start();
        vehicleB.start();
        vehicleC.start();
    }
    
}
