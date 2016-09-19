package client; 

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;

import javax.swing.*; 
import javax.swing.border.BevelBorder;

class myJPanel extends JPanel { 
	boolean m_occupied; 
	int color; // we'll use black = -1, none = 0, white = 1; 
	JLabel mylabel = new JLabel ("poop");
	
	public myJPanel() { 
		this.m_occupied = false;
		this.color = 0;
	}
	
	// this is a copy constructor that will be used to copy all the elements from one
	// of myJPanel's to another, except for the image
	public myJPanel(myJPanel opposite) {
		this.m_occupied = opposite.m_occupied;
		this.color = opposite.color * (-1);
	}
	
	public void addLabel(JLabel temp)
	{
		this.remove(mylabel);
		mylabel = new JLabel(temp.getIcon(), JLabel.CENTER);
		this.add(mylabel,BorderLayout.CENTER);
	}
	
	public void doAnnoyingStuff()
	{
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.black, Color.black, Color.black, Color.black));
        this.setBackground( Color.LIGHT_GRAY );
	}
}

public class ReversiInterface extends JFrame implements MouseListener {
	
	int PORT = 8080; 
	String IPADDRESS = "127.0.0.1"; 
	Socket clientSocket;
	ConnectionThread connection;
	OutputStream outstream; 
	InputStream instream;
	Semaphore boardSem = new Semaphore(1, true);
	
	// This is for the board/game
	JPanel panel; 
	JPanel sidePanel;
	myJPanel [][] board = new myJPanel[8][8]; 
	JLabel piece; 
	int xAdjustment; 
	int yAdjustment;
	int squareLength = 75;
	int x = 0;
	int movelist[] = {0,0};
	JLabel whitePlayer; 
	JLabel blackPlayer; 
	myJPanel who_turn = new myJPanel();
	int boardType; 
	int pieceColor  = 0;
	int num_white = 0;
	int num_black = 0;
	
	JLabel black =  new JLabel( new ImageIcon("images/blackPiece.png"), JLabel.CENTER);
	JLabel white=  new JLabel(new ImageIcon("images/whitePiece.png"), JLabel.CENTER);
	
