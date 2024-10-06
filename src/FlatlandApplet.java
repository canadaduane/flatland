import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FlatlandApplet extends JApplet {

    public void init() {
        SwingUtilities.invokeLater(() -> {
            JFrame flatlandFrame = createAndShowGUI();
            flatlandFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        });
    }

    public JFrame createAndShowGUI() {
        // Create our single instance of a Flatland
        Flatland flatlandInstance = new Flatland();

        // Set up the main Flatland window (frame)
        JFrame flatlandFrame = new JFrame("Flatland");

        flatlandFrame.getContentPane().add(flatlandInstance.flatlandPane);

        flatlandFrame.pack();
        flatlandFrame.setVisible(true);
        flatlandFrame.requestFocusInWindow();
        flatlandInstance.flatlandCanvas.requestFocus();

        return flatlandFrame;
    }

    public void paint(Graphics g) {
        g.drawString("The Flatland Project window should have opened up.", 20, 50);
    }

    // Add a main method to allow running as a standalone application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatlandApplet applet = new FlatlandApplet();
            JFrame flatlandFrame = applet.createAndShowGUI();
            flatlandFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}
