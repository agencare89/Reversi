package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class MainReversi { 
	static JFrame mainFrame; 	// frame for the main screen
	
	public static void main (String [] args) { 
						
		JButton quitGameButton;
		JButton newGameButton;
		
		mainFrame = new JFrame("Reversi Main Screen"); 
		mainFrame.setSize(400, 200);
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		//mainFrame.pack();
		
		mainFrame.setLocationRelativeTo(null);
		
		JPanel reversiTitle = new JPanel(new FlowLayout(FlowLayout.CENTER));
		reversiTitle.setBackground(Color.white);
		reversiTitle.setSize(200, 600);
		
		JLabel pictureLabel = new JLabel(new ImageIcon("images/reversi_mainscreen.png"), JLabel.CENTER);
		pictureLabel.setSize(200, 600);
		
		reversiTitle.add(pictureLabel);
		reversiTitle.setBorder(BorderFactory.createTitledBorder(""));
    	
    	mainFrame.add(reversiTitle, BorderLayout.NORTH);
    	
    	newGameButton = new JButton("Start A Game");
    	quitGameButton = new JButton("Quit"); 
    	
    	JPanel startPanel = new JPanel();
    	startPanel.setSize(200, 600);
    	startPanel.add(newGameButton);
    	
    	JPanel quitPanel = new JPanel();
    	quitPanel.setSize(200, 600);
    	quitPanel.add(quitGameButton, BorderLayout.AFTER_LAST_LINE); 
    	
    	mainFrame.add(startPanel);
    	mainFrame.add(quitPanel, BorderLayout.AFTER_LAST_LINE);
    	
    	mainFrame.setVisible(true);
    	
    	newGameButton.addActionListener( new ActionListener()
    	{
    	    public void actionPerformed(ActionEvent e)
    	    {
    	        System.out.println("Starting game... ");
    	    	// this is going to be put into the start game button action listener
    	    	JFrame frame = new ReversiInterface();  
    			frame.setSize(800, 800);
    			frame.setLayout(new GridBagLayout()); 
    			frame.setResizable(false); 
    			frame.pack(); 
    			frame.setLocationRelativeTo( null );
    			frame.setVisible(true);
    	    }
    	});
    	
    	quitGameButton.addActionListener (new ActionListener() 
    	{ 
    		public void actionPerformed (ActionEvent e) 
    		{ 
    			System.out.println("Closing game...");
    			closeFrame();
    		}
    	});

	}
	
	public static void closeFrame() { 
		mainFrame.dispose();
	}
}
		
		
		