	// the following is a method that is used to write to the socket!!!!
	public void SendString(String s)
	{
		try
		{
			outstream.write(s.getBytes());
			System.out.println("Sent:"+s);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	class ConnectionThread extends Thread { 
		public ConnectionThread()
		{
			
		}
		
		// i think this will make it so we can always be reading from the socket
		public void run()
		{
			
			try
			{
				int i =1;
				while(true)
				{
					byte[] readBuffer = new byte[256];

					instream.read(readBuffer);
					
					String decoded = new String(readBuffer);
					System.out.println("Got:" + decoded);
					if (i == 1) { 
						i++; 
						setTitle(decoded);
						if (decoded.contains("WHITE")) { 
							boardType = 1;
						}
						else if (decoded.contains("BLACK")) { 
							boardType = -1;
							// signal semaphore here
							/*
							try {
						        System.out.println("I'm about to block my board!");
						        //boardSem.acquire();
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							} */
						}
						
					}
					else if(decoded.contains("Pass")) { 
				        //System.out.println("I'm about to unblock my board!");
				        //boardSem.release();
				
						System.out.println("The other player passed their turn!");
						x++;
						changeTurn(x);
						
					}
					else if (decoded.contains("EndW")) { 
						// we need to show the right message and then end the game
						JOptionPane.showMessageDialog(null, "Congratulations, you won!");
						System.exit(0);
					}
					else if (decoded.contains("EndL")) {
						JOptionPane.showMessageDialog(null, "Sorry, you lost!");
						System.exit(0);
					}
					else { 
						
						char xCoordinate = 0;
						char yCoordinate = 0; 
						xCoordinate = decoded.charAt(0);
						yCoordinate = decoded.charAt(1);
						
						int xCo = Character.getNumericValue(xCoordinate);
						int yCo = Character.getNumericValue(yCoordinate);
						
						// we have gotten coordinates from the other player, and we need to set these coordinates
						setCoordinates(xCo, yCo);
					}
				}				
			}
			catch (Exception e)
			{
				System.out.println(e.toString());
			}	
				
		}
		
	}
	
	void endOtherGame() { 
		num_white = 0; 
		num_black = 0;
		
		for (int i = 0; i < 8; i++) { 
			for (int j = 0; j < 8; j++) { 
				if (board[i][j].color == 1) { 
        			num_white++;
        		}
        		else if (board[i][j].color == -1) { 
        			num_black++; 
        		}
			}
		}
		System.out.println("W" + num_white + " " + "B" + num_black);
		
		boolean done = ((num_white+num_black) == 64);
		boolean white_over_black = (num_white > num_black);
		
		
		
		if(num_white == 0 && boardType == 1)
		{
			System.out.println("1");
			//Print You lose message (white)
			JOptionPane.showMessageDialog(null, "Sorry, you lost!");
			System.exit(0);
		}
		else if(num_white == 0 && boardType == -1)
		{
			System.out.println("2");
			//print You win message(black)
			JOptionPane.showMessageDialog(null, "Congratulations, you won!");
			System.exit(0);
		}
		else if(num_black == 0 && boardType == 1)
		{
			System.out.println("3");
			//print You win message(white)
			JOptionPane.showMessageDialog(null, "Congratulations, you won!");
			System.exit(0);
		}
		else if(num_black == 0 && boardType == -1)
		{
			System.out.println("4");
			//Print You lose message (black)
			JOptionPane.showMessageDialog(null, "Sorry, you lost!");
			System.exit(0);
		}
	
		if(done && white_over_black && boardType == 1)
		{
			System.out.println("5");
			//print You win message(white)
			JOptionPane.showMessageDialog(null, "Congratulations, you won!");
			System.exit(0);
		}
		else if(done && white_over_black && boardType == -1)
		{
			System.out.println("6");
			//Print You lose message (black)
			JOptionPane.showMessageDialog(null, "Sorry, you lost!");
			System.exit(0);
		}
		else if(done && !white_over_black && boardType == 1)
		{
			System.out.println("7");
			//Print You lose message (white
			JOptionPane.showMessageDialog(null, "Congratulations, you won!");
			System.exit(0);
		}
		else if(done && !white_over_black && boardType == -1)
		{
			System.out.println("8");
			//print You win message(black)
			JOptionPane.showMessageDialog(null, "Sorry, you lost!");
			System.exit(0);
		}
		else { 
			System.out.println("9");
			// this else is in case the person has quit, then the other person will get a you win!
			JOptionPane.showMessageDialog(null, "Congratulations, you won!");
			System.exit(0);
		}
	}
	
	void setCoordinates(int xx, int yy) { 
		int xCo = xx;
		int yCo = yy;
		System.out.println( xCo + " " + yCo);
		int num_white = 0;
		int num_black = 0;
		int pieceColor  = 0;
		
		if (x%2 == 0) { 
			pieceColor = 1;
		}
		else if (x%2 == 1) { 
			pieceColor = -1;
		}

		boolean valid = ValidMove(pieceColor,xCo,yCo);
		
		System.out.print("The other player made a move   "); 
		System.out.println(valid + " x:"+xCo+" Y:"+yCo);
		
		if(valid)
		{
			if (board[xCo][yCo].m_occupied == false) { 
				board[xCo][yCo].m_occupied = true;
				
				// if its white's turn
				if (x%2 == 0) { 
					board[xCo][yCo].color = 1;			// set the color at the tile
					board[xCo][yCo].addLabel(white);	// set the label on the tile 
				}
				// if its black's turn
				else if (x%2 == 1) { 
					board[xCo][yCo].color = -1;			// set the color at the tile
					board[xCo][yCo].addLabel(black);	// set the label on the tile
				}
				
				x++; // give the turn to the other color
				changeTurn(x);
				
				board[xCo][yCo].doAnnoyingStuff(); 		// this adds the new picture to the previously empty space
		        
				// remove all the panels so they can be replaced
		        panel.removeAll();
		        num_white = 0; 
		        num_black = 0; 
		        
		        // then add everything back on
		        for (int a = 0; a < 8; a++) { 
		        	for (int b = 0; b < 8; b++) { 
		        		if (board[a][b].color == 1) { 
		        			num_white++;
		        		}
		        		else if (board[a][b].color == -1) { 
		        			num_black++; 
		        		}
		        		
		        		panel.add(board[b][a]);
		        	}
		        }
		        
		        whitePlayer.setText("White: " + num_white); 
		        blackPlayer.setText("Black: " + num_black);
		        panel.updateUI();
		        
		        System.out.println("I'm about to give unblock my board!");
		        //boardSem.release();
			}
		}
	}
	
	public ReversiInterface() { 
		// CLIENT CONNECTION CODE *******************************
		setTitle("Connecting ..... ");
		// connect the client socket 
		try {
			
			clientSocket = new Socket(IPADDRESS, PORT);
			
			outstream = clientSocket.getOutputStream();	// this just create the object so we can use outstream.write();
			instream = clientSocket.getInputStream(); 	// this just creates the object so we can use instream.read();
			connection = new ConnectionThread();
			connection.start(); // calls run, overridden in the connectionThread class
			System.out.println("Connected..");
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}
		
		// THE CONNECTION CODE IS UP HERE ^^ ******************
		
		System.out.println("The elves are making the game....");
		
		Dimension size = new Dimension(600, 600);
		panel = new JPanel(); 
		panel.setSize(size);
		panel.addMouseListener(this);
		
		sidePanel = new JPanel(); 
		Dimension size2 = new Dimension (150, 600);
		sidePanel.setSize(size2);
		
		panel.setLayout(new GridLayout(8,8));
		panel.setPreferredSize(size);
		panel.setBounds(0,0,size.width, size.height); 

		// button stuff for sidePanel
		JButton button1 = new JButton ("Pass The Turn");
		JButton button2 = new JButton ("Forfeit"); 
		JLabel turn = new JLabel ("Whose Turn: "); 
		
		who_turn.add(new JLabel("white"));
		whitePlayer = new JLabel("White: 2"); 
		blackPlayer = new JLabel("Black: 2");
		
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
		sidePanel.add(button1);
		button1.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				// if they click this button, the code should be passed to the mouse click
				// signal the other board! 
				if (x%2 == 0) pieceColor = 1; 
				else if (x%2 == 1) pieceColor = -1; 
				
				if (pieceColor != boardType) { 
					JOptionPane.showMessageDialog(null, "You can only pass the turn when its your turn");
				}
				
				else { 
					x++; 					// note: x is a global variable so it can be changed here 
					changeTurn(x);
					SendString("Pass");
					
				}
				
				/*try {
			        System.out.println("I'm about to block my board!");
			        boardSem.acquire();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} */
				
			}
		});
		sidePanel.add(button2);
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				// send something to the server so it can close these games..
				
				
				if (x%2 == 0) pieceColor = 1; 
				else if (x%2 == 1) pieceColor = -1; 
				
