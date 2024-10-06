import java.awt.*;
import java.lang.Math;
//import java.util.ArrayList;
import java.text.DecimalFormat;
//import java.awt.geom.Point2D;

class FlatlandObject
{
   // *** Generic Canvas Access Methods ***
   static FlatlandCanvas defaultCanvas = null;
   FlatlandCanvas canvas = null;

   public static void setDefaultCanvas( FlatlandCanvas fc ) { defaultCanvas = fc; }
   public void setCanvas( FlatlandCanvas fc ) { canvas = fc; }
   public FlatlandCanvas getCanvas() { return ( canvas == null ? defaultCanvas : canvas ); }
   //  ***
   
   final static double FRICTION_COEFFICIENT = 0.1;
   FlatlandShape shape;
   double velocity = 0, direction = 0, angularMomentum = 0;
   String label;
   boolean showInformation = false;
  
   public FlatlandObject() {
      shape = new FlatlandShape();
   }

   public FlatlandObject( FlatlandShape fs )
   {
      shape = fs;
   }
   
   public FlatlandObject( double x, double y )
   {
      shape = new FlatlandShape( x, y );
   }
   
   public boolean visible()
   {
      boolean pointVisible = false;
      double x, y;
      int halfWidth = getCanvas().getWidth() / 2;
      int halfHeight = getCanvas().getHeight() / 2;
      for( int i = 0; i < shape.tail; i++ )
      {
         x = shape.point[ i ].xFromOrigin();
         y = shape.point[ i ].yFromOrigin();
         pointVisible |= ( x >= -halfWidth && x <= halfWidth && y >= -halfHeight && y <= halfHeight );
      }
      //If any point is visible, then consider the whole object visible
      return pointVisible;
   }
   
   public void paint( Graphics g )
   {
      if( visible() )
      {
         if( showInformation )
         {
            int cx = (int)shape.center.xFromOrigin();
            int cy = -(int)shape.center.yFromOrigin();
            
            // Show the label
            if( label != null ) 
            {
               g.drawString( label, cx + 30, cy + 10 );
            }

            // Show the coordinates
            int coordX = (int)shape.center.x;
            int coordY = -(int)shape.center.y;
            DecimalFormat fmt = new DecimalFormat( "0.0" );
            String coords = fmt.format( coordX ) + ", " + fmt.format( coordY );
            g.drawString( coords, cx + 30, cy + 25 );
            
            // Show the angle in degrees
            g.drawString( fmt.format( shape.angle / Math.PI * 180 ), cx + 30, cy + 40 );
            
         }

         int x1, y1, x2, y2;
         int j = 1;
         for( int i = 0; i < shape.tail; i++ )
         {
            x1 = (int)shape.point[ i ].xFromOrigin();
            y1 = -(int)shape.point[ i ].yFromOrigin();
            x2 = (int)shape.point[ j ].xFromOrigin();
            y2 = -(int)shape.point[ j ].yFromOrigin();
   
            g.drawLine( x1, y1, x2, y2 );
            // g.drawLine( cx, cy, cx, cy );
            j++;
            if( j >= shape.tail ) j = 0;
         }
      }   
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

      direction = Math.atan( (Ay + By) / (Ax + Bx) );
      // Flip the XY axis if the angle should be in the negative quadrants
      if( Ax + Bx < 0 ) direction += Math.PI;
   }

   public void spin( double am )
   {
      angularMomentum += am;
   }
   
   public void push( double force )
   {
      push( force, direction );
   }
   
   public void brake( double howMuch )
   {
      velocity /= howMuch;
   }
   
   public void move()
   {
      velocity *= ( 1 - FRICTION_COEFFICIENT );
      angularMomentum *= ( 1 - FRICTION_COEFFICIENT );
      
      direction += angularMomentum;
      if( direction < 0 )
         direction += Math.PI * 2;
      if( direction >= Math.PI * 2 )
         direction -= Math.PI * 2;
      
      shape.setAngle( direction );
      shape.slide( velocity * Math.cos( direction ), velocity * Math.sin( direction ) );
   }
}

class Figure extends FlatlandObject
{
   final double DEFAULT_RADIUS = 10;
   Eye eye;
   
   public Figure( double x, double y, int sides )
   {
      super( x, y );
      shape.makeRegular( sides, DEFAULT_RADIUS );
      eye = new Eye( this, shape.point[ 0 ] );
   }
   
   public void move()
   {
      super.move();
      eye.direction = direction;
   }
   
   public void paint( Graphics g )
   {
      super.paint( g );
      eye.paint( g );
   }

}

class Eye extends FlatlandObject
{
   final int TRACE_LINES = 32;
   boolean active = false;
   final double DEFAULT_RADIUS = 3;
	final int RANGE = 300;
	FlatlandPoint pointOfAttachment;
   FlatlandObject objectOfAttachment;
	
   public Eye( FlatlandObject fo, FlatlandPoint fp )
   {
      super();
      showInformation = false;
		pointOfAttachment = fp;
      objectOfAttachment = fo;
      shape.weldTo( pointOfAttachment );
      // Make the eye shape
      shape.makeRegular( 5, DEFAULT_RADIUS );
   }

