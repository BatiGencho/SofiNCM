package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import sofistik.LibraryLoader;

import java.beans.*;
import java.net.URL;
import java.util.Random;
 
public class GuiProgressBar extends JPanel
                             implements ActionListener, 
                                        PropertyChangeListener {
 
    private static final long serialVersionUID = 1L;
    private JProgressBar progressBar;
    private JButton startButton;
    private JTextArea taskOutput;
    private Task task;
    private JPanel panel;
    
    private static JFrame frame;
    
    static GuiMain pGuiMain = null;
    static String pathToCdb = null;
 
    //internal class that does the threading and updates when done swing components
    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
        	
        	//open the cdb
        	try {
        		GuiMain.libInterface.loadCdb(pathToCdb);
        	} 
        	catch (Exception e)
        	{
        		JOptionPane.showMessageDialog(null, "An error occured whilst loading the cdb file", "Cdb loader error", JOptionPane.ERROR_MESSAGE);
        	}
        	//setProgress(100);
            Random random = new Random();
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
            while (progress < 100) {
                //Sleep for up to one second.
                try {
                    Thread.sleep(random.nextInt(100));
                } catch (InterruptedException ignore) {}
                //Make random progress.
                progress += random.nextInt(10);
                setProgress(Math.min(progress, 100));
            }
            return null;
        }
 
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
        	//minimize the Jpanel ???
        	
            Toolkit.getDefaultToolkit().beep();
            startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            taskOutput.append("CDB loaded!\n");
            GuiProgressBar.pGuiMain.setEnabled(true);
            pGuiMain.consoleTArea.append("Successfully opened " + pathToCdb + "\n");
            
            pGuiMain.consoleTArea.append("Structural Groups loaded : " +  GuiMain.libInterface.getGroups() + "\n");
            pGuiMain.consoleTArea.append("Structural Lines loaded : " + GuiMain.libInterface.getStructLines() + "\n");
            pGuiMain.consoleTArea.append("Structural Nodes loaded : " + GuiMain.libInterface.getNodes() + "\n");
            pGuiMain.consoleTArea.append("Structural Beam FE loaded : " + GuiMain.libInterface.getBeams() + "\n");
            pGuiMain.consoleTArea.append("Structural Sections loaded : " + GuiMain.libInterface.getSects() + "\n");
            pGuiMain.consoleTArea.append("Structural Materials loaded : " + GuiMain.libInterface.getMaterials() + "\n");
            pGuiMain.consoleTArea.append("Structural Load Cases loaded : " + GuiMain.libInterface.getLoadCases() + "\n");	
            pGuiMain.consoleTArea.append("==> Generated Topological Analysis Members = " + GuiMain.libInterface.getMembers() + "\n");
            
            if (GuiMain.libInterface.getMembers()>0)
            {
            	pGuiMain.preprocessAnalysisButton.setEnabled(true);
            	frame.setVisible(false);
            }
            else
            {
            	JOptionPane.showMessageDialog(null, "Unable to generated any analysis members. Check the cdb again!", "Cdb loader error", JOptionPane.ERROR_MESSAGE);
            }
            
        }
    }
 
    public GuiProgressBar() 
    {
        super(new BorderLayout());

        //Create the demo's UI.
        this.startButton = new JButton("Start");
        this.startButton.setActionCommand("start");
        this.startButton.addActionListener(this);
 
        this.progressBar = new JProgressBar(0, 100);
        this.progressBar.setValue(0);
        this.progressBar.setStringPainted(true);
 
        this.taskOutput = new JTextArea(5, 20);
        this.taskOutput.setMargin(new Insets(5,5,5,5));
        this.taskOutput.setEditable(false);
 
        this.panel = new JPanel();
        this.panel.add(startButton);
        this.panel.add(progressBar);
 
        this.add(panel, BorderLayout.PAGE_START);
        this.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
 
    }
 
    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        startButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusable, so
        //we create new instances as needed.
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
    }
 
    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("progress")) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            taskOutput.append(String.format("Loading Completed %d%%.\n", task.getProgress()));
        } 
    }

 
    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    public static void doLoadCdbInProgressBar(final String pathToCdb, final GuiMain pGuiMain) 
    {

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        //javax.swing.SwingUtilities.invokeLater(new Runnable() {
            //public void run()
            //{
    			GuiProgressBar.pGuiMain = pGuiMain;
    			GuiProgressBar.pGuiMain.setEnabled(false); //disable the GuiMainAnalysis
    			GuiProgressBar.pathToCdb = pathToCdb;
            	
                //Create and set up the window.
                frame = new JFrame("CDB Loader");
        	    //add a windows listener
                frame.addWindowListener(new WindowAdapter()
        	    {
        	    	@Override
        	    	public void windowClosing(WindowEvent e) 
        	    	{
        	    		GuiProgressBar.pGuiMain.setEnabled(true);
        	    		GuiProgressBar.pGuiMain.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        	    	}
        	    });
                
                
                //Create and set up the content pane.
                JComponent newContentPane = new GuiProgressBar();
                newContentPane.setOpaque(true); //content panes must be opaque
                frame.setContentPane(newContentPane);
                
                //Display the window.
                frame.pack();
                
                //translate to middle of screen
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                Point p = ge.getCenterPoint();
                p.translate(-frame.getWidth()/2, -frame.getHeight()/2);
                frame.setLocation(p);
                
        	    //add the desktop logo icon
        	    ImageIcon desktopImage = null;
        	  	URL imgURL = newContentPane.getClass().getClassLoader().getResource(GuiMain.desktopIconPath);
        	  	if (imgURL != null) {
        	  		desktopImage = new ImageIcon(imgURL);
        	  		frame.setIconImage(desktopImage.getImage());
        	  	}
                
                //show all
                frame.setVisible(true);
            	
//            }
//        });
        
    }
}