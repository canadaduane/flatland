import java.awt.*;
import java.awt.event.*;
import java.lang.Math;
import javax.swing.*;

class Point {

    public int x, y;

    public Point(int tx, int ty) {
        x = tx;
        y = ty;
    }

    public void set(int tx, int ty) {
        x = tx;
        y = ty;
    }
}

class PointList {

    final int MAX = 500;
    public Point[] list = new Point[MAX];
    public int index = 0, iterate = 0;

    public void add(int x, int y) {
        list[index++] = new Point(x, y);
    }

    public void reset() {
        iterate = 0;
    }

    public Point getNext() {
        if (index > 0) {
            if (iterate < index) {
                return list[iterate++];
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean hasNext() {
        return (iterate < index);
    }
}

class DrawCanvas extends Canvas {

    final int WIDTH = 500, HEIGHT = 500;
    PointList pointList = new PointList();
    boolean leftButton = false;
    boolean modifyMode = false;
    int mouseX = 0, mouseY = 0;
    int selectedPoint = -1;

    public DrawCanvas() {
        super();
        setSize(WIDTH, HEIGHT);
        setBackground(Color.WHITE);
        addMouseListener(new PointMouseListener());
        addMouseMotionListener(new PointMouseMotionListener());
        addKeyListener(new PointKeyListener());
    }

    public void paint(Graphics g) {
        g.translate(WIDTH / 2, HEIGHT / 2);
        g.drawRect(-5, 0, 10, 0);
        g.drawRect(0, -5, 0, 10);
        //g.drawRect( 20, 20, 50, 100 );

        for (int i = 0; i < 5; i++) {
            int x = (int) (Math.cos(((Math.PI * 2) / 5.0) * i - Math.PI / 2) * (WIDTH / 2 - 40));
            int y = (int) (Math.sin(((Math.PI * 2) / 5.0) * i - Math.PI / 2) * (WIDTH / 2 - 40));
            g.drawLine(x, y, x, y);
        }

        pointList.reset();
        Point point = null, firstPoint = null;
        while (pointList.hasNext()) {
            if (firstPoint == null) {
                point = pointList.getNext();
                if (!pointList.hasNext()) {
                    break;
                }
            }
            firstPoint = point;
            point = pointList.getNext();

            g.setColor(Color.BLACK);
            g.drawLine(firstPoint.x, firstPoint.y, point.x, point.y);
            if (modifyMode) {
                // Draw little circles at each vertex if we're in "modify mode"
                // so that the user can click and drag points on the polygon
                g.setColor(Color.RED);
                g.drawOval(firstPoint.x - 5, firstPoint.y - 5, 10, 10);
                g.drawOval(point.x - 5, point.y - 5, 10, 10);
            }
        }

        // Draw a red line from the last point to the current mouse position
        // if a) there *is* exists a last point,
        //    b) the left mouse button is currently being pressed, and
        //    c) the user is not in modify mode (after pressing the right button)
        if (point != null && leftButton && !modifyMode) {
            g.setColor(Color.RED);
            g.drawLine(point.x, point.y, mouseX - WIDTH / 2, mouseY - HEIGHT / 2);
        }
    }

    public void selectPoint(int x, int y) {
        pointList.reset();
        Point point;
        while (pointList.hasNext()) {
            point = pointList.getNext();
            if (x > point.x - 5 && x < point.x + 5 && y > point.y - 5 && y < point.y + 5) {
                selectedPoint = pointList.iterate - 1;
                return;
            }
        }
        selectedPoint = -1;
    }

    public String toString() {
        String retString = "{\n";
        pointList.reset();
        Point point;
        while (pointList.hasNext()) {
            point = pointList.getNext();
            retString += "\t{ " + point.x + ", " + -point.y + "},\n";
        }
        retString += "}\n";

        return retString;
    }

    private class PointKeyListener extends KeyAdapter {

        public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_M) {
                modifyMode = !modifyMode;
                repaint();
            }
        }
    }

    private class PointMouseListener extends MouseAdapter {

        public void mousePressed(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                leftButton = true;
                mouseX = event.getX();
                mouseY = event.getY();
                if (modifyMode) {
                    selectPoint(mouseX - WIDTH / 2, mouseY - HEIGHT / 2);
                }
                repaint();
            }
        }

        public void mouseReleased(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                leftButton = false;
                if (modifyMode) {
                    selectedPoint = -1;
                } else {
                    pointList.add(event.getX() - WIDTH / 2, event.getY() - HEIGHT / 2);
                }
            } else {
                modifyMode = true;
            }
            repaint();
        }
    }

    private class PointMouseMotionListener extends MouseMotionAdapter {

        public void mouseDragged(MouseEvent event) {
            if (leftButton) {
                mouseX = event.getX();
                mouseY = event.getY();
                if (modifyMode) {
                    if (selectedPoint >= 0) {
                        //System.out.println( "selectedPoint: " + selectedPoint );
                        pointList.list[selectedPoint].set(mouseX - WIDTH / 2, mouseY - HEIGHT / 2);
                    }
                }
                repaint();
            }
        }
    }
}

public class DrawFlatlandShape extends JFrame {

    public DrawFlatlandShape() {
        super("Draw a Flatland Shape");
        BoxLayout box = new BoxLayout(getContentPane(), BoxLayout.X_AXIS);
        getContentPane().setLayout(box);

        final DrawCanvas canvas = new DrawCanvas();
        getContentPane().add(canvas);

        JButton button = new JButton("Save Shape");
        button.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println(canvas.toString());
                }
            }
        );
        getContentPane().add(button);

        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        new DrawFlatlandShape();
    }
}
