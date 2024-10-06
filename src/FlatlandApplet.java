import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FlatlandApplet extends JApplet {

    JFrame flatlandFrame = new JFrame();

    public void init() {
        // Create our single instance of a Flatland
        Flatland flatlandInstance = new Flatland();

        // Set up the main Flatland window (frame)
        flatlandFrame = new JFrame("Flatland");

        flatlandFrame.getContentPane().add(flatlandInstance.flatlandPane);

        flatlandFrame.pack();
        flatlandFrame.setVisible(true);
        flatlandFrame.requestFocusInWindow();
        flatlandInstance.flatlandCanvas.requestFocus();
        flatlandFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void paint(Graphics g) {
        g.drawString("The Flatland Project window should have opened up.", 20, 50);
    }
}
