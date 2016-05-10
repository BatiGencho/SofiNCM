package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import sofistik.AnalysisOptions;


class GuiColumnVisualisation extends JPanel
{

	private static final long serialVersionUID = 1L;
	public static final String sofColumnPath =  "column1.png";
	private ImageIcon iconLogo;
	private URL imgURL;

	GuiColumnVisualisation()
	{
		this.iconLogo = null;
	  	this.imgURL = GuiColumnVisualisation.this.getClass().getClassLoader().getResource(GuiColumnVisualisation.sofColumnPath);
	  	if (this.imgURL != null) {
	  		this.iconLogo = new ImageIcon(this.imgURL);
	  	} else {
	  		JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + GuiColumnVisualisation.sofColumnPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
	  		System.exit(1);
	  	}
	  	JLabel logoLabel = new JLabel(iconLogo, SwingConstants.CENTER);
	  	logoLabel.setSize(this.iconLogo.getIconWidth(), this.iconLogo.getIconWidth());

	  	this.add(logoLabel);
		
	}
	
	// Override paintComponent to perform your own painting
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);     // paint parent's background
		setBackground(Color.BLACK);  // set background color for this JPanel
		

		//g.setColor(Color.YELLOW);    // set the drawing color
		//g.drawLine(this.getWidth()/2, (int)(0.1*(GuiAnalysis.COLUMN_VISUALISATION_HEIGHT)), this.getWidth()/2, (int)(0.9*(GuiAnalysis.COLUMN_VISUALISATION_HEIGHT)));

//		if (AnalysisOptions.selectedStrLine == -1 && AnalysisOptions.selectedGroup == -1) // || AnalysisOptions.selectedLoadCases.isEmpty()
//		{
//			g.drawString(Integer.toString(AnalysisOptions.selectedStrLine), this.getWidth()/2, (int)(0.95*(GuiAnalysis.COLUMN_VISUALISATION_HEIGHT)));
//		}
       
//       g.drawOval(150, 180, 10, 10);
//       g.drawRect(200, 210, 20, 30);
//       g.setColor(Color.RED);       // change the drawing color
//       g.fillOval(300, 310, 30, 50);
//       g.fillRect(400, 350, 60, 50);
//       // Printing texts
//       g.setColor(Color.WHITE);
//       g.setFont(new Font("Monospaced", Font.PLAIN, 12));
       	//g.drawString(Integer.toString(AnalysisOptions.selectedStrLine), 10, 20);
    }
 }
