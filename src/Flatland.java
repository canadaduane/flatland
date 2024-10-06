import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Flatland
{
   public JFrame flatlandFrame;
   FlatlandCanvas flatland;
   
   ArrayList objects = new ArrayList();
   
   public Flatland()
   {
      flatland = new FlatlandCanvas();
      //flatland.setDefaultCanvas();
      
      Figure aSquare = new Figure( 0, 0, 4 );
      flatland.origin = aSquare.shape.center;
      flatland.setControl( aSquare );
      //FlatlandObject origin = flatland.addObject( 0, 0 );
      //origin.setLabel( "Origin" );
      flatland.add( aSquare );
      flatland.add( new House( 0, 0 ) );
      //flatland.add( new Tree( -500, 100 ) );
      //flatland.add( new Tree( -800, 200 ) );
      //flatland.add( new Tree( -400, -400 ) );
      flatland.add( new Figure( -168, 25, 5 ) );
      flatland.add( new Figure( -135, 70, 5 ) );
      flatland.add( new Figure( -90, 110, 5 ) );
      flatland.add( new Figure( -30, 150, 5 ) );
      flatland.add( new Figure( -30, -140, 6 ) );
      flatland.add( new Figure( -100, -140, 6 ) );
      flatland.add( new Figure( 300, -20, 2 ) );
      
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
