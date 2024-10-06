import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Flatland
{
   //FlatlandObject myObject;
   Figure myFigure;
   public JFrame flatlandFrame;
   FlatlandCanvas flatland;
   
   ArrayList objects = new ArrayList();
   
   public Flatland()
   {
      flatland = new FlatlandCanvas();
      //flatland.addObject( 0, 0, "Origin" );
      Figure triangle = flatland.addFigure( -200, 50, 3, true );
      flatland.setControl( triangle );
      flatland.addFigure( 100, 90, 4, true );
      flatland.addFigure( 0, -140, 5, true );
      flatland.addFigure( 300, -20, 2, true );
      flatland.addFigure( 170, 20, 18, true );
      
      //flatland.requestFocus();
      
      //triangle.push( 2, 0 );
      
      // Set up the main Flatland window (frame)
      flatlandFrame = new JFrame( "Flatland" );
      flatlandFrame.getContentPane().add( flatland );
      flatlandFrame.pack();
      flatlandFrame.setVisible( true );
      flatland.requestFocusInWindow();
      flatlandFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
   }
   
   public static void main( String[] args )
   {
      new Flatland();
   }
}