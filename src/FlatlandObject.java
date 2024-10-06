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
   boolean fixedPosition = true;
  
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
            
            // Use the appropriate color, either from the point,
            // or else just black if we're in black and white mode
            if( getCanvas().useColor )
            {
               g.setColor( shape.point[ i ].lineColor );
            }
            else
            {
               g.setColor( Color.BLACK );
            }
            g.drawLine( x1, y1, x2, y2 );
            // g.drawLine( cx, cy, cx, cy );
            j++;
            if( j >= shape.tail ) j = 0;
         }
      }   
   }
   
  // Push the center (point) towards a given angle in radians
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
   	  //double oldVelocity = velocity;
   	  
      velocity *= ( 1 - FRICTION_COEFFICIENT );
      angularMomentum *= ( 1 - FRICTION_COEFFICIENT );
      
      direction += angularMomentum;
      if( direction < 0 )
         direction += Math.PI * 2;
      if( direction >= Math.PI * 2 )
         direction -= Math.PI * 2;
      
      shape.setAngle( direction );
      shape.setVelocity( velocity );
      shape.slide( velocity * Math.cos( direction ), velocity * Math.sin( direction ) );
      
      //Gravity
      if( getCanvas().gravity && !fixedPosition ) push( 0.1, Math.PI * 3 / 2 );
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
      fixedPosition = false;
   }
   
   public void move()
   {
      super.move();
      detectCollision();
      eye.direction = direction;
   }
   
   public void detectCollision()
   {
		Object[] listOfObjects = (Object[])getCanvas().objects.toArray();
		FlatlandObject flObj;
      FlatlandPoint collisionPoint;
      FlatlandPoint p1, p2, p3, p4;
      
		for( int i = 0; i < listOfObjects.length; i++ )
		{
			flObj = (FlatlandObject)(listOfObjects[ i ]);
			if( flObj != this )
			{
				// Go through each line segment in each FlatlandObject
				// and determine if our line intersects
				int k = 1;
				for( int j = 0; j < flObj.shape.tail; j++ )
				{
               p1 = flObj.shape.point[ j ];
               p2 = flObj.shape.point[ k ];
               
               int l = 1;
               for( int m = 0; m < shape.tail; m++ )
               {
                  p3 = shape.point[ l ];
                  p4 = shape.point[ m ];
                  
                  collisionPoint = p1.intersect( p2, p3, p4 );
                  if( collisionPoint != null )
                  {
                     velocity = -velocity;
                     angularMomentum = -angularMomentum;
                     direction += angularMomentum;
                     if( direction < 0 )
                        direction += Math.PI * 2;
                     if( direction >= Math.PI * 2 )
                        direction -= Math.PI * 2;
                     
                     shape.setAngle( direction );
                     shape.slide( velocity * Math.cos( direction ), velocity * Math.sin( direction ) );
                     return;
                  }
                  
                  l++;
                  if( l >= shape.tail ) l = 0;
               }
               k++;
               if( k >= flObj.shape.tail ) k = 0;
            }
         }
      }
   }
     
   public void paint( Graphics g )
   {
      super.paint( g );
      eye.paint( g );
   }

}

