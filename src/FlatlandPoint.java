import java.awt.geom.Point2D;
import java.lang.Math;
import java.util.Vector;
import java.util.Enumeration;

class FlatlandPoint extends Point2D.Double
{
   // *** Generic Canvas Access Methods ***
   static FlatlandCanvas defaultCanvas = null;
   FlatlandCanvas canvas = null;

   public static void setDefaultCanvas( FlatlandCanvas fc ) { defaultCanvas = fc; }
   public void setCanvas( FlatlandCanvas fc ) { canvas = fc; }
   public FlatlandCanvas getCanvas() { return ( canvas == null ? defaultCanvas : canvas ); }
   //  ***

   double angle = 0, distance = 0;
   Vector children = new Vector();
   
   public FlatlandPoint()
   {
      super();
   }
   
   public FlatlandPoint( double theX, double theY )
   {
      super( theX, theY );
      //computeRadial();
   }
   
   public void computeRadial( FlatlandPoint fp )
   {
      distance = distance( fp );
      double opposite = y - fp.y;
      double adjacent = x - fp.x;
      if( adjacent == 0 )
      {
         if( opposite > 0 ) {
            angle = Math.PI / 2; }
         else {
            angle = Math.PI * 3 / 2;
         }
      } else {
         angle = Math.acos( adjacent / distance );
         
         // If we're in quadrants III or IV, then it's 360 degrees minus the angle
         if( opposite < 0 )
            angle = Math.PI * 2 - angle;
      }
   }
   
   public void computeLinear()
   {
      x = Math.cos( angle ) * distance;
      y = Math.sin( angle ) * distance;
   }   
   
   public double xFromOrigin()
   {
      double tempX = x - getCanvas().origin.x;
      double tempY = y - getCanvas().origin.y;
      double tempAngle = getCanvas().control.direction;
      return tempX * Math.cos( tempAngle ) + tempY * Math.sin( tempAngle );
   }

   public double yFromOrigin()
   {
      double tempX = x - getCanvas().origin.x;
      double tempY = y - getCanvas().origin.y;
      double tempAngle = getCanvas().control.direction;
      return -tempX * Math.sin( tempAngle ) + tempY * Math.cos( tempAngle );
   }
   
   
   public FlatlandPoint intersect( FlatlandPoint P2, FlatlandPoint Q1, FlatlandPoint Q2 )
   {
      double Px1 = x;
      double Py1 = y;
      double Px2 = P2.x;
      double Py2 = P2.y;
      double Qx1 = Q1.x;
      double Qy1 = Q1.y;
      double Qx2 = Q2.x;
      double Qy2 = Q2.y;
      double Qm, Pm, Qb, Pb;
      double Qrise, Qrun, Prise, Prun;
      double retY, retX;
      
      Qrise = Qy2 - Qy1;
      Prise = Py2 - Py1;
      Qrun = Qx2 - Qx1;
      Prun = Px2 - Px1;
      
      if( Qrun == 0 )
      {
         Pm = Prise / Prun;      // slope of segment P
         
         Pb = Py1 - Pm * Px1;    // y-intercept of segment P
         // If we have an infinite slope, deal with it separately

         retX = Qx1;
         retY = Pm * Qx1 + Pb;
      }
      else if( Qrise == 0 )
      {
         Pm = Prise / Prun;      // slope of segment P
         
         Pb = Py1 - Pm * Px1;    // y-intercept of segment P
         
         retX = ( Qy1 - Pb ) / Pm;
         retY = Qy1;
      }
      else
      {
         Qm = Qrise / Qrun;      // slope of segment Q
         Pm = Prise / Prun;      // slope of segment P
         
         Qb = Qy1 - Qm * Qx1;    // y-intercept of segment Q
         Pb = Py1 - Pm * Px1;    // y-intercept of segment P
         
         // Calculate point of intersection
         retY = (Qb * Pm - Pb * Qm) / (Pm - Qm);
         retX = ( retY - Pb ) / Pm;
      }
            
      double t;
      if( Px1 > Px2 ) { t = Px1; Px1 = Px2; Px2 = t; }
      if( Py1 > Py2 ) { t = Py1; Py1 = Py2; Py2 = t; }
      if( Qx1 > Qx2 ) { t = Qx1; Qx1 = Qx2; Qx2 = t; }
      if( Qy1 > Qy2 ) { t = Qy1; Qy1 = Qy2; Qy2 = t; }

      if( retX >= Px1 && retX <= Px2 && retX >= Qx1 && retX <= Qx2 &&
          retY >= Py1 && retY <= Py2 && retY >= Qy1 && retY <= Qy2 )
      {
         return new FlatlandPoint( retX, retY );
      } else {
         return null;
      }
   }
   
   public void slide( double slideX, double slideY )
   {
      Enumeration enum = children.elements();
      while( enum.hasMoreElements() )
      {
         FlatlandPoint fp = (FlatlandPoint)enum.nextElement();
         fp.slide( slideX, slideY );
      }
      
      x += slideX;
      y += slideY;
   }
   
   public void move( double moveX, double moveY )
   {
      double diffX = moveX - x;
      double diffY = moveY - y;
      
      slide( diffX, diffY );
   }   
}