import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

class FlatlandCanvas extends Canvas
{
   static final int WIDTH = 640;
   static final int HEIGHT = 480;
   boolean[] keys = new boolean[ 4 ];
   static FlatlandObject control;
   ArrayList objects;
   Timer timer = new Timer( 50, null );
   
   public FlatlandCanvas()
   {
      super();
      setBackground( Color.WHITE );
      setSize( WIDTH, HEIGHT );
      objects = new ArrayList();
      
      // Initialize the timer that will automagically update each
      // object and repaint the canvas
      timer.addActionListener( new ActionListener() {
         public void actionPerformed( ActionEvent event ) {
            moveAllObjects();
            repaint();
         }
      } );
      timer.start();
      
      // Add a key event handler so that we can funnel the events
      // into whichever object is being controlled by the user
      // (Eg: A. Square)
      addKeyListener( new KeyListener() {
         public void keyPressed( KeyEvent e )
         {
            switch( e.getKeyCode() )
            {
               case KeyEvent.VK_UP:
                  keys[ 0 ] = true;
                  break;
               case KeyEvent.VK_LEFT:
                  keys[ 1 ] = true;
                  break;
               case KeyEvent.VK_RIGHT:
                  keys[ 2 ] = true;
                  break;
               case KeyEvent.VK_DOWN:
                  keys[ 3 ] = true;
                  break;
               default:
                  //some other key
            }
         }
         public void keyReleased( KeyEvent e )
         {
            switch( e.getKeyCode() )
            {
               case KeyEvent.VK_UP:
                  keys[ 0 ] = false;
                  break;
               case KeyEvent.VK_LEFT:
                  keys[ 1 ] = false;
                  break;
               case KeyEvent.VK_RIGHT:
                  keys[ 2 ] = false;
                  break;
               case KeyEvent.VK_DOWN:
                  keys[ 3 ] = false;
                  break;
               default:
                  //some other key
            }            
         }
         public void keyTyped( KeyEvent e )
         {
         }
      } );
   }
   
   public static void setControl( FlatlandObject flObj )
   {
      control = flObj;
   }
   
   public void moveAllObjects()
   {
      // Keys control one particular Figure in Flatland
      if( keys[ 0 ] ) control.push( 3, 90 );
      if( keys[ 1 ] ) control.push( 3, 180 );
      if( keys[ 2 ] ) control.push( 3, 0 );
      if( keys[ 3 ] ) control.push( 3, 270 );
   
      Object[] listOfObjects = (Object[])objects.toArray();
      FlatlandObject flObj;
      for( int i = 0; i < objects.size(); i++ )
      {
         flObj = (FlatlandObject)(listOfObjects[ i ]);
         flObj.move();
      }
   }
   
   public void paintAllObjects( Graphics g )
   {
      Object[] listOfObjects = (Object[])objects.toArray();
      FlatlandObject flObj;
      for( int i = 0; i < objects.size(); i++ )
      {
         flObj = (FlatlandObject)(listOfObjects[ i ]);
         flObj.paint( g );
      }
   }
   
   public void paint( Graphics g )
   {
      g.translate( WIDTH / 2, HEIGHT / 2 );      
      //System.out.println( "" + objects.size() + " objects to draw." );
      paintAllObjects( g );      
   }
   
   public FlatlandObject addObject( double x, double y )
   {
      FlatlandObject obj = new FlatlandObject( x, y );
      objects.add( obj );
      
      return obj;
   }
   
   public FlatlandObject addObject( double x, double y, String lbl )
   {
      FlatlandObject obj = new FlatlandObject( x, y, lbl );
      objects.add( obj );
      
      return obj;
   }
   
   public Figure addFigure( double x, double y, int sides, boolean regular )
   {
      Figure fig = new Figure( x, y, sides, regular );
      objects.add( fig );
      
      return fig;
   }
}