				if (pieceColor != boardType) { 
					JOptionPane.showMessageDialog(null, "You can only quit when its your turn");
				}
				
				else {
					SendString("EndW"); 
					JOptionPane.showMessageDialog(null, "Sorry, you lost!");
					System.exit(0);
				}
			}
		});
		sidePanel.add(whitePlayer); 
		sidePanel.add(blackPlayer);
		sidePanel.add(turn);
		sidePanel.add(who_turn);
		
		getContentPane().add(sidePanel);
		getContentPane().add(panel);
		
		for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
            	board[i][j] = new myJPanel();
            	
            	if ((i == 3 && j == 3) || (i == 4 && j == 4)) { 
            		
            		board[i][j].addLabel(white);
            		board[i][j].m_occupied = true;
            		board[i][j].color = 1;
            	}
            	if ((i == 4 && j == 3) || (i == 3 && j == 4)) { 
      
            		board[i][j].addLabel(black);
            		board[i][j].m_occupied = true;
            		board[i][j].color = -1;
            	}
            	
                board[i][j].doAnnoyingStuff();
                
                panel.add(board[i][j]);
            }
        }
	
		getContentPane().add(panel);
        
	}
	
	public boolean ValidMove(int color, int x, int y)
	{
		boolean isValid = false;
		//list of all the movesets available
		int movesetX[] = { 1, 1, 1, 0, -1,-1, -1, 0};
		int movesetY[] = { 1, 0, -1,-1,-1, 0, 1, 1};
		
		
		//movesetlist going to be used later to help flip pieces
		
		
		//I am going to try all directions
		for(int i = 0 ; i < 8; i++)
		{
			if(goDirection(color, x ,y ,movesetX[i],movesetY[i]))
			{
				fliperoni(x,y,movelist, color);
				isValid = true;
			}
		}
		return isValid;
	}
	
	public void checkWinner() 
	{
		boolean done = ((num_white+num_black) == 64);
		boolean white_over_black = (num_white > num_black);
		
		if (num_white == 0 || num_black == 0 || done) 
		{ 
			
			if(num_white == 0 && boardType == 1)
			{
				SendString("EndW"); // send to the other person they won
				//Print You lose message (white)
				JOptionPane.showMessageDialog(null, "Sorry, you lost!");
				System.exit(0);
			}
			else if(num_white == 0 && boardType == -1)
			{
				SendString("EndL");
				//print You win message(black)
				JOptionPane.showMessageDialog(null, "Congratulations, you won!");
				System.exit(0);
			}
			else if(num_black == 0 && boardType == 1)
			{
				SendString("EndL");
				//print You win message(white)
				JOptionPane.showMessageDialog(null, "Congratulations, you won!");
				System.exit(0);
			}
			else if(num_black == 0 && boardType == -1)
			{
				SendString("EndW");
				//Print You lose message (black)
				JOptionPane.showMessageDialog(null, "Sorry, you lost!");
				System.exit(0);
			}
		
			else if(done && white_over_black && boardType == 1)
			{
				SendString("EndL");
				//print You win message(white)
				JOptionPane.showMessageDialog(null, "Congratulations, you won!");
				System.exit(0);
			}
			else if(done && white_over_black && boardType == -1)
			{
				SendString("EndW");
				//Print You lose message (black)
				JOptionPane.showMessageDialog(null, "Sorry, you lost!");
				System.exit(0);
			}
			else if(done && !white_over_black && boardType == 1)
			{
				SendString("EndL");
				//Print You lose message (white
				JOptionPane.showMessageDialog(null, "Congratulations, you won!");
				System.exit(0);
			}
			else if(done && !white_over_black && boardType == -1)
			{
				SendString("EndW");
				//print You win message(black)
				JOptionPane.showMessageDialog(null, "Sorry, you lost!");
				System.exit(0);
			}
		}
	}

	public boolean goDirection(int color, int x, int y, int dirX, int dirY)
	{	
		movelist[0] = 0;
		movelist[1] = 0;
		
		// +1 in a certain direction
		x += dirX;
		y += dirY;
		
		movelist[0]+= dirX;
		movelist[1]+= dirY;
		
		// Need to be touching at least 1 piece of opposite colour, If I DONT return false
		if((!((x < 0)||(x > 7)||(y < 0)||(y >7))))
		{
			if(board[x][y].color != color*(-1))
			{
				return false;
			}
		}
		
		while(!((x < 0)||(x > 7)||(y < 0)||(y >7)))
		{
			if(board[x][y].m_occupied == false)
				return false;
			
			if(board[x][y].color == color)
				return true;
			
			x += dirX;
			y += dirY;
			movelist[0]+= dirX;
			movelist[1]+= dirY;
		}
		return false;
	}
	
	public void fliperoni(int x, int y, int[] list, int color)
	{
		
		int largest = 0;
		if( Math.abs(list[0]) > Math.abs(list[1]))
			largest = Math.abs(list[0]);
		else
			largest = Math.abs(list[1]);
		
		int xUnit = 0;
		int yUnit = 0;
		
		if(largest != 0)
		{
			xUnit = list[0]/largest;
			yUnit = list[1]/largest;
			
			for(int i = 0 ; i <largest; i++)
			{
				x+=xUnit;
				y+=yUnit;
				if(color == 1)
				{
					board[x][y].addLabel(white);
					board[x][y].color = 1;
			
				}
				if(color == -1)
				{
					board[x][y].addLabel(black);
					board[x][y].color = -1;
			
				}
				board[x][y].doAnnoyingStuff();
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		
		System.out.println("Clicked at " + e.getX() + " and " + e.getY());

		int i = (e.getX() / squareLength);
		int j = (e.getY() / squareLength); 
		
		if (x%2 == 0) { 
			pieceColor = 1;
		}
		else if (x%2 == 1) { 
			pieceColor = -1;
		}
		
		if ((pieceColor != boardType) || (board[i][j].m_occupied == true)) { 
			System.out.println("Do nothing");
			return;
		}
		
		boolean valid = ValidMove(pieceColor,i,j);
		System.out.println(valid + " x:"+i+" Y:"+j);
		
		if(valid)
		{
			if (board[i][j].m_occupied == false) { 
				board[i][j].m_occupied = true;
				
				// if its white's turn
				if (x%2 == 0) { 
					board[i][j].color = 1;			// set the color at the tile
					board[i][j].addLabel(white);	// set the label on the tile 
				}
				// if its black's turn
				else if (x%2 == 1) { 
					board[i][j].color = -1;			// set the color at the tile
					board[i][j].addLabel(black);	// set the label on the tile
				}
				
				x++; // give the turn to the other color
				changeTurn(x);
				
				board[i][j].doAnnoyingStuff(); 		// this adds the new picture to the previously empty space
		        
				// remove all the panels so they can be replaced
		        panel.removeAll();
		        num_white = 0; 
		        num_black = 0; 
		        
		        // then add everything back on
		        for (int a = 0; a < 8; a++) { 
			        	for (int b = 0; b < 8; b++) { 
			        		if (board[a][b].color == 1) { 
			        			num_white++;
			        		}
			        		else if (board[a][b].color == -1) { 
			        			num_black++; 
			        		}
			        		
			        		panel.add(board[b][a]);
			        	}
		        }
		        
		        whitePlayer.setText("White: " + num_white); 
		        blackPlayer.setText("Black: " + num_black);
		        panel.updateUI();
		        
				checkWinner();
			
				// at the end, send the coordinates to the bitches on the other side (aka Banting) 	
				SendString(Integer.toString(i));
				SendString(Integer.toString(j));
		       
		        /*
				try {
			        System.out.println("I'm about to block my board!");
			        boardSem.acquire();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} */
			}
		}
	}
	
	public void changeTurn(int turn) 
	{ 
		who_turn.removeAll();
		if (turn%2 == 0) 
			who_turn.add(new JLabel("white"));
		
		else 
			who_turn.add(new JLabel("black"));
		
		sidePanel.updateUI();
	}
	
	
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {	}
	public void mouseExited(MouseEvent e) {}
}

