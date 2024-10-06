import java.awt.*;
import java.lang.Math;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.awt.geom.Point2D;

class Corner extends Point2D.Double
{
   static private Point2D.Double referenceFrame;

   final static double FRICTION_COEFFICIENT = 0.1;
   
   public double velocity, direction;
   
   public Corner( double x, double y )
   {
      super( x, y );
   }
   
   public Corner( Point2D.Double p )
   {
      super( p.x, p.y );
   }

   // Push the corner (point) towards a given angle in radians
   // with a particular force
   public void push( double force, double angle )
   {
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
   
   public void move()
   {
      velocity *= ( 1 - FRICTION_COEFFICIENT );
      
      x += velocity * Math.cos( direction );
      y += velocity * Math.sin( direction );
   }
   
   public void setVelocity( double vel )
   {
      velocity = vel;
   }
   
   public double getRelativeX()
   {
      return x - referenceFrame.x;
   }
   
   public double getRelativeY()
   {
      return y - referenceFrame.y;
   }
   
   public static boolean hasReferenceFrame()
   {
      return referenceFrame != null;
   }
   
   public static void setReferenceFrame( Point2D.Double rf )
   {
      referenceFrame = rf;
   }
   
   public static Point2D.Double getReferenceFrame()
   {
      return referenceFrame;
   }
}

class Segment
{
   public double length, tension;
   
   public Segment( double l, double t )
   {
      length = l;
      tension = t;
   }
}

class FlatlandObject
{
   final static double DEFAULT_TENSION = 0.9;
   final static double DEFAULT_RADIUS = 10;
   final static double SPRING_TOLERANCE = 0.001;
   final static int DEFAULT_NUMBER_OF_SIDES = 3;
   protected String label;
   protected int sides;
   protected Corner[] points;
   protected Segment[] lines;
   protected Point2D.Double center;
   
   public void setSides( int s )
   {
      sides = s;
      points = new Corner[ sides ];
      lines = new Segment[ sides ];
   }
   
   private void init( double x, double y, int s )
   {
      setSides( s );
      center = new Point2D.Double( x, y );
      if( !Corner.hasReferenceFrame() )
         Corner.setReferenceFrame( center );
   }

   public FlatlandObject( double x, double y )
   {
      init( x, y, DEFAULT_NUMBER_OF_SIDES );
      makeRegular( DEFAULT_RADIUS );
   }
   
   public FlatlandObject( double x, double y, int numberOfSides )
   {
      init( x, y, numberOfSides );
      makeRegular( DEFAULT_RADIUS );
   }

   public FlatlandObject( double x, double y, int numberOfSides, double rad )
   {
      init( x, y, numberOfSides );
      makeRegular( rad );
   }
   
   public FlatlandObject( double x, double y, int numberOfSides, boolean regular )
   {
      init( x, y, numberOfSides );
      if( regular ) makeRegular( DEFAULT_RADIUS );
   }

   public void setLabel( String lbl )
   {
      label = lbl;
   }
   
   public void hideLabel()
   {
      label = null;
   }

   public void connectCorners()
   {
      int j = 1;
      Corner c1, c2;
      for( int i = 0; i < sides; i++ )
      {
         c1 = points[ i ];
         c2 = points[ j ];
         lines[ i ] = new Segment( c1.distance( c2 ), DEFAULT_TENSION );
         j++;
         if( j >= sides )
            j = 0;
      }
   }
   
   public void makeIrregular( Point2D.Double[] list )
   {
      setSides( list.length );
      for( int i = 0; i < list.length; i++ )
      {
         points[ i ] = new Corner( list[ i ] );
         points[ i ].x += center.x;
         points[ i ].y += center.y;
      }
      
      connectCorners();
   }
   
