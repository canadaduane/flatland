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
      //FlatlandObject origin = flatland.addObject( 0, 0 );
      //origin.setLabel( "Origin" );
      Figure aSquare = flatland.addFigure( 0, 0, 4 );
      flatland.setControl( aSquare );
      flatland.addHouse( 40, 0 );
      flatland.addFigure( -168, 25, 5 );
      flatland.addFigure( -135, 70, 5 );
      flatland.addFigure( -90, 110, 5 );
      flatland.addFigure( -30, 150, 5 );
      flatland.addFigure( -30, -140, 6 );
      flatland.addFigure( -100, -140, 6 );
      flatland.addFigure( 300, -20, 2 );
//      flatland.addFigure( 170, 20, 18 );
      
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