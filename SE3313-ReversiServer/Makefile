ReversiServer : ReversiServer.o Blockable.o thread.o socket.o socketserver.o ReversiGameThread.o
	g++ -o ReversiServer ReversiServer.o Blockable.o thread.o socket.o socketserver.o ReversiGameThread.o -pthread -l rt

ReversiServer.o : ReversiServer.cpp ReversiGameThread.h socket.h socketserver.h thread.h
	g++ -c ReversiServer.cpp

Blockable.o : Blockable.h Blockable.cpp
	g++ -c Blockable.cpp

thread.o : thread.cpp thread.h
	g++ -c thread.cpp

socket.o : socket.cpp socket.h
	g++ -c socket.cpp

socketserver.o : socketserver.cpp socket.h socketserver.h
	g++ -c socketserver.cpp

ReversiGameThread.o : ReversiGameThread.cpp ReversiGameThread.h socket.h socketserver.h thread.h
	g++ -c ReversiGameThread.cpp 
