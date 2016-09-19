// ReversiGameThread.h

#ifndef REVERSIGAMETHREAD_H
#define REVERSIGAMETHREAD_H


#include "ReversiGameThread.h"
#include "socket.h"
#include "socketserver.h" 
#include "thread.h"

//#include <vector>

class ReversiGameThread : public Thread
{ 
	private: 
		Socket m_player1;
		Socket m_player2;
		SocketServer m_socketServer; 
		//std::vector<ServerThread*> m_vector; 

	public: 
		ReversiGameThread(Socket &socket1, Socket &socket2, SocketServer socket_server);
		~ReversiGameThread();  
		//void AddToVector(void); 
		long ThreadMain(void);
}; 

#endif // REVERSIGAMETHREAD_H