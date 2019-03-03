package hun;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class AppClient implements Runnable{

	public static final String SERVER_ADDR = "192.168.1.200";
	public static final int SERVER_PORT = 5678;

	private Socket client;
	private InputStream is;
	private OutputStream os;
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private boolean clientStatus = false;

	/*error listener for communication between appserver or user interface*/
	private ErrorListener el;
	public void setErrorListener(ErrorListener el)
	{
		this.el = el;
	}
	/*msg listener communication between appserver or user interface*/
	private MsgListener ml;
	public void setMsgListener(MsgListener ml)
	{
		this.ml = ml;
	}

	/*This constructor for client side program*/
	public AppClient()
	{

	}
	public void connect()
	{
		try {
			client = new Socket(SERVER_ADDR , SERVER_PORT);
			clientStatus = true;

			is = client.getInputStream();
			os = client.getOutputStream();
			oos = new ObjectOutputStream(os);
			ois = new ObjectInputStream(is);
			Thread clientThread = new Thread(this);
			clientThread.start();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occured when getting io streams : " + e.getMessage());
			el.errorOccured("Error occured when getting io streams : " + e.getMessage() , "Client");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occured : " + e.getMessage());
			el.errorOccured("Error occured : " + e.getMessage() , "Client");
		}
	}

	/*this constructor for serverside program*/
	public AppClient(Socket client)
	{
		this.client = client;
		this.clientStatus = true;
		try {
			is = client.getInputStream();
			os = client.getOutputStream();
			oos = new ObjectOutputStream(os);
			ois = new ObjectInputStream(is);
			Thread clientThread = new Thread(this);
			clientThread.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occured when getting io streams : " + e.getMessage());
			el.errorOccured("Error occured when getting io streams : " + e.getMessage() , "Client");
		}
	}


	/*overiding method for runnable , reading from client or server is here*/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Command command;
//			ObjectInputStream ois = new ObjectInputStream(is);
			System.out.println("ois");
			while(clientStatus && (command = (Command)ois.readObject()) != null)
			{
				System.out.println(command.toString());
				ml.msgReceived(command,client.getInetAddress().toString());
			}
			if(!isClientClosed())
			{
				disconnectClient();
			}
//			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("Error occured when reading via io streams : " + e.getMessage());
			el.errorOccured("Error occured when reading via io streams : " + e.getMessage() , client.getInetAddress().toString());
		}
	}

	/*send to client or send to server*/
	public void send(Command com)
	{
		try {
//			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(com);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occured when writing via io streams : " + e.getMessage());
			el.errorOccured("Error occured when writing via io streams : " + e.getMessage() , client.getInetAddress().toString());
//			disconnectClient();
		}
	}

	/*disconnect client*/
	public void disconnectClient()
	{
		clientStatus = false;
		try {
			client.close();
			Command com = new Command();
			com.put("req" , "Client disconnected");
			ml.msgReceived(com , client.getInetAddress().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occured when disconnecting : " + e.getMessage());
			el.errorOccured("Error occured when disconnecting : " + e.getMessage() , "Client");
		}
	}

	public boolean isClientClosed()
	{
		if(client == null)
			return true;
		else
			return client.isClosed();
	}

}