   public void makeRegular( double radius )
   {
      if( sides > 1 )
      {
         double angleIncrease = Math.PI * 2 / sides;
         double x, y, length;
         // Calculate length of each line segment
         length = Math.sin( angleIncrease / 2 ) * 2 * radius;
         for( int i = 0; i < sides; i++ )
         {
            y = Math.sin( angleIncrease * i ) * radius;
            x = Math.cos( angleIncrease * i ) * radius;
            points[ i ] = new Corner( center.x + x, center.y + y );
            lines[ i ] = new Segment( length, DEFAULT_TENSION );
         }
      }
      else
      {
         // Can't initialize sides
         System.out.println( "FlatlandShape::makeRegular(): Can't initialize " + sides + " sides." );
      }
   }
   
   public boolean inScreen()
   {
      return true;  
   }

   public Point2D.Double intersect( double Px1, double Py1, double Px2, double Py2,
                                    double Qx1, double Qy1, double Qx2, double Qy2 )
   {
      double Qm, Pm, Qb, Pb;
      double Qrise, Qrun, Prise, Prun;
      Qrise = Qy2 - Qy1;
      Prise = Py2 - Py1;
      Qrun = Qx2 - Qx1;
      Prun = Px2 - Px1;
      
      Qm = Qrise / Qrun;      // slope of segment Q
      Pm = Prise / Prun;      // slope of segment P
      
      Qb = Qy1 - Qm * Qx1;    // y-intercept of segment Q
      Pb = Py1 - Pm * Px1;    // y-intercept of segment P
      
      // Calculate point of intersection
      double y = ( Qm * ( Qb - Pb ) ) / ( Pm - Qm );
      double x = ( y - Pb ) / Pm;
      
      if( x >= Px1 && x < Px2 && x >= Qx1 && x <= Qx2 &&
          y >= Py1 && y < Py2 && y >= Qy1 && y <= Qy2 )
      {
         return new Point2D.Double( x, y );
      } else {
         return null;
      }
   }

   public void paint( Graphics g )
   {
      if( inScreen() )
      {
         if( label != null ) {
            g.drawString( label, (int)(center.x - 100) + 10, (int)(center.y) );
         }

         double angleIncrease = Math.PI * 2 / sides;
         int x1, y1, x2, y2;
         int cx = (int)getRelativeX();
         int cy = -(int)getRelativeY();
         int j = 1;
         for( int i = 0; i < sides; i++ )
         {
            x1 = (int)(points[ i ]).getRelativeX();
            y1 = -(int)(points[ i ]).getRelativeY();
            x2 = (int)(points[ j ]).getRelativeX();
            y2 = -(int)(points[ j ]).getRelativeY();
   
            g.drawLine( x1, y1, x2, y2 );
            // g.drawLine( cx, cy, cx, cy );
            j++;
            if( j >= sides ) j = 0;
         }
      }
   }
   
   public void move()
   {
      for( int i = 0; i < sides; i++ )
      {
         points[ i ].move();
      }
            
      spring();
      
      recalculateCenter();
   }
   
   public void push( double force, double angle )
   {
      for( int i = 0; i < sides; i++ )
      {
         points[ i ].push( force, angle );
      }
      //recalculateCenter();
   }
   
   // Using the tension ratios in each segment of the shape,
   // expand and contract as necessary
   public void spring()
   {
      double angle, length;
      double xlength, ylength;
      double strength;
      double extension, xextension, yextension;
      int point1 = 0, point2 = 1;
      DecimalFormat fmt = new DecimalFormat( "0.0000" );
      for( int i = 0; i < sides; i++ )
      {
         // Determine the current angle and length of the line segment
         xlength = points[ point2 ].x - points[ point1 ].x;
         ylength = points[ point2 ].y - points[ point1 ].y;
         length = Math.sqrt( xlength * xlength + ylength * ylength );
         
         extension = lines[ i ].length - length;
         xextension = ( xlength * extension ) / ( length * 2 );
         yextension = ( ylength * extension ) / ( length * 2 );

         if( xextension > SPRING_TOLERANCE || yextension > SPRING_TOLERANCE )
         {         
            points[ point1 ].x -= xextension;
            points[ point1 ].y -= yextension;
            points[ point2 ].x += xextension;
            points[ point2 ].y += yextension;
         }
         
         //angle = Math.atan( ylength / xlength );
         
         // Increment point1 and point2 so we can work on the next segment
         point1++;
         point2++;
         if( point2 >= sides ) point2 = 0;
      }
   }
/*   
   public void pushPoint( int p, double x, double y )
   {
      points[ p ].x += x;
      points[ p ].y += y;
   }
*/   

