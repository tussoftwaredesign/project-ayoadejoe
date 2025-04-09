package events_gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;

public class PreLaunch {
	
	public PreLaunch() {
   	 //define look and feel
        try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	//I use a method reference here to prepare the QuizzJokeApp Instance, which is then invoked when the SwingUtilities thread runs
    	SwingUtilities.invokeLater(QuizJokeApp::new);
        
	}

    public static void main(String[] args) {
    	PreLaunch prelaunch = new PreLaunch();
    }
	
}


/*
 * Method Reference

Use method createAndShowGUI in class QuizJokeApp as a function to run.”

createAndShowGUI does not run the method immediately.

It also does not care for now what the method returns.

It creates a method reference — basically a shortcut to say:

“Call createAndShowGUI later when needed.”
*/