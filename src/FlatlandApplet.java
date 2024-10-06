import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FlatlandApplet extends JApplet
{ 
   public void init()
   {
			
			Flatland flatland = new Flatland();
			
			getContentPane().add( flatland.flatlandCanvas );
			setSize( flatland.flatlandCanvas.getWidth(), flatland.flatlandCanvas.getHeight() );
			requestFocusInWindow();
			flatland.flatlandCanvas.requestFocus();
			
   }

	public void paint( Graphics g )
	{
		g.drawString( "Applet? Where are you?", 20, 50 );
	}
}
