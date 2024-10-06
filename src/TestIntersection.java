import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

/*
// header - edit "Data/yourJavaAppletHeader" to customize
// contents - edit "EventHandlers/Java Applet/onCreate" to customize
*/

public class TestIntersection extends java.applet.Applet
{
   int mouseX = 0, mouseY = 0;
   
	public void init()
	{
		resize( 560, 560 );
      new FlatlandPoint();
      addMouseMotionListener( new MouseMotionAdapter() {
         public void mouseMoved( MouseEvent e ) {
            mouseX = e.getX() - 560/2;
            mouseY = e.getY() - 560/2;
            repaint();
         }
      } );
	}


	public void paint(Graphics g)
	{
      g.translate( 560/2, 560/2 );
      g.drawLine( 0, 0, mouseX, mouseY );
      FlatlandPoint origin = new FlatlandPoint( 0, 0 );
      FlatlandPoint p = new FlatlandPoint( mouseX, -mouseY );
      origin.computeRadial( p );
      g.drawString( "" + origin.angle, mouseX, mouseY );
	}
}


