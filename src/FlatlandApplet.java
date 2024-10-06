import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class FlatlandApplet extends Applet
{ 
   public void init()
   {
      this.setBackground( Color.WHITE );
      Button openFrame = new Button( "Begin" );
      openFrame.addActionListener( new ActionListener() {
         public void actionPerformed( ActionEvent e ) {
            Flatland f = new Flatland();
            f.flatlandFrame.setVisible( true );
         }
      } );
      add( openFrame );
   } 
}