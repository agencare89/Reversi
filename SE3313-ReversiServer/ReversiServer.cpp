// server codeeeeeee 

// put includes here 
#include "ReversiGameThread.h"
#include "socket.h"
#include "socketserver.h" 
#include "thread.h"

#include <stdlib.h>
#include <iostream>
#include <vector> 

using namespace std; 

int main (void) 
{ 
	cout << " --------------------------- SERVER IS RUNNING ------------------------------- " << endl;  
	cout << " Type quit to exit at any point. Note: Quiting will terminate ALL reversi games" << endl;

	Socket * player1;
	Socket * player2;

	string check = ""; 
	SocketServer socket_server(8080); 		// create a socket server that runs on the port 8080
	vector <ReversiGameThread*> allGames;	// create a vector to store all running games!
	int i = 0;

	while (true) { 
		try { 

			FlexWait flex(2, &socket_server, &cinWatcher);
			Blockable* block = flex.Wait();

			if (block == &cinWatcher)  {
				getline(cin, check); 

				if (check == "quit") { 
					break;
				}

				else
					continue;
			}

			// so at this point, the flex wait was broken but it wasn't for a cin, therefore someone connected!
			if (((i+1)%2) == 1) { 
				player1 = new Socket(socket_server.Accept());
				cout << " Player one has arrived " << endl; 
			}

			
			else if ((i+1)%2 == 0) { 
				player2 = new Socket(socket_server.Accept());
				allGames.push_back(new ReversiGameThread(*player1, *player2, socket_server));
				cout << "Number of Games going: " << allGames.size() << endl;
			}  

			i++;

		} // end try block

		catch(TerminationException e) {
            cout << "The socket server is no longer listening. Exiting now." << endl;
            break;
        }

        catch(std::string s) {
            cout << "thrown " << s << endl;
            break;
        }

        catch(...) {
            cout << "caught  unknown exception" << endl;
            break;
        }

	} // end while loop

	// if we reach this point, we know that we got a quit request.. all games in the vector 
	// must be terminated! 
	for (int i = 0; i < allGames.size(); i++) { 
		delete allGames[i]; 
	}


	delete player1; 
	delete player2; 
	player1 = NULL; 
	player2 = NULL; 

	cout << " All running games were removed !" << endl; 

	sleep(2);

	cout << " SERVER IS DONE " << endl;
	// still need to put this thread to sleep!

} // end the main


