import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;

class FlatlandPanel extends JPanel
{
   // *** Generic Canvas Access Methods ***
   static FlatlandCanvas defaultCanvas = null;
   FlatlandCanvas canvas = null;

   public static void setDefaultCanvas( FlatlandCanvas fc ) { defaultCanvas = fc; }
   public void setCanvas( FlatlandCanvas fc ) { canvas = fc; }
   public FlatlandCanvas getCanvas() { return ( canvas == null ? defaultCanvas : canvas ); }
   //  ***
   
   public FlatlandPanel()
   {
      setBorder( new BorderUIResource.EtchedBorderUIResource() );
      setPreferredSize( new Dimension( 160, 480 ) );
      setSize( 160, 480 );
      
      setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
      
      // A. Square's control panel
      JPanel aSquarePanel = new JPanel();
      aSquarePanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.BLACK ), "A. Square" ) );
      aSquarePanel.setLayout( new BoxLayout( aSquarePanel, BoxLayout.Y_AXIS ) );
      JCheckBox chkVisionRays = new JCheckBox( "Show Vision Rays", true );
      chkVisionRays.addActionListener( new ActionListener() {
         public void actionPerformed( ActionEvent e ) {
            JCheckBox checkBox = (JCheckBox)e.getSource();
            if( checkBox.isSelected() )
            {
               Figure fig = (Figure)getCanvas().control;
               fig.eye.showRays = true;
            }
            else
            {
               Figure fig = (Figure)getCanvas().control;
               fig.eye.showRays = false;
            }
            getCanvas().requestFocus();
         }
      } );
      aSquarePanel.add( chkVisionRays );
      add( aSquarePanel );
      
      add( Box.createRigidArea( new Dimension( 1, 15 ) ) );
      
      // World Control panel
      JPanel worldPanel = new JPanel();
      worldPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.BLACK ), "A. Square" ) );
      worldPanel.setLayout( new BoxLayout( worldPanel, BoxLayout.Y_AXIS ) );
      JCheckBox chkFog = new JCheckBox( "Fog on/off", true );
      chkFog.addActionListener( new ActionListener() {
         public void actionPerformed( ActionEvent e ) {
            JCheckBox checkBox = (JCheckBox)e.getSource();
            if( checkBox.isSelected() )
            {
               getCanvas().setFog( true );
            }
            else
            {
               getCanvas().setFog( false );
            }
            getCanvas().requestFocus();
         }
      } );
      worldPanel.add( chkFog );
      JCheckBox chkColor = new JCheckBox( "Color on/off", false );
      worldPanel.add( chkColor );
      JCheckBox chkInfo = new JCheckBox( "Information on/off", false );
      worldPanel.add( chkInfo );
      add( worldPanel );
   }
}