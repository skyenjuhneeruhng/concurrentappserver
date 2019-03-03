package hun;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;

public class AppServer implements Runnable , MsgListener , ErrorListener{
	
	public static final String SERVER_ADDR = "192.168.1.200";
	public static final int SERVER_PORT = 5678;
	/*server socket*/
	private ServerSocket server;
	
	/*client sockets*/
	private Hashtable<String , AppClient> clients;
	
	/*indicates that server is running*/
	private boolean serverStatus = false;
	
	
	/*interfaces for communication with gui*/
	private ErrorListener el;
	public void setErrorListener(ErrorListener el)
	{
		this.el = el;
	}
	private MsgListener ml;
	public void setMsgListener(MsgListener ml)
	{
		this.ml = ml;
	}
	
	public AppServer()
	{
		clients = new Hashtable<String , AppClient>();
	}
	
	/*start server*/
	public void startServer()
	{
		Command com = new Command();
		com.put("req", "Server started");
		ml.msgReceived(com , "server");
		try{
			server = new ServerSocket(SERVER_PORT);
			serverStatus = true;
			Thread serverThread = new Thread(this);
			serverThread.start();
		}
		catch(IOException e){
			server = null;
			System.out.println("Can not start server : " + e.getMessage());
			el.errorOccured("Can not start server : " + e.getMessage() , "Server");
		}
	}
	
	/*Overrided method from thread*/
	public void run()
	{
		System.out.println("Server thread started.");
		while(serverStatus)
		{
			try {
				Socket socket = server.accept();
				acceptNewClient(socket);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(!server.isClosed())
				{
					System.out.println("An error occured when accepting client : " + e.getMessage());
					el.errorOccured("An error occured when accepting client : " + e.getMessage() , "Server");
				}
			}
		}
		System.out.println("Server thread stopped.");
	}
	
	public void acceptNewClient(Socket socket)
	{
		String ip = socket.getInetAddress().toString();
		AppClient client = new AppClient(socket);
		client.setErrorListener(this);
		client.setMsgListener(this);
		clients.put(ip , client);
		Command com = new Command();
		com.put("req", "Client Connected");
		ml.msgReceived( com, ip);
	}
	
	/*stop server*/
	public void stopServer()
	{
		/*first stop accepting*/
		serverStatus = false;
		
		/*close all clients*/
		Enumeration<String> keys = clients.keys();
		while(keys.hasMoreElements())
		{
			String ip = keys.nextElement();
			AppClient client = clients.get(ip);
			client.disconnectClient();
			clients.remove(ip);
			client = null;
			
		}
		/*******************************/
		
		/*close server*/
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("An error occured when closing server : " + e.getMessage());
			el.errorOccured("An error occured when closing server : " + e.getMessage() , "Server");
		}
		Command com = new Command();
		com.put("req", "Server stopped");
		ml.msgReceived(com, "server");
	}
	
	/*handler for errors from app client*/
	@Override
	public void errorOccured(String msg, String ip) {
		// TODO Auto-generated method stub
		el.errorOccured(msg, ip);
		AppClient client = clients.get(ip);
		if(!client.isClientClosed())
			client.disconnectClient();
		clients.remove(ip);
		client = null;
	}

	/*handler for msg from app client*/
	@Override
	public void msgReceived(Command msg, String ip) {
		// TODO Auto-generated method stub
		ml.msgReceived(msg, ip);
	}
	
	public boolean isServerClosed()
	{
		if(server == null)
			return true;
		else 
			return server.isClosed();
	}
	
	public void sendMsg(Command com)
	{
		Enumeration<String> keys = clients.keys();
		while(keys.hasMoreElements())
		{
			AppClient client = clients.get(keys.nextElement());
			client.send(com);
		}
	}
}