class Eye extends FlatlandObject
{
   final int TRACE_LINES = 128;
   boolean active = false;
   boolean showRays = true;
   final double DEFAULT_RADIUS = 3;
	final int RANGE = 450;
	FlatlandPoint pointOfAttachment;
   FlatlandObject objectOfAttachment;
   FlatlandVisor visor;
	
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
                  endPoint.setColor( flObj.shape.point[ j ].lineColor );
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
      
      
      if( active )
      {
         Graphics visorGraphics = g;
         if( visor != null )
         {
            visorGraphics = visor.getGraphics();
         }

         //int displayX = -visor.getWidth() / 2 + 20 + ( TRACE_LINES * 5 );
         //int displayY = -visor.getHeight() / 2 + 20;
         int displayX = visor.getWidth();
         int displayY = 0;
         int displayWidth = visor.getWidth() / TRACE_LINES;
         int displayHeight = visor.getHeight();
         int green = 255;
         int greenIncrement = 0;
         int intensity = 255;
         
         for( double d = -Math.PI/4; d <= Math.PI/4; d += Math.PI / 2 / TRACE_LINES )
         {
            FlatlandPoint point = firstVisible( direction + d );
            if( point != null )
            {
               // Draw the green lines and red contact points
               if( showRays )
               {
                  g.setColor( new Color( 0, green, 30 ) );
            	   g.drawLine( (int)shape.center.xFromOrigin(), -(int)shape.center.yFromOrigin(), (int)point.xFromOrigin(), -(int)point.yFromOrigin() );
            	   g.setColor( Color.RED );
               	g.fillOval( (int)point.xFromOrigin()-2, -(int)point.yFromOrigin()-2, 5, 5 );
               
                  green += greenIncrement;
               }
               
               if( getCanvas().fog )
               {
                  intensity = 255 - (int)( point.distance / RANGE * 255 );
               }
               else
               {
                  intensity = 255;
               }
               //System.out.println( "intensity: " + intensity + " dist: " + point.distance );
               if( intensity > 255 ) intensity = 255;
               if( intensity < 0 ) intensity = 0;
               if( getCanvas().useColor )
               {
                  int red = point.lineColor.getRed();
                  int green2 = point.lineColor.getGreen();
                  int blue = point.lineColor.getBlue();
                  visorGraphics.setColor( new Color( intensity * red / 255, intensity * green2 / 255, intensity * blue / 255 ) );
               }
               else
               {
                  visorGraphics.setColor( new Color( intensity, intensity, intensity ) );
               }
               visorGraphics.fillRect( displayX, displayY, displayWidth, displayHeight );
            }
            else
            {
               visorGraphics.setColor( Color.black );
               visorGraphics.fillRect( displayX, displayY, displayWidth, displayHeight );
            }
            displayX -= displayWidth;
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

class Tree extends FlatlandObject
{
	double[][] deciduousShapeData =
   {
    	{ -5, -75},
    	{ 41, -79},
    	{ 58, -80},
    	{ 47, -66},
    	{ 24, -44},
    	{ 20, -16},
    	{ 21, 22},
    	{ 30, 43},
    	{ 47, 48},
    	{ 61, 48},
    	{ 63, 38},
    	{ 65, 31},
    	{ 70, 25},
    	{ 77, 22},
    	{ 86, 23},
    	{ 97, 32},
    	{ 97, 49},
    	{ 92, 59},
    	{ 84, 67},
    	{ 72, 64},
    	{ 64, 55},
    	{ 44, 59},
    	{ 22, 56},
    	{ 24, 68},
    	{ 31, 74},
    	{ 39, 77},
    	{ 50, 80},
    	{ 59, 88},
    	{ 64, 96},
    	{ 75, 87},
    	{ 85, 90},
    	{ 91, 97},
    	{ 93, 107},
    	{ 85, 121},
    	{ 71, 125},
    	{ 64, 121},
    	{ 62, 115},
    	{ 62, 108},
    	{ 60, 102},
    	{ 50, 90},
    	{ 29, 84},
    	{ 11, 70},
    	{ 3, 119},
    	{ 13, 127},
    	{ 17, 133},
    	{ 16, 145},
    	{ 5, 150},
    	{ -7, 150},
    	{ -25, 141},
    	{ -31, 131},
    	{ -24, 116},
    	{ -4, 111},
    	{ 1, 85},
    	{ -4, 66},
    	{ -17, 64},
    	{ -36, 76},
    	{ -49, 87},
    	{ -44, 103},
    	{ -44, 115},
    	{ -52, 122},
    	{ -66, 123},
    	{ -74, 114},
    	{ -84, 97},
    	{ -84, 87},
    	{ -76, 74},
    	{ -65, 71},
    	{ -53, 75},
    	{ -44, 70},
    	{ -32, 62},
    	{ -18, 53},
    	{ -18, 41},
    	{ -30, 36},
    	{ -40, 39},
    	{ -49, 43},
    	{ -50, 52},
    	{ -58, 59},
    	{ -70, 57},
    	{ -76, 47},
    	{ -71, 33},
    	{ -60, 29},
    	{ -50, 34},
    	{ -34, 28},
    	{ -16, 14},
    	{ -18, -11},
    	{ -20, -34},
    	{ -33, -50},
    	{ -43, -56},
    	{ -52, -69},
    	{ -57, -78},
    	{ -5, -75 },
   };

   public Tree( double x, double y )
   {
      super( x, y );
      shape.loadData( deciduousShapeData );
   }

}

class Mountain extends FlatlandObject
{
	double[][] mountain = {
        { -93, 49},
        { -159, -39},
        { -31, -43},
        { 86, -36},
        { 124, -44},
        { 79, 84},
        { 64, 79},
        { 45, 47},
        { 54, 61},
        { 65, 52},
        { 77, 55},
        { 83, 51},
        { 89, 55},
        { 78, 84},
        { 64, 79},
        { 57, 67},
        { 2, 136},
        { -31, 103},
        { -17, 89},
        { -5, 94},
        { 10, 90},
        { 27, 104},
        { 10, 90},
        { -5, 94},
        { -17, 89},
        { -31, 102},
        { -65, 41},
        { -76, 37},
        { -87, 27},
        { -92, 31},
        { -100, 27},
        { -104, 34},
        { -93, 50},
        { -76, 37}
    };

	public Mountain( double x, double y )
	{
		super( x, y );
		shape.loadData( mountain );
	}
}