   public void moveCenter( double x, double y )
   {
      for( int i = 0; i < sides; i++ )
      {
         points[ i ].x += ( x - center.x );
         points[ i ].y += ( y - center.y );
      }
      
      center.x = x;
      center.y = y;
   }
   
   public Point2D.Double recalculateCenter()
   {
      // Calculate the average x and y position, or "center" of
      // this FlatlandObject
      double x = 0, y = 0;
      for( int i = 0; i < sides; i++ )
      {
         x += points[ i ].x;
         y += points[ i ].y;
      }
      
      x /= sides;
      y /= sides;
      
      
      center.x = x;
      center.y = y;

      return center;
   }
   
   public Point2D.Double getRelativeCenter()
   {
      Point2D.Double rf = Corner.getReferenceFrame();
      double x = center.x - rf.x;
      double y = center.y - rf.y;
      return new Point2D.Double( x, y );
   }
   
   public double getRelativeX()
   {
      return center.x - Corner.getReferenceFrame().x;
   }
   
   public double getRelativeY()
   {
      return center.y - Corner.getReferenceFrame().y;
   }
   
   public void setVelocity( double vel )
   {
      for( int i = 0; i < sides; i++ )
      {
         points[ i ].setVelocity( vel );
      }
   }
}

class Eye extends FlatlandObject
{
   public Eye( double x, double y )
   {
      // Regular triangle with radius of 3
      super( x, y, 5, 3.0 );
   }

   public void paint( Graphics g )
   {
      super.paint( g );
      
   }   
}

class Figure extends FlatlandObject
{
   Eye myEye;
   
   public Figure( double x, double y, int sides )
   {
      super( x, y, sides );
      myEye = new Eye( x, y );
   }

   public void paint( Graphics g )
   {
      super.paint( g );
      myEye.paint( g );
   }

