// another reversi server thread! THIS IS FOR THE GAMEPLAY BETWEEN TWO SOCKETS!!

// ** PUT INCLUDES HERE 

#include "ReversiGameThread.h"
#include "socket.h"
#include "socketserver.h" 
#include "thread.h"

#include <iostream>

using namespace std;

ReversiGameThread::ReversiGameThread(Socket &player1, Socket &player2, SocketServer socket_server) 
	: m_player1(player1), m_player2(player2), m_socketServer(socket_server)
{ 
	Start(); 
}

long ReversiGameThread::ThreadMain (void) 
{ 
	int turn = 0; // this is a variable that counts whose turn it is!!! !!!!!!!!!!!!!!!!
	// this is the thread that runs all communication between the two clients for the game!
	// note: player1's socket is m_player1, and player2's socket is m_player1
	cout << " GAME IS SETTING UP " << endl;

	m_player1.Write(ByteArray("Player 1 -- WHITE"));
	sleep(1);
	m_player2.Write(ByteArray("Player 2 -- BLACK"));

	ByteArray xcoor; 
	ByteArray ycoor; 

	for(;;)
    {
    	// player 1
    	if ((turn%2) == 0) 
    	{ 
    		turn++; 
    		int read1 = m_player1.Read(xcoor);
    		if (xcoor.ToString() == "Pass") { 
    			cout << " passing turn " << endl; 
    			ByteArray temp("Pass");
    			m_player2.Write(temp);
    			continue; 
    		}
    		if (xcoor.ToString() == "EndL" || xcoor.ToString() == "EndW") { 
    			cout << " the game is over!! " << endl;
    			if (xcoor.ToString() == "EndL") { 
    				ByteArray temp2("EndL");
    				m_player2.Write(temp2); 
    			} 
    			else { 
    				ByteArray temp2("EndW");
    				m_player2.Write(temp2); 
    			} 
    			break;
    		}
        	int read2 = m_player1.Read(ycoor);

        	if (read1 == -1 || read2 == -1)
        	{
	            cout << "Error in socket detected" << endl;
	            break;
	        }
	        else if (read1 == 0 || read2 == 0)
	        {
	            cout << "Socket closed at remote end" << endl;
	            break;
	        }
	        else
	        {
	            string x_str = xcoor.ToString();
	            cout << "Received: " << x_str << endl;
	            //int x = atoi(x_str.c_str());
	            //cout << "Converted x to int: " << x << endl; 

	            string y_str = ycoor.ToString();
	            cout << "Received: " << y_str << endl;
	            //int y = atoi(y_str.c_str());
	            //cout << "Converted y to int: " << y << endl; 

	            // since this is for player 1, we send to player 2 
	            m_player2.Write(xcoor);
	            m_player2.Write(ycoor);

	        }
    	}
        
        // player 2
        else if ((turn%2) == 1) 
        { 
        	turn++; 
           	int read1 = m_player2.Read(xcoor);
           	if (xcoor.ToString() == "Pass") { 
    			cout << " passing turn " << endl; 
    			ByteArray temp("Pass");
    			m_player1.Write(temp);
    			continue; 
    		}
    		if (xcoor.ToString() == "EndL" || xcoor.ToString() == "EndW") { 
    			cout << " the game is over!! " << endl;
    			if (xcoor.ToString() == "EndL") { 
    				ByteArray temp2("EndL");
    				m_player1.Write(temp2); 
    			} 
    			else { 
    				ByteArray temp2("EndW");
    				m_player1.Write(temp2); 
    			} 
    			break;
    		}
        	int read2 = m_player2.Read(ycoor);

        	if (read1 == -1 || read2 == -1)
	        {
	            cout << "Error in socket detected" << endl;
	            break;
	        }
	        else if (read1 == 0 || read2 == 0)
	        {
	            cout << "Socket closed at remote end" << endl;
	            break;
	        }
	        else
	        {
	            string x_str = xcoor.ToString();
	            cout << "Received: " << x_str << endl;
	            //int x = atoi(x_str.c_str());
	            //cout << "Converted x to int: " << x << endl; 

	            string y_str = ycoor.ToString();
	            cout << "Received: " << y_str << endl;
	            //int y = atoi(y_str.c_str());
	            //cout << "Converted y to int: " << y << endl; 

	            // since this is for player 2, we send to player 1
	            m_player1.Write(xcoor);
	            m_player1.Write(ycoor);
	        }
        }
    }

	// we need to have a of objects that will basically store all the knowledge of the board.
	cout << " Closing this game thread!" << endl; 
}

ReversiGameThread::~ReversiGameThread() { 
	cout << " Game thread destructor called" << endl;
	//terminationEvent.Wait();
	m_player1.Write(ByteArray("EndW"));
	m_player2.Write(ByteArray("EndW"));
	terminationEvent.Wait();
	m_player1.Close();
	m_player2.Close();
}


