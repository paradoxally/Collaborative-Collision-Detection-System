package collisiondetection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer; 


public class App extends JFrame implements ActionListener {

  private static final long serialVersionUID = 1L;

  public static void main(String[] a) {
    App app = new App();
    app.createControls();
  }

  public App() {
    setResizable(false);
    setTitle("Collision Test - v1");
    setSize(800, 600);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
  }

  private void createControls() {
    JPanel upper = new JPanel();

    JButton fire = new JButton("Start");
    fire.addActionListener(this);
    upper.add(fire);

    this.add(upper, BorderLayout.NORTH);
    setVisible(true);
  }

  /*
   * When the "Attack" button is pressed the time will start and the
   * circle will start painting.
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    AttackFX attackfx = new AttackFX();

    this.add(attackfx, BorderLayout.CENTER);
    this.validate();
  }

  class AttackFX extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    
    Thread vehicleA= new Thread();
    
    VehicleData v= new VehicleData(TOOL_TIP_TEXT_KEY, null, WIDTH, null);
	
    Point2D.Double c1= v.getCoordinatesValues();
    private int xCoor = (int)c1.getX();
    private int yCoor = (int)c1.getY();
	
   // private Coordinates coor = new VehicleData.Coordinates(new Point2D.Double(0, 300), new Date());
   
    
    private int time = 1;
    private final Timer t;
        private CDReading reading;

    public AttackFX() {
           
        t = new Timer(500, this);
        t.start();
    }
    
    

    @Override
    public void paintComponent ( Graphics graphics ) {
        super.paintComponent ( graphics );
        Graphics2D graphicsA = (Graphics2D) graphics;
        
        graphicsA.setColor ( Color.RED );
        graphicsA.fillOval(xCoor, yCoor,8,8);
    }

    /*
     * The timer will fire an action every 10 milliseconds, moving
     * our circle by 1 each time.
     * 
     * I unfortunately had to hard code a value that the circle should stop at
     * but I am sure you can find a way around this.
     */

        @Override
        public void actionPerformed(ActionEvent ae) {
        //Coordinates nextC = v.nextPoint(time);
        Point2D.Double c1= v.getCoordinatesValues();
        xCoor = (int)c1.getX(); 
        yCoor = (int)c1.getY();
        //System.out.println(nextC.toString());
        
        if (c1.x >1000 || c1.y > 1000) {
            t.stop();
            setVisible(false);
        }

        xCoor++;
        time++;
        repaint();        
        }
    
  }
}