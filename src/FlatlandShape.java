import java.io.*;
import java.util.StringTokenizer;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import java.util.Enumeration;

class FlatlandShape
{
   // *** Generic Canvas Access Methods ***
   static FlatlandCanvas defaultCanvas = null;
   FlatlandCanvas canvas = null;

   public static void setDefaultCanvas( FlatlandCanvas fc ) { defaultCanvas = fc; }
   public void setCanvas( FlatlandCanvas fc ) { canvas = fc; }
   public FlatlandCanvas getCanvas() { return ( canvas == null ? defaultCanvas : canvas ); }
   //  ***

   int tail = 0, capacity = 100;
   double angle = 0;
   FlatlandPoint center = new FlatlandPoint( 0, 0 );
   FlatlandPoint[] point = new FlatlandPoint[ capacity ];

   public FlatlandShape()
   {
      center = new FlatlandPoint( 0, 0 );
   }
   
   public FlatlandShape( double x, double y )
   {
      center = new FlatlandPoint( x, y );
   }

   public void save( String filename ) throws IOException {
      FileWriter fw = new FileWriter( filename );
      BufferedWriter bw = new BufferedWriter( fw );
      PrintWriter outFile = new PrintWriter( bw );
      
      for( int i = 0; i < tail; i++ )
         outFile.println( point[ i ].x + ", " + point[ i ].y );
   }

   public void load( String filename ) {
      try {
         FileReader fr = new FileReader( filename );
         BufferedReader inFile = new BufferedReader( fr );
         
         StringTokenizer tokenizer;
         String token;
         double x, y;
         
         String line = inFile.readLine();
         while( line != null )
         {
            tokenizer = new StringTokenizer( line, "," );
            try
            {
               x = Double.parseDouble( tokenizer.nextToken() );
               y = Double.parseDouble( tokenizer.nextToken() );
               add( new FlatlandPoint( center.x + x, center.y + y ) );
            }
            catch( NumberFormatException e )
            {
               System.out.println( "Couldn't read '" + line + "' from input file " + filename );
            }
            line = inFile.readLine();
         }
      }
      catch( FileNotFoundException e )
      {
         System.out.println( "The file " + filename + " was not found." );
      }
      catch( IOException e )
      {
         System.out.println( e );
      }
   }
		
		public void loadData( double[][] data )
		{
			double[] pair;
			for( int i = 0; i < data.length; i++ )
			{
				pair = data[ i ];
				add( new FlatlandPoint( center.x + pair[ 0 ], center.y + pair[ 1 ] ) );
			}
		}
   
   public void add( FlatlandPoint p )
   {
      if( tail >= capacity )
         doubleCapacity();
      
      point[ tail++ ] = p;
   }
   
   public void add( double x, double y )
   {
      add( new FlatlandPoint( x, y ) );
   }
   
   public void remove( int index )
   {
      for( int i = index; i < tail; i++ )
      {
         point[ i ] = point[ i + 1 ];
      }
      
      tail--;
   }

   public void clear()
   {
      tail = 0;
   }
   
   private void doubleCapacity()
   {
      FlatlandPoint tempArray[] = point;
      point = new FlatlandPoint[ capacity * 2 ];
      for( int i = 0; i < tail; i++ )
         point[ i ] = tempArray[ i ];
   }
  
   public void makeRegular( int sides, double radius )
   {
      if( sides > 1 )
      {
         double angleIncrease = Math.PI * 2 / sides;
         double x, y, length;
         // Calculate length of each line segment
         length = Math.sin( angleIncrease / 2 ) * 2 * radius;
         //System.out.println( "******* " + sides + " sides *******" );
         for( int i = 0; i < sides; i++ )
         {
            y = Math.sin( angleIncrease * i ) * radius;
            x = Math.cos( angleIncrease * i ) * radius;
            FlatlandPoint fp = new FlatlandPoint( center.x + x, center.y + y );
            center.children.add( fp );
            fp.computeRadial( center );
            //System.out.print( fp.angle + ", " );
            add( fp );
         }
         //System.out.println();
      }
      else
      {
         // Can't initialize sides
         System.out.println( "FlatlandShape::makeRegular(): Can't initialize " + sides + " sides." );
      }
   }
   
   public void slide( double slideX, double slideY )
   {
      center.slide( slideX, slideY );
   }
   
   public void weldTo( FlatlandPoint p )
   {
      p.children.addAll( center.children );
      center = p;
   }
   
   public void setAngle( double a )
   {
      double distanceToCenter;
         
      if( angle - a != 0 )
      {
         angle = a;
         center.angle = a;

         double tempAngle;
         for( int i = 0; i < tail; i++ )
         {
            tempAngle = point[ i ].angle + a;
            point[ i ].move(
               center.x + point[ i ].distance * Math.cos( tempAngle ),
               center.y + point[ i ].distance * Math.sin( tempAngle ) );
         }
      }
   }
   
   public void setVelocity( double v )
   {
      double vx = Math.cos( angle ) * v;
      double vy = Math.sin( angle ) * v;
      
      center.velocityX = vx;
      center.velocityY = vy;
      for( int i = 0; i < tail; i++ )
      {
         point[ i ].velocityX = vx;
         point[ i ].velocityY = vy;
         
         Enumeration enum = point[i].children.elements();
         while( enum.hasMoreElements() )
         {
        	 FlatlandPoint fp = (FlatlandPoint)enum.nextElement();
             fp.velocityX = vx;
             fp.velocityY = vy;
         }
      }
   }

   public Rectangle2D.Double getBoundingBox()
   {
      double left = point[ 0 ].x, right = point[ 0 ].x, top = point[ 0 ].y, bottom = point[ 0 ].y;
      
      for( int i = 1; i < tail; i++ )
      {
         if( point[ i ].x < left )
            left = point[ i ].x;
         if( point[ i ].x > right )
            right = point[ i ].x;
         if( point[ i ].y < top )
            top = point[ i ].y;
         if( point[ i ].y > bottom )
            bottom = point[ i ].y;
      }
      
      return new Rectangle2D.Double( left, top, right-left, bottom-top );
   }
}