   public void move()
   {
      super.move();
      myEye.moveCenter( points[ 0 ].x, points[ 0 ].y );
   }   
}

class Tree extends FlatlandObject
{
   static Point2D.Double[] shape = {
   	new Point2D.Double( 3, -153),
    	new Point2D.Double( 61, -159),
    	new Point2D.Double( 86, -170),
    	new Point2D.Double( 93, -178),
    	new Point2D.Double( 87, -159),
    	new Point2D.Double( 51, -139),
    	new Point2D.Double( 29, -105),
    	new Point2D.Double( 25, -47),
    	new Point2D.Double( 18, 23),
    	new Point2D.Double( 18, 54),
    	new Point2D.Double( 21, 80),
    	new Point2D.Double( 43, 94),
    	new Point2D.Double( 67, 100),
    	new Point2D.Double( 75, 92),
    	new Point2D.Double( 76, 82),
    	new Point2D.Double( 68, 78),
    	new Point2D.Double( 65, 71),
    	new Point2D.Double( 68, 62),
    	new Point2D.Double( 74, 59),
    	new Point2D.Double( 82, 59),
    	new Point2D.Double( 89, 65),
    	new Point2D.Double( 91, 73),
    	new Point2D.Double( 84, 79),
    	new Point2D.Double( 78, 83),
    	new Point2D.Double( 77, 93),
    	new Point2D.Double( 83, 93),
    	new Point2D.Double( 103, 95),
    	new Point2D.Double( 124, 106),
    	new Point2D.Double( 135, 121),
    	new Point2D.Double( 135, 113),
    	new Point2D.Double( 133, 110),
    	new Point2D.Double( 129, 105),
    	new Point2D.Double( 129, 96),
    	new Point2D.Double( 132, 90),
    	new Point2D.Double( 137, 90),
    	new Point2D.Double( 146, 89),
    	new Point2D.Double( 151, 93),
    	new Point2D.Double( 153, 100),
    	new Point2D.Double( 148, 108),
    	new Point2D.Double( 142, 112),
    	new Point2D.Double( 138, 113),
    	new Point2D.Double( 138, 121),
    	new Point2D.Double( 144, 134),
    	new Point2D.Double( 149, 164),
    	new Point2D.Double( 134, 196),
    	new Point2D.Double( 101, 218),
    	new Point2D.Double( 43, 218),
    	new Point2D.Double( -66, 239),
    	new Point2D.Double( -124, 210),
    	new Point2D.Double( -149, 168),
    	new Point2D.Double( -135, 114),
    	new Point2D.Double( -111, 96),
    	new Point2D.Double( -90, 87),
    	new Point2D.Double( -89, 77),
    	new Point2D.Double( -94, 74),
    	new Point2D.Double( -98, 68),
    	new Point2D.Double( -97, 59),
    	new Point2D.Double( -90, 53),
    	new Point2D.Double( -80, 52),
    	new Point2D.Double( -70, 61),
    	new Point2D.Double( -71, 70),
    	new Point2D.Double( -79, 78),
    	new Point2D.Double( -87, 79),
    	new Point2D.Double( -87, 88),
    	new Point2D.Double( -65, 84),
    	new Point2D.Double( -39, 79),
    	new Point2D.Double( -29, 65),
    	new Point2D.Double( -25, 36),
    	new Point2D.Double( -25, 8),
    	new Point2D.Double( -29, -23),
    	new Point2D.Double( -33, -73),
    	new Point2D.Double( -41, -99),
    	new Point2D.Double( -48, -117),
    	new Point2D.Double( -62, -127),
    	new Point2D.Double( -86, -139),
    	new Point2D.Double( -102, -140),
    	new Point2D.Double( -112, -144),
    	new Point2D.Double( -114, -152),
    	new Point2D.Double( -103, -146),
    	new Point2D.Double( -57, -150),
    	new Point2D.Double( 2, -153)
  };

