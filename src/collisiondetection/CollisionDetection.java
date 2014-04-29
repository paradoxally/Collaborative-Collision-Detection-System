/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collisiondetection;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nino
 */
public class CollisionDetection implements Runnable {
    private final CDReading readingsList;

    public CollisionDetection(CDReading readingsList) {
        this.readingsList = readingsList;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // wait for notifications from the vehicle that owns this CD system (new readings)
                synchronized (Thread.currentThread()) { // the Vehicle class owns the thread object, so we can't use 'this'
                    Thread.currentThread().wait();
                    System.out.println("I was notified: " + Thread.currentThread().getName());
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(CollisionDetection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
