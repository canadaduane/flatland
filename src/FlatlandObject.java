import java.awt.*;
import java.lang.Math;
import java.util.ArrayList;
import java.awt.geom.Point2D;

class FlatlandObject
{
   static final double friction = 0.1;
   protected Point2D.Double center;
   double width, height;
   double radius;
   double direction, velocity;
   String label;
   static FlatlandObject referenceFrame;
   
   public void init( double x, double y, String lbl )
   {
      label = lbl;
      width = 100;
      height = 20;
      center = new Point2D.Double( x, y );
      if( referenceFrame == null )
         referenceFrame = this;
   }
   
   public FlatlandObject( double x, double y )
   {
      init( x, y, "Flatland Object" );
   }

   public FlatlandObject( double x, double y, String lbl )
   {
      init( x, y, lbl );
   }
   
   public void paint( Graphics g )
   {
      if( inScreen() )
      {
         g.drawRect( (int)(center.x - width/2), (int)(center.y - height/2), (int)width, (int)height );
         g.drawString( label, (int)(center.x - width/2) + 10, (int)(center.y) );
      }
   }
   
   public void move()
   {
      // Reduce velocity by the coefficient of friction
      velocity *= ( 1 - friction );
      
      center.x += velocity * Math.cos( direction );
      center.y -= velocity * Math.sin( direction );
   }
   
   // Push the object with a force directed towards its
   // center of mass, from a given angle
   public void push( double force, double angle )
   {
      angle = ( angle / 180 * Math.PI );
      // Vector addition
      // Cr^2 = Ar^2 + Br^2 + 2ArBr[cos(direction-angle)]
      double Ar = velocity;
      double Ax, Ay;
      double Br = force;
      double Bx, By;
      velocity = Math.sqrt( Ar*Ar + Br*Br + 2*Ar*Br*Math.cos( direction - angle ) );
      Ay = velocity * Math.sin( direction );
      Ax = velocity * Math.cos( direction );
      By = force * Math.sin( angle );
      Bx = force * Math.cos( angle );
      //direction = Math.asin( ( Math.abs( Ay + By ) ) / velocity );
      direction = Math.atan( (Ay + By) / (Ax + Bx) );
      // Flip the X axis if the angle should be in the negative quadrant
      if( Ax + Bx < 0 ) direction += Math.PI;
   }
   
   public boolean inScreen()
   {
      return true;  
   }
   
   public double getX()
   {
      return center.x - referenceFrame.center.x;
   }
   
   public double getY()
   {
      return center.y - referenceFrame.center.y;
   }
   
   public void setVelocity( double vel )
   {
      velocity = vel;
   }
}

class Figure extends FlatlandObject
{
   int numberOfSides;
   boolean regular;
   Point2D.Double[] points;
   
   public void makeShape()
   {
      points = new Point2D.Double[ numberOfSides ];
            
      if( numberOfSides > 1 )
      {
         double angleIncrease = Math.PI * 2 / numberOfSides;
         double x, y;
         for( int i = 0; i < numberOfSides; i++ )
         {
            x = Math.sin( angleIncrease * i ) * radius;
            y = -Math.cos( angleIncrease * i ) * radius;
            points[ i ] = new Point2D.Double( x, y );
         }
      }
      else
      {
         // Can't initialize sides
         System.out.println( "Figure::initPoints(): Can't initialize " + numberOfSides + " sides." );
      }
   }
   
   public Figure( double x, double y, int sides, boolean isRegular )
   {
      super( x, y );
      regular = isRegular;
      
      radius = 12;
      numberOfSides = sides;
      makeShape();
   }
   
   public void paint( Graphics g )
   {
      //System.out.println( "drawing " + numberOfSides + "-sided figure." );
      double angleIncrease = Math.PI * 2 / numberOfSides;
      int x1, y1, x2, y2;
      int j = 1;
      for( int i = 0; i < numberOfSides; i++ )
      {
         x1 = (int)(points[ i ]).getX();
         y1 = (int)(points[ i ]).getY();
         x2 = (int)(points[ j ]).getX();
         y2 = (int)(points[ j ]).getY();
         //System.out.println( "x1: " + x1 + " y1: " + y1 + " x2: " + x2 + " y2: " + y2 );
         g.drawLine( (int)getX() + x1, (int)getY() + y1, (int)getX() + x2, (int)getY() + y2 );
         j++;
         if( j >= numberOfSides ) j = 0;
      }
   }
}