   public Tree( double x, double y )
   {
      super( x, y );
      makeIrregular( shape );
   }

}

class House extends FlatlandObject
{
   static Point2D.Double[] shape = {
      //new Point2D.Double( -24, -8 ),
      new Point2D.Double( -173, -25 ),
      new Point2D.Double( -199, 64 ),
      new Point2D.Double( 0, 210 ),
      new Point2D.Double( 199, 64 ),
      new Point2D.Double( 123, -169 ),
      new Point2D.Double( -123, -169 ),
      new Point2D.Double( -151, -94 ),
      new Point2D.Double( -146, -91 ),
      new Point2D.Double( -138, -113 ),
      new Point2D.Double( -122, -113 ),
      new Point2D.Double( -121, -119 ),
      new Point2D.Double( -136, -119 ),
      new Point2D.Double( -126, -145 ),
      new Point2D.Double( -119, -163 ),
      new Point2D.Double( -69, -163 ),
    	new Point2D.Double( -69, -118 ),
    	new Point2D.Double( -84, -118 ),
    	new Point2D.Double( -84, -112 ),
    	new Point2D.Double( -49, -112 ),
    	new Point2D.Double( -49, -118 ),
    	new Point2D.Double( -63, -118 ),
    	new Point2D.Double( -63, -163 ),
    	new Point2D.Double( 1, -163 ),
    	new Point2D.Double( 1, -117 ),
    	new Point2D.Double( -11, -117 ),
    	new Point2D.Double( -11, -111 ),
    	new Point2D.Double( 17, -111 ),
    	new Point2D.Double( 17, -117 ),
    	new Point2D.Double( 7, -117 ),
    	new Point2D.Double( 7, -144 ),
    	new Point2D.Double( 7, -163 ),
    	new Point2D.Double( 119, -163 ),
    	new Point2D.Double( 132, -124 ),
    	new Point2D.Double( 45, -72 ),
    	new Point2D.Double( 44, -78 ),
    	new Point2D.Double( 38, -75 ),
    	new Point2D.Double( 48, -53 ),
    	new Point2D.Double( 53, -56 ),
    	new Point2D.Double( 49, -67 ),
    	new Point2D.Double( 134, -118 ),
    	new Point2D.Double( 157, -47 ),
    	new Point2D.Double( 80, -2 ),
    	new Point2D.Double( 76, -11 ),
    	new Point2D.Double( 70, -8 ),
    	new Point2D.Double( 91, 43 ),
    	new Point2D.Double( 96, 40 ),
    	new Point2D.Double( 93, 33 ),
    	new Point2D.Double( 121, 19 ),
    	new Point2D.Double( 118, 15 ),
    	new Point2D.Double( 91, 28 ),
    	new Point2D.Double( 81, 3 ),
    	new Point2D.Double( 159, -41 ),
    	new Point2D.Double( 169, -10 ),
    	new Point2D.Double( 136, 9 ),
    	new Point2D.Double( 139, 14 ),
    	new Point2D.Double( 171, -4 ),
    	new Point2D.Double( 179, 21 ),
    	new Point2D.Double( 137, 46 ),
    	new Point2D.Double( 140, 51 ),
    	new Point2D.Double( 181, 27 ),
    	new Point2D.Double( 191, 62 ),
    	new Point2D.Double( 121, 113 ),
    	new Point2D.Double( 105, 69 ),
    	new Point2D.Double( 119, 61 ),
    	new Point2D.Double( 117, 54 ),
    	new Point2D.Double( 103, 64 ),
    	new Point2D.Double( 101, 53 ),
    	new Point2D.Double( 95, 56 ),
    	new Point2D.Double( 97, 67 ),
    	new Point2D.Double( 76, 79 ),
    	new Point2D.Double( 80, 84 ),
    	new Point2D.Double( 99, 73 ),
    	new Point2D.Double( 116, 118 ),
    	new Point2D.Double( 2, 202 ),
    	new Point2D.Double( 2, 117 ),
    	new Point2D.Double( 43, 99 ),
    	new Point2D.Double( 39, 94 ),
    	new Point2D.Double( -4, 113 ),
    	new Point2D.Double( -4, 136 ),
    	new Point2D.Double( -15, 128 ),
    	new Point2D.Double( -18, 134 ),
    	new Point2D.Double( -4, 144 ),
    	new Point2D.Double( -4, 200 ),
    	new Point2D.Double( -66, 154 ),
    	new Point2D.Double( -43, 117 ),
    	new Point2D.Double( -34, 123 ),
    	new Point2D.Double( -30, 118 ),
    	new Point2D.Double( -53, 102 ),
    	new Point2D.Double( -56, 107 ),
    	new Point2D.Double( -47, 113 ),
    	new Point2D.Double( -70, 150 ),
    	new Point2D.Double( -129, 108 ),
    	new Point2D.Double( -106, 69 ),
    	new Point2D.Double( -93, 79 ),
    	new Point2D.Double( -89, 73 ),
    	new Point2D.Double( -116, 55 ),
    	new Point2D.Double( -120, 61 ),
    	new Point2D.Double( -111, 66 ),
    	new Point2D.Double( -134, 105 ),
    	new Point2D.Double( -191, 62 ),
    	new Point2D.Double( -153, 44 ),
    	new Point2D.Double( -143, 48 ),
    	new Point2D.Double( -140, 42 ),
    	new Point2D.Double( -150, 37 ),
    	new Point2D.Double( -149, 32 ),
    	new Point2D.Double( -157, 30 ),
    	new Point2D.Double( -161, 41 ),
    	new Point2D.Double( -190, 55 ),
      new Point2D.Double( -173, -3 ),
      new Point2D.Double( -146, 7 ),
      new Point2D.Double( -150, 14 ),
      new Point2D.Double( -143, 18 ),
      new Point2D.Double( -139, 4 ),
      new Point2D.Double( -173, -9 ),
      new Point2D.Double( -168, -22 ),
      new Point2D.Double( -172, -25 )
   };

   public House( double x, double y )
   {
      super( x, y );
      makeIrregular( shape );
   }
}