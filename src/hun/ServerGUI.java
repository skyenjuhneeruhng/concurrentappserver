package hun;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Date;
import java.util.Vector;

import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


public class ServerGUI implements MsgListener, ErrorListener , WindowListener{

	/*components for Server user interface*/
	private JFrame mainFrame;
	private JTextArea historyArea;
	private JLabel counter , roundTitle , roundDescription , roundPrice , roundIP;
	private JButton submitBtn , startBtn , stopBtn , clearBtn;
	//	private JTextField ;
	private JTextField productTitle , productInitialPrice , productTimeLimit;
	private JTextArea productDescription;

	/*Server*/
	private AppServer server;

	/*auction history*/
	private Auction auction;
	private int auctionId = 0;

	public ServerGUI()
	{
		initApp();
		configureServer();
	}
	private void configureServer()
	{
		this.server = new AppServer();
		/*setting server interfaces*/
		this.server.setErrorListener(this);
		this.server.setMsgListener(this);
	}
	private void initApp()
	{
		mainFrame = new JFrame("ServerApp");
		mainFrame.addWindowListener(this);
		Container container = mainFrame.getContentPane();
		container.setLayout(new BorderLayout(10 , 10));
		JPanel con = new JPanel(new BorderLayout(10 , 10));
		con.setBorder(new EmptyBorder(10 , 10 , 10 , 10));
		container.add(con , BorderLayout.CENTER);

		/*bottom panel begine*/
		JPanel bottomPanel = new JPanel(new FlowLayout());
		startBtn = new JButton("Start");
		startBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(server.isServerClosed())
					server.startServer();
			}
		});
		stopBtn = new JButton("Stop");
		stopBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(auction != null)
					auction.stop();
				if(!server.isServerClosed())
					server.stopServer();
			}
		});
		clearBtn = new JButton("Clear");
		clearBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				historyArea.setText("History goes here");
			}
		});
		bottomPanel.add(startBtn);
		bottomPanel.add(stopBtn);
		bottomPanel.add(clearBtn);
		con.add(bottomPanel , BorderLayout.SOUTH);
		/*bottom panel end*/

		/*left panel begin*/
		JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
		leftPanel.setPreferredSize(new Dimension(500 , 580));
		JPanel auctioneerPanel = new JPanel(new BorderLayout(5 , 5));
		productTitle = new JTextField("Product Title");
		auctioneerPanel.add(productTitle , BorderLayout.NORTH);
		productDescription = new JTextArea("Product Description");
		JScrollPane descWrapper = new JScrollPane(productDescription);
		auctioneerPanel.add(descWrapper , BorderLayout.CENTER);
		JPanel auctBottom = new JPanel(new GridLayout(3 , 1 , 5 , 5));
		productTimeLimit = new JTextField("Time Limit");
		productInitialPrice = new JTextField("Initial Price");
		submitBtn = new JButton("Submit");
		submitBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(!server.isServerClosed())
				{
					try{
						String title = productTitle.getText().toString();
						String description = productDescription.getText().toString();
						int timeLimit = Integer.parseInt(productTimeLimit.getText().toString());
						float initialPrice = Float.parseFloat(productInitialPrice.getText().toString());
						auctionId ++;
						auction = new Auction(timeLimit , server , title , description , auctionId , initialPrice);
						auction.setErrorListener(ServerGUI.this);
						auction.setMsgListener(ServerGUI.this);
						auction.start();
						
						//change servergui interface
						roundDescription.setText(description);
						roundIP.setText("0.0.0.0");
						roundPrice.setText("$" + initialPrice);
						roundTitle.setText(title);
					}
					catch(NumberFormatException e)
					{
						JOptionPane.showMessageDialog(mainFrame, "Please fill with number in Time Limit and Initial Price");
					}
				}
				else
				{
					JOptionPane.showMessageDialog(mainFrame, "Server has not been started");
				}
			}
		});
		auctBottom.add(productTimeLimit);
		auctBottom.add(productInitialPrice);
		auctBottom.add(submitBtn);
		auctioneerPanel.add(auctBottom , BorderLayout.SOUTH);

		JPanel auctionPanel = new JPanel(new GridLayout(5 , 1 , 5 , 5));
		counter = new JLabel("00.0");
		roundTitle = new JLabel("Shoes");
		roundDescription = new JLabel("This is the most famous trade mark.");
		roundPrice = new JLabel("$50");
		roundIP = new JLabel("192.168.1.200");
		auctionPanel.add(counter);
		auctionPanel.add(roundTitle);
		auctionPanel.add(roundDescription);
		auctionPanel.add(roundPrice);
		auctionPanel.add(roundIP);

		leftPanel.add(auctioneerPanel , BorderLayout.CENTER);
		leftPanel.add(auctionPanel , BorderLayout.NORTH);

		con.add(leftPanel , BorderLayout.WEST);
		/*left panel end*/

		/*right panel begin*/
		JPanel rightPanel = new JPanel(new BorderLayout(10 , 10));		
		historyArea = new JTextArea("History goes here.");
		JScrollPane historyWrapper = new JScrollPane(historyArea);
		rightPanel.add(historyWrapper , BorderLayout.CENTER);

		con.add(rightPanel , BorderLayout.CENTER);
		/*right panel end*/

		mainFrame.setSize(800 , 600);
		mainFrame.setVisible(true);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerGUI app = new ServerGUI();
	}

	/*error from appserver*/
	@Override
	public void errorOccured(String msg, String ip) {
		// TODO Auto-generated method stub
		//		JOptionPane.showMessageDialog(mainFrame, "From : " + ip + " Msg:" + msg);
		Date now = new Date();
		String nowTime = now.toString();
		historyArea.append('\n' + nowTime + ":");
		historyArea.append('\n' + msg);
		historyArea.append('\n' + "From : " + ip);

	}
	/*msg from appserver*/
	@Override
	public void msgReceived(Command msg, String ip) {
		// TODO Auto-generated method stub
		//		JOptionPane.showMessageDialog(mainFrame, "From : " + ip + " Msg:" + msg);
		Date now = new Date();
		String nowTime = now.toString();
		historyArea.append('\n' + nowTime + ":");
		historyArea.append('\n' + msg.toString());
		historyArea.append('\n' + "From : " + ip);
		
		String req = msg.get("req");
		String price = msg.get("price");
		if(req.equals("NEW"))
		{
			if(auction.getTimeRemain() > 0)
			{
				auction.setWinnerIp(ip);
				auction.setWinnerPrice(Float.parseFloat(price));
				
				//Also change servergui interface
				roundIP.setText(ip);
				roundPrice.setText(price);
			}
		}
		else if(req.equals("TIME"))
		{
			counter.setText(msg.get("time"));
		}
	}

	/*window state change listeners begin*/
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Window is closing.");
		if(!server.isServerClosed())
			server.stopServer();
		if(auction != null)
			auction.stop();
		System.exit(0);
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}
	/*window state change listeners end*/

}
