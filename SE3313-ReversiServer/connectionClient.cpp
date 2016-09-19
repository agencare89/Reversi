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
				byte[] readBuffer = new byte[1024];
				while(true)
				{
					instream.read(readBuffer);
					String decoded = new String(readBuffer, "UTF-8");
					System.out.println("Got:" + decoded);
				}				
			}
			catch (Exception e)
			{
				System.out.println(e.toString());
			}	
				
		}
		
	}
	
	// this is a constructor that is called when we make the game, therefore
	// it is called when we click the start game button on a main interface. 

	// note: the first thing done in this constructor is the client connection code
	public ReversiInterface() { 
		// CLIENT CONNECTION CODE *******************************
		
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


// the remainder of the code is our client side game!