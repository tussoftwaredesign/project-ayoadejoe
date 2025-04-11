package events_gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;


/**
 * Who Wants to be a Knowledge Bank - Quiz & Joke App
 * A desktop application built using Java Swing that presents users with quiz questions
 * and rewards correct answers with jokes fetched live from the internet.
 *
 * @Author: Joseph Adetunji Ayoade
 * Created for: MSc Software Design With AI - OOP2 Coursework
 * April 2025
 */
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

