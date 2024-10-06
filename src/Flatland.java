import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Flatland {

    public static JFrame flatlandFrame;

    JPanel flatlandPane = new JPanel();
    FlatlandCanvas flatlandCanvas = new FlatlandCanvas();
    FlatlandVisor flatlandVisor = new FlatlandVisor();
    FlatlandPanel flatlandPanel = new FlatlandPanel();

    ArrayList objects = new ArrayList();

    public Flatland() {
        buildWorld();
        buildPane();
    }

    public void buildWorld() {
        Figure aSquare = new Figure(0, 0, 4);
        flatlandCanvas.origin = aSquare.shape.center;
        flatlandCanvas.setControl(aSquare);
        flatlandCanvas.setVisor(flatlandVisor);
        //FlatlandObject origin = flatland.addObject( 0, 0 );
        //origin.setLabel( "Origin" );
        flatlandCanvas.add(aSquare);
        flatlandCanvas.add(new House(0, 0));
        flatlandCanvas.add(new House(400, -300));
        flatlandCanvas.add(new Tree(-500, 100));
        flatlandCanvas.add(new Tree(-800, 200));
        flatlandCanvas.add(new Tree(-400, -400));
        flatlandCanvas.add(new Figure(-168, 25, 5));
        flatlandCanvas.add(new Figure(-135, 70, 5));
        flatlandCanvas.add(new Figure(-90, 110, 5));
        flatlandCanvas.add(new Figure(-30, 150, 5));
        flatlandCanvas.add(new Figure(-30, -140, 6));
        flatlandCanvas.add(new Figure(-100, -140, 6));
        flatlandCanvas.add(new Mountain(205, 500));
        flatlandCanvas.add(new Figure(160, 30, 2));
    }

    public void buildPane() {
        //Container pane = flatlandFrame.getContentPane();
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        flatlandPane.setLayout(gb);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gb.setConstraints(flatlandPanel, gbc);
        flatlandPane.add(flatlandPanel);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gb.setConstraints(flatlandCanvas, gbc);
        flatlandPane.add(flatlandCanvas);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gb.setConstraints(flatlandVisor, gbc);
        flatlandPane.add(flatlandVisor);
    }

    public static void main(String[] args) {
        // Create our single instance of a Flatland
        Flatland flatlandInstance = new Flatland();

        // Set up the main Flatland window (frame)
        flatlandFrame = new JFrame("Flatland");

        flatlandFrame.getContentPane().add(flatlandInstance.flatlandPane);

        flatlandFrame.pack();
        flatlandFrame.setVisible(true);
        flatlandFrame.requestFocusInWindow();
        flatlandInstance.flatlandCanvas.requestFocus();
        flatlandFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
