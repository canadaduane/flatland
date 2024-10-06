import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.lang.Math;

class FlatlandCanvas extends Canvas
{
   static final int WIDTH = 640;
   static final int HEIGHT = 480;
   boolean[] keys = new boolean[ 4 ];
   FlatlandObject control;
   FlatlandPoint origin;
   ArrayList objects;
   Timer timer = new Timer( 50, null );
   
   public FlatlandCanvas()
   {
      super();
      setBackground( Color.WHITE );
      setSize( WIDTH, HEIGHT );
      objects = new ArrayList();

      FlatlandObject.setDefaultCanvas( this );
      FlatlandShape.setDefaultCanvas( this );
      FlatlandPoint.setDefaultCanvas( this );
      
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
            if( e.getKeyCode() == KeyEvent.VK_SPACE ) {
               //control.shape.pushPoint( 0, 0, -50 );
               //control.shape.pushPoint( 1, 0, 50 );
               //control.shape.pushPoint( 2, -50, 0 );
               System.out.println( "space" );
            }
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
   
   public void setControl( FlatlandObject object )
   {
      control = object;
      Figure figure = (Figure)object;
      figure.eye.active = true;
   }
   
   public void moveAllObjects()
   {
      // Keys control one particular Figure in Flatland
      /*
      if( keys[ 0 ] ) control.push( 2, Math.PI / 2 );
      if( keys[ 1 ] ) control.push( 2, Math.PI );
      if( keys[ 2 ] ) control.push( 2, 0 );
      if( keys[ 3 ] ) control.push( 2, Math.PI * 3 / 2 );
      */
      if( keys[ 0 ] ) control.push( 2, control.direction );
      if( keys[ 1 ] ) control.spin( Math.PI/50 );
      if( keys[ 2 ] ) control.spin( -Math.PI/50 );
      if( keys[ 3 ] ) control.brake( 2 );
   
      Object[] listOfObjects = (Object[])objects.toArray();
      FlatlandObject object;
      for( int i = 0; i < objects.size(); i++ )
      {
         object = (FlatlandObject)(listOfObjects[ i ]);
         object.move();
      }
   }
   
   public void paint( Graphics g )
   {
      g.translate( WIDTH / 2, HEIGHT / 2 );      

      Object[] listOfObjects = (Object[])objects.toArray();
      FlatlandObject object;
      for( int i = 0; i < objects.size(); i++ )
      {
         object = (FlatlandObject)(listOfObjects[ i ]);
         object.paint( g );
      }
   }
   
   public FlatlandObject add( FlatlandObject object )
   {
      objects.add( object );
      return object;
   }
}
