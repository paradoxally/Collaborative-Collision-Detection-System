package collisiondetection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer; 


public class App extends JFrame implements ActionListener {

 private static final long serialVersionUID = 1L;   
  
  private JButton fire;
 
  Random ran = new Random(); //Generates random position to an obstacle
  int xran = ran.nextInt(100) + 40;
  int yran= ran.nextInt(100)+ 45;
  
  public static void main(String[] a) {
    App app = new App();
    app.createControls();
  }

  public App() {
    setResizable(false);
    setTitle("Collision Test - v1");
    setSize(320, 380);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout()); 
  }

  private void createControls() {
    JPanel upper = new JPanel(); 
    fire = new JButton("Start");
    fire.addActionListener(this);
    upper.add(fire); 

    this.add(upper, BorderLayout.NORTH);
    setVisible(true); 
  
  }

  /*
   * When the "Start" button is pressed the time will start and the
   * vehicle simulation will initiate.
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    this.fire.setEnabled(false);
    VehicleDraw vehicledraw = new VehicleDraw();
    this.add(vehicledraw, BorderLayout.CENTER);
    this.validate();
  }

  class VehicleDraw extends JPanel implements ActionListener {
    
    private static final long serialVersionUID = 1L;
    
    //Thread vehicleA= new Thread();
    
    //VehicleData v= new VehicleData(TOOL_TIP_TEXT_KEY, null, WIDTH, null); 
  
    CDReading reading = new CDReading();
    
    VehicleData v = new VehicleData("A", 
                            new VehicleData.Coordinates(new Point2D.Double(0.0, 150.0), new Date()), 
                15.0,  VehicleData.Direction.EAST, VehicleData.RoadCondition.DRY_ASPHALT); 
    
    VehicleData v2 = new VehicleData("B", 
                          new VehicleData.Coordinates(new Point2D.Double(310.0, 150.0), new Date()),
                20.0, VehicleData.Direction.WEST, VehicleData.RoadCondition.DRY_ASPHALT); 
    
   // VehicleData v3= new VehicleData("C", 
   //             new VehicleData.Coordinates(new Point2D.Double(150, 30), new Date()), 15.00,VehicleData.RoadCondition.DRY_ASPHALT);
    
    // create vehicle threads
        Thread vehicleA = new Thread(new Vehicle(v, reading));
        
        Thread vehicleB = new Thread(new Vehicle(v2, reading));
   
    //    Thread vehicleC = new Thread(new Vehicle(v3, reading, Vehicle.Direction.NORTH));
        
       
    //Get coordinates of each vehicle
    Point2D.Double c1= v.getCoordinatesValues(); 
        
    private int xCoor = (int)c1.getX();
    private int yCoor = (int)c1.getY();

    Point2D.Double c2= v2.getCoordinatesValues();
    
    private int xCoor1 = (int)c2.getX();
    private int yCoor1 = (int)c2.getY();
    
   // Point2D.Double c3= v3.getCoordinatesValues();
   
  // private int xCoor2 = (int)c3.getX();
  //  private int yCoor2 = (int)c3.getY();
    
    
   // private Coordinates coor = new VehicleData.Coordinates(new Point2D.Double(0, 300), new Date());
   
    
    // private int time = 1;
    private final Timer t;
       

    public VehicleDraw() {
        t = new Timer(500, this);
        t.start(); 
        
        vehicleA.start(); 
        vehicleB.start();
     //   vehicleC.start();
    }
    
    
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent ( graphics );
        Graphics2D graphicsA = (Graphics2D) graphics;   
        Line2D lin = new Line2D.Float(0, 8, 320, 8);
       
        graphicsA.setColor(Color.BLACK); 
        graphicsA.draw(lin);
        
        graphicsA.setColor ( Color.RED ); //First vehicle identification
        graphicsA.fillOval(xCoor, yCoor,8,8); 
        
        graphicsA.setColor ( Color.BLUE ); //Second vehicle identification
        graphicsA.fillOval(xCoor1, yCoor1,8,8);  
        
        graphicsA.setColor(Color.ORANGE); //Random obstacle
        graphicsA.fillRect(xran, yran, 5, 5);
        
        //graphicsA.setColor ( Color.CYAN ); //Third vehicle identification
        //graphicsA.fillOval(xCoor2, yCoor2,8,8); 
        
        }

    /*
     * The timer will fire an action every TIME_SPAN, moving
     * our vehicles by the appropriate speed each time.
     * 
     */

        @Override
        public void actionPerformed(ActionEvent ae) {
        //Coordinates nextC = v.nextPoint(time);
       Point2D.Double c1= v.getCoordinatesValues();
       Point2D.Double c2= v2.getCoordinatesValues();
     //  Point2D.Double c3= v3.getCoordinatesValues();
       
        xCoor = (int)c1.getX(); 
        yCoor = (int)c1.getY();  
        
        xCoor1 = (int)c2.getX(); 
        yCoor1 = (int)c2.getY();  
        
     //   xCoor2 = (int)c3.getX(); 
     //   yCoor2 = (int)c3.getY(); 
        
        
        //System.out.println(nextC.toString());
        
        if (c1.x >320 || c1.y > 320 && c2.x > 320 || c2.y > 320 ) { //Check if vehicles cross the limited zone
            t.stop();
            setVisible(false);
        }

        
        xCoor++;
        yCoor++;
        xCoor1++;
        yCoor1++; 
       // xCoor2++;
       // yCoor2++; 
        repaint();        
        }
    
  }
}