#include "SharedObject.h"
#include "Semaphore.h"
#include "thread.h"
#include "socketserver.h"
#include <stdlib.h>
#include <time.h>
#include <list>
#include <pthread.h>
#include <vector>

class CommThread : public Thread
{
private:
    Socket theSocket;
public:
    CommThread(Socket const & p)
        : Thread(true),theSocket(p)
    {
        ;
    } 
    long ThreadMain(void)
    {
        ByteArray bytes;
        std::cout << "Created a socket thread!" << std::endl;
        for(;;)
        {
            int read = theSocket.Read(bytes);
            if (read == -1)
            {
                std::cout << "Error in socket detected" << std::endl;
                break;
            }
            else if (read == 0)
            {
                std::cout << "Socket closed at remote end" << std::endl;
                break;
            }
            else
            {
                std::string theString = bytes.ToString();
                std::cout << "Received: " << theString << std::endl;
                bytes.v[0]='R';
                theSocket.Write(bytes);
            }
        }
        std::cout << "Thread is gracefully ending" << std::endl;
    }
    ~CommThread(void)
    {
        theSocket.Write(ByteArray("done"));
        terminationEvent.Wait();
        theSocket.Close();
    }
};

int main(void)
{
    std::cout << "I am a socket server.  Type 'quit' to exit" << std::endl;
    SocketServer theServer(2000);
    std::vector<CommThread *> threads;

    for(;;)
    {
        try
        {
            FlexWait waiter(2,&theServer,&cinWatcher);
            Blockable * result = waiter.Wait();
            if (result == &cinWatcher)
            {
                std::string s;
                std::cin >> s;
                if (s=="quit")
                {
                    // No need to call SocketServer::Shutdown.  It isn't active.
                    break;
                }
                else
                    continue;
            }
            // Accept should not now block.
            Socket newSocket = theServer.Accept();
            std::cout << "Received a socket connection!" << std::endl;
            threads.push_back(new CommThread(newSocket));
        }
        catch(TerminationException e)
        {
            std::cout << "The socket server is no longer listening. Exiting now." << std::endl;
            break;
        }
        catch(std::string s)
        {
            std::cout << "thrown " << s << std::endl;
            break;
        }
        catch(...)
        {
            std::cout << "caught  unknown exception" << std::endl;
            break;
        }
    }
    for (int i=0;i<threads.size();i++)
        delete threads[i];

    std::cout << "Sleep now" << std::endl;
    sleep(1);
    std::cout << "End of main" << std::endl;

}