	public FlatlandPoint firstVisible( double lookDirection )
	{
      FlatlandPoint outerPerimeter =
         new FlatlandPoint(
            shape.center.x + Math.cos( lookDirection ) * RANGE,
            shape.center.y + Math.sin( lookDirection ) * RANGE );
      
		Object[] listOfObjects = (Object[])shape.getCanvas().objects.toArray();
		FlatlandObject flObj;
		FlatlandPoint endPoint, shortestPoint = null;
		for( int i = 0; i < listOfObjects.length; i++ )
		{
			flObj = (FlatlandObject)(listOfObjects[ i ]);
			if( flObj != this && flObj != objectOfAttachment )
			{
				// Go through each line segment in each FlatlandObject
				// and determine if our line intersects
				int k = 1;
				for( int j = 0; j < flObj.shape.tail; j++ )
				{
               endPoint = shape.center.intersect( outerPerimeter, flObj.shape.point[ j ], flObj.shape.point[ k ] );

					if( endPoint != null )
					{
                  endPoint.distance = endPoint.distance( shape.center );
						if( shortestPoint == null ) {
							shortestPoint = endPoint;
						} else {
							if( shortestPoint.distance( shape.center ) > endPoint.distance )
							{
								shortestPoint = endPoint;
							}
						}
					}
					k++;
					if( k >= flObj.shape.tail ) k = 0;
				}
			}
		}      
		return shortestPoint;
	}

   public void paint( Graphics g )
	{
      super.paint( g );
      
      int displayX = -getCanvas().getWidth() / 2 + 20 + ( TRACE_LINES * 5 );
      int displayY = -getCanvas().getHeight() / 2 + 20;
      int green = 255 - 32 * 5;
      if( active )
      {
         for( double d = -Math.PI/4; d <= Math.PI/4; d += Math.PI / 2 / TRACE_LINES )
         {
            FlatlandPoint point = firstVisible( direction + d );
            if( point != null )
            {
            	g.setColor( new Color( 0, green, 30 ) );
               g.fillRect( displayX, displayY + 10, 5, 5 );
               green += 5;
            	g.drawLine( (int)shape.center.xFromOrigin(), -(int)shape.center.yFromOrigin(), (int)point.xFromOrigin(), -(int)point.yFromOrigin() );
            	g.setColor( Color.RED );
            	g.fillOval( (int)point.xFromOrigin()-2, -(int)point.yFromOrigin()-2, 5, 5 );
               
               
               int intensity = 255 - (int)( point.distance / RANGE * 255 );
               //System.out.println( "intensity: " + intensity + " dist: " + point.distance );
               if( intensity > 255 ) intensity = 255;
               g.setColor( new Color( intensity, intensity, intensity ) );
               g.fillRect( displayX, displayY, 5, 10 );
            }
            else
            {
               g.setColor( Color.black );
               g.fillRect( displayX, displayY, 5, 10 );
            }
            displayX -= 5;
         }
   		g.setColor( Color.BLACK );
      }
	}   
}

class House extends FlatlandObject
{
	double[][] houseShapeData = {
		{-173, -25},
		{-199, 64},
		{0, 210},
		{199, 64},
		{123, -169},
		{-123, -169},
		{-151, -94},
		{-146, -91},
		{-138, -113},
		{-122, -113},
		{-121, -119},
		{-136, -119},
		{-126, -145},
		{-119, -163},
		{-69, -163},
		{-69, -118},
		{-84, -118},
		{-84, -112},
		{-49, -112},
		{-49, -118},
		{-63, -118},
		{-63, -163},
		{1, -163},
		{1, -117},
		{-11, -117},
		{-11, -111},
		{17, -111},
		{17, -117},
		{7, -117},
		{7, -144},
		{7, -163},
		{119, -163},
		{132, -124},
		{45, -72},
		{44, -78},
		{38, -75},
		{48, -53},
		{53, -56},
		{49, -67},
		{134, -118},
		{157, -47},
		{80, -2},
		{76, -11},
		{70, -8},
		{91, 43},
		{96, 40},
		{93, 33},
		{121, 19},
		{118, 15},
		{91, 28},
		{81, 3},
		{159, -41},
		{169, -10},
		{136, 9},
		{139, 14},
		{171, -4},
		{179, 21},
		{137, 46},
		{140, 51},
		{181, 27},
		{191, 62},
		{121, 113},
		{105, 69},
		{119, 61},
		{117, 54},
		{103, 64},
		{101, 53},
		{95, 56},
		{97, 67},
		{76, 79},
		{80, 84},
		{99, 73},
		{116, 118},
		{2, 202},
		{2, 117},
		{43, 99},
		{39, 94},
		{-4, 113},
		{-4, 136},
		{-15, 128},
		{-18, 134},
		{-4, 144},
		{-4, 200},
		{-66, 154},
		{-43, 117},
		{-34, 123},
		{-30, 118},
		{-53, 102},
		{-56, 107},
		{-47, 113},
		{-70, 150},
		{-129, 108},
		{-106, 69},
		{-93, 79},
		{-89, 73},
		{-116, 55},
		{-120, 61},
		{-111, 66},
		{-134, 105},
		{-191, 62},
		{-153, 44},
		{-143, 48},
		{-140, 42},
		{-150, 37},
		{-149, 32},
		{-157, 30},
		{-161, 41},
		{-190, 55},
		{-173, -3},
		{-146, 7},
		{-150, 14},
		{-143, 18},
		{-139, 4},
		{-173, -9},
		{-168, -22},
		{-172, -25 } };
   public House( double x, double y )
   {
      super( x, y );
      shape.loadData( houseShapeData );
   }
}
