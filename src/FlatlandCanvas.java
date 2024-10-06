import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.lang.Math;
import java.util.ArrayList;
import javax.swing.*;

class FlatlandCanvas extends JPanel {

    static final int WIDTH = 1200;
    static final int HEIGHT = 800;
    float LIGHT_SPEED = 15;
    boolean[] keys = new boolean[4];
    boolean fog = true;
    boolean useColor = false;
    boolean gravity = false;
    FlatlandObject control;
    FlatlandVisor visor;
    FlatlandPoint origin;
    ArrayList objects;
    Timer timer = new Timer(50, null);

    public FlatlandCanvas() {
        super(true);
        setBackground(Color.WHITE);
        setSize(WIDTH, HEIGHT);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        objects = new ArrayList();

        FlatlandObject.setDefaultCanvas(this);
        FlatlandShape.setDefaultCanvas(this);
        FlatlandPoint.setDefaultCanvas(this);
        FlatlandPanel.setDefaultCanvas(this);

        // Initialize the timer that will automagically update each
        // object and repaint the canvas

        timer.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    moveAllObjects();
                    repaint();
                }
            }
        );
        timer.start();

        // Add a key event handler so that we can funnel the events
        // into whichever object is being controlled by the user
        // (Eg: A. Square)
        addKeyListener(
            new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        //control.shape.pushPoint( 0, 0, -50 );
                        //control.shape.pushPoint( 1, 0, 50 );
                        //control.shape.pushPoint( 2, -50, 0 );
                        System.out.println("space");
                    }
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            keys[0] = true;
                            break;
                        case KeyEvent.VK_LEFT:
                            keys[1] = true;
                            break;
                        case KeyEvent.VK_RIGHT:
                            keys[2] = true;
                            break;
                        case KeyEvent.VK_DOWN:
                            //control.direction += Math.PI;
                            keys[3] = true;
                            break;
                        default:
                        //some other key
                    }
                }

                public void keyReleased(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            keys[0] = false;
                            break;
                        case KeyEvent.VK_LEFT:
                            keys[1] = false;
                            break;
                        case KeyEvent.VK_RIGHT:
                            keys[2] = false;
                            break;
                        case KeyEvent.VK_DOWN:
                            //control.direction -= Math.PI;
                            keys[3] = false;
                            break;
                        default:
                        //some other key
                    }
                }

                public void keyTyped(KeyEvent e) {}
            }
        );

        //final FlatlandCanvas thisCanvas = this;
        addMouseListener(
            new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX() - WIDTH / 2;
                    int y = e.getY() - HEIGHT / 2;
                    double rotatedX, rotatedY;
                    double angle = origin.angle;
                    // "Unrotate" the mouse click so that it's in a useful coordinate
                    // reference frame
                    rotatedX = x * Math.cos(angle) + y * Math.sin(angle);
                    rotatedY = y * Math.cos(angle) - x * Math.sin(angle);

                    // Select the smallest object that the user is clicking on,
                    // and make its center the point of origin
                    FlatlandObject select = selectSmallestObject(
                        origin.x + rotatedX,
                        origin.y - rotatedY
                    );
                    if (select != null) {
                        origin = select.shape.center;
                    }
                }
            }
        );
    }

    public void setControl(FlatlandObject object) {
        control = object;
        Figure figure = (Figure) object;
        figure.eye.active = true;
    }

    public void setVisor(FlatlandVisor fv) {
        visor = fv;
        if (control != null) {
            Figure figure = (Figure) control;
            figure.eye.visor = fv;
        }
    }

    public void setFog(boolean f) {
        fog = f;
    }

    public void moveAllObjects() {
        // Keys control one particular Figure in Flatland
        if (keys[0]) control.push(1.5);
        if (keys[1]) control.spin(Math.PI / 100);
        if (keys[2]) control.spin(-Math.PI / 100);
        if (keys[3]) control.brake(2);

        //if( keys[ 3 ] ) {
        //if( newDir >= Math.PI * 2 ) newDir -= Math.PI * 2;
        //if( newDir < 0 ) newDir += Math.PI * 2;
        //control.push( 1, control.direction );
        //}

        Object[] listOfObjects = (Object[]) objects.toArray();
        FlatlandObject object;
        for (int i = 0; i < listOfObjects.length; i++) {
            object = (FlatlandObject) (listOfObjects[i]);
            object.move();
        }
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);

        g.translate(WIDTH / 2, HEIGHT / 2);

        Object[] listOfObjects = (Object[]) objects.toArray();
        FlatlandObject object;
        for (int i = 0; i < objects.size(); i++) {
            object = (FlatlandObject) (listOfObjects[i]);
            object.paint(g);
        }
    }

    public FlatlandObject add(FlatlandObject object) {
        objects.add(object);
        return object;
    }

    public FlatlandObject selectSmallestObject(double x, double y) {
        Object[] listOfObjects = (Object[]) objects.toArray();
        FlatlandObject object;
        FlatlandObject smallestObject = null;
        double smallestSize = Math.sqrt(WIDTH * WIDTH + HEIGHT * HEIGHT);

        for (int i = 0; i < objects.size(); i++) {
            object = (FlatlandObject) (listOfObjects[i]);
            Rectangle2D.Double rect = object.shape.getBoundingBox();
            double squareRoot = Math.sqrt(
                rect.getWidth() * rect.getWidth() + rect.getHeight() * rect.getHeight()
            );
            if (rect.contains(x, y) && squareRoot < smallestSize) {
                smallestSize = squareRoot;
                smallestObject = object;
            }
        }

        return smallestObject;
    }
}
