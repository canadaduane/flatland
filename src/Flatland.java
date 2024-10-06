import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Flatland
{
   public static JFrame flatlandFrame;
   FlatlandCanvas flatlandCanvas = new FlatlandCanvas();
   
   ArrayList objects = new ArrayList();
   
   public Flatland()
   {
			buildWorld();
   }

		public void buildWorld()
		{
      Figure aSquare = new Figure( 0, 0, 4 );
      flatlandCanvas.origin = aSquare.shape.center;
      flatlandCanvas.setControl( aSquare );
      //FlatlandObject origin = flatland.addObject( 0, 0 );
      //origin.setLabel( "Origin" );
      flatlandCanvas.add( aSquare );
      flatlandCanvas.add( new House( 0, 0 ) );
      //flatland.add( new Tree( -500, 100 ) );
      //flatland.add( new Tree( -800, 200 ) );
      //flatland.add( new Tree( -400, -400 ) );
      flatlandCanvas.add( new Figure( -168, 25, 5 ) );
      flatlandCanvas.add( new Figure( -135, 70, 5 ) );
      flatlandCanvas.add( new Figure( -90, 110, 5 ) );
      flatlandCanvas.add( new Figure( -30, 150, 5 ) );
      flatlandCanvas.add( new Figure( -30, -140, 6 ) );
      flatlandCanvas.add( new Figure( -100, -140, 6 ) );
      flatlandCanvas.add( new Figure( 300, -20, 2 ) );
		}
   
   public static void main( String[] args )
   {
			Flatland flatlandInstance = new Flatland();
      // Set up the main Flatland window (frame)
      flatlandFrame = new JFrame( "Flatland" );
      flatlandFrame.getContentPane().add( flatlandInstance.flatlandCanvas );
      flatlandFrame.pack();
      flatlandFrame.setVisible( true );
      flatlandFrame.requestFocusInWindow();
      flatlandFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

   }
